/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.meeds.gamification.rest.builder;

import static io.meeds.gamification.plugin.ProgramTranslationPlugin.PROGRAM_DESCRIPTION_FIELD_NAME;
import static io.meeds.gamification.plugin.ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE;
import static io.meeds.gamification.plugin.ProgramTranslationPlugin.PROGRAM_TITLE_FIELD_NAME;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.UserInfo;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.rest.model.ProgramRestEntity;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

@SuppressWarnings("deprecation")
public class ProgramBuilder {

  private static final Log LOG = ExoLogger.getLogger(ProgramBuilder.class);

  private ProgramBuilder() {
    // Class with static methods
  }

  public static ProgramRestEntity toRestEntity(ProgramService programService, // NOSONAR
                                               RuleService ruleService,
                                               TranslationService translationService,
                                               UserACL userAcl,
                                               ProgramDTO program,
                                               Locale locale,
                                               String username,
                                               List<String> expandFields,
                                               boolean expandAdministrators) {
    if (program == null) {
      return null;
    }
    boolean anonymous = StringUtils.isBlank(username);
    translatedLabels(translationService, program, locale);

    int activeRulesCount = 0;
    if (CollectionUtils.isNotEmpty(expandFields)
        && (expandFields.contains("countActiveRules")
            || (expandFields.contains("countActiveRulesWhenDisabled") && !program.isEnabled()))) {
      activeRulesCount = ruleService.countActiveRules(program.getId());
    }

    List<UserInfo> owners = anonymous ? Collections.emptyList() :
                                      getProgramOwnersByIds(program.getOwnerIds(), program.getSpaceId());
    return new ProgramRestEntity(program.getId(), // NOSONAR
                                 program.getTitle(),
                                 program.getDescription(),
                                 program.getColor(),
                                 program.getSpaceId(),
                                 program.getPriority(),
                                 anonymous ? null : program.getCreatedBy(),
                                 program.getCreatedDate(),
                                 anonymous ? null : program.getLastModifiedBy(),
                                 program.getLastModifiedDate(),
                                 program.isDeleted(),
                                 program.isEnabled(),
                                 program.getBudget(),
                                 program.getType(),
                                 null,
                                 null,
                                 program.getCoverFileId(),
                                 program.getAvatarFileId(),
                                 program.getCoverUrl(),
                                 program.getAvatarUrl(),
                                 anonymous ? null :
                                           owners.stream().map(o -> o.getId()).map(Long::parseLong).collect(Collectors.toSet()),
                                 program.getRulesTotalScore(),
                                 program.isOpen(),
                                 buildSpace(program, anonymous),
                                 toUserContext(programService, program, username),
                                 owners,
                                 anonymous || !expandAdministrators ? null :
                                                                    buildAdministrators(programService, userAcl),
                                 activeRulesCount,
                                 program.getVisibility());
  }

  public static void translatedLabels(TranslationService translationService, ProgramDTO program, Locale locale) {
    if (program == null || locale == null) {
      return;
    }
    String translatedTitle = translationService.getTranslationLabel(PROGRAM_OBJECT_TYPE,
                                                                    program.getId(),
                                                                    PROGRAM_TITLE_FIELD_NAME,
                                                                    locale);
    if (StringUtils.isNotBlank(translatedTitle)) {
      program.setTitle(translatedTitle);
    }
    String translatedDescription = translationService.getTranslationLabel(PROGRAM_OBJECT_TYPE,
                                                                          program.getId(),
                                                                          PROGRAM_DESCRIPTION_FIELD_NAME,
                                                                          locale);
    if (StringUtils.isNotBlank(translatedDescription)) {
      program.setDescription(translatedDescription);
    }
  }

  public static List<ProgramRestEntity> toRestEntities(ProgramService programService, // NOSONAR
                                                       RuleService ruleService,
                                                       TranslationService translationService,
                                                       UserACL userAcl,
                                                       Locale locale,
                                                       List<ProgramDTO> programs,
                                                       List<String> expandFields,
                                                       String username) {
    return programs.stream()
                   .map(program -> toRestEntity(programService,
                                                ruleService,
                                                translationService,
                                                userAcl,
                                                program,
                                                locale,
                                                username,
                                                expandFields,
                                                false))
                   .toList();
  }

  public static List<String> buildAdministrators(ProgramService programService, UserACL userAcl) {
    if (userAcl == null || programService == null) {
      return Collections.emptyList();
    } else {
      List<String> administrators = programService.getAdministrators();
      String superUser = userAcl.getSuperUser();
      return administrators == null ? null :
                                    administrators.stream()
                                                  .filter(u -> !superUser.equals(u))
                                                  .toList();
    }
  }

  private static List<UserInfo> getProgramOwnersByIds(Set<Long> ids, long spaceId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceId > 0 ? spaceService.getSpaceById(String.valueOf(spaceId)) : null;
    return ids.stream()
              .distinct()
              .map(id -> {
                try {
                  Identity identity = identityManager.getIdentity(String.valueOf(id));
                  if (identity != null
                      && identity.isUser()
                      && identity.isEnable()
                      && !identity.isDeleted()
                      && (spaceId == 0
                          || (space != null
                              && !spaceService.isManager(space, identity.getRemoteId())
                              && spaceService.isMember(space, identity.getRemoteId())))) {
                    return toUserInfo(identity);
                  }
                } catch (Exception e) {
                  LOG.warn("Error when getting program owner with id {}. Ignore retrieving identity information", id, e);
                }
                return null;
              })
              .filter(Objects::nonNull)
              .toList();
  }

  public static UserInfoContext toUserContext(ProgramService programService,
                                              ProgramDTO program,
                                              String username) {
    UserInfoContext userContext = new UserInfoContext();

    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    Identity identity = StringUtils.isBlank(username) ? null : identityManager.getOrCreateUserIdentity(username);
    mapUserInfo(userContext, identity);
    if (program != null) {
      boolean anonymous = identity == null;
      boolean canView = programService.canViewProgram(program.getId(), username);
      boolean isOwner = !anonymous && programService.isProgramOwner(program.getId(), username);
      boolean isMember = !anonymous && programService.isProgramMember(program.getId(), username);
      boolean canEdit = !anonymous && isOwner && !program.isDeleted();
      userContext.setManager(isOwner);
      userContext.setCanEdit(canEdit);
      userContext.setMember(isMember);
      userContext.setCanView(canView);
      userContext.setProgramOwner(isOwner);

      if (program.isOpen()) {
        userContext.setRedactor(true);
      } else {
        Space space = Utils.getSpaceById(String.valueOf(program.getSpaceId()));
        if (space != null) {
          boolean isRedactor = !anonymous
                               && CommonsUtils.getService(SpaceService.class)
                                              .canRedactOnSpace(space, Utils.getUserAclIdentity(username));
          userContext.setRedactor(isRedactor);
        }
      }
    }
    return userContext;
  }

  private static UserInfo toUserInfo(Identity identity) {
    UserInfo userInfo = new UserInfo();
    mapUserInfo(userInfo, identity);
    return userInfo;
  }

  private static <E extends UserInfo> void mapUserInfo(E userInfo, Identity identity) {
    if (identity != null) {
      userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
      userInfo.setFullName(identity.getProfile().getFullName());
      userInfo.setRemoteId(identity.getRemoteId());
      userInfo.setId(identity.getId());
    }
  }

  private static Space buildSpace(ProgramDTO program, boolean anonymous) {
    if (anonymous || program.getSpaceId() <= 0) {
      return null;
    }
    Space space = Utils.getSpaceById(String.valueOf(program.getSpaceId()));
    if (space == null) {
      return null;
    }
    String[] spaceManagers = space.getManagers();
    if (ArrayUtils.isNotEmpty(spaceManagers)) {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      spaceManagers = Arrays.stream(spaceManagers).map(u -> {
        Identity identity = identityManager.getOrCreateUserIdentity(u);
        if (identity != null
            && identity.isUser()
            && identity.isEnable()
            && !identity.isDeleted()) {
          return u;
        } else {
          return null;
        }
      })
                            .filter(Objects::nonNull)
                            .toArray(String[]::new);
      space.setManagers(spaceManagers);
    }
    return space;
  }

}
