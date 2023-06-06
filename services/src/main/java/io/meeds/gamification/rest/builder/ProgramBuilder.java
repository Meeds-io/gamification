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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.utils.CommonsUtils;
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
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;

@SuppressWarnings("deprecation")
public class ProgramBuilder {

  private static final Log LOG = ExoLogger.getLogger(ProgramBuilder.class);

  private ProgramBuilder() {
    // Class with static methods
  }

  public static ProgramRestEntity toRestEntity(ProgramService programService,
                                               TranslationService translationService,
                                               ProgramDTO program,
                                               Locale locale,
                                               String username) {
    if (program == null) {
      return null;
    }
    translatedLabels(translationService, program, locale);

    return new ProgramRestEntity(program.getId(), // NOSONAR
                                 program.getTitle(),
                                 program.getDescription(),
                                 program.getSpaceId(),
                                 program.getPriority(),
                                 program.getCreatedBy(),
                                 program.getCreatedDate(),
                                 program.getLastModifiedBy(),
                                 program.getLastModifiedDate(),
                                 program.isDeleted(),
                                 program.isEnabled(),
                                 program.getBudget(),
                                 program.getType(),
                                 null,
                                 program.getCoverFileId(),
                                 program.getCoverUrl(),
                                 program.getOwnerIds(),
                                 program.getRulesTotalScore(),
                                 program.isOpen(),
                                 program.getSpaceId() > 0 ? Utils.getSpaceById(String.valueOf(program.getSpaceId())) : null,
                                 toUserContext(programService, program, username),
                                 getProgramOwnersByIds(program.getOwnerIds(), program.getSpaceId()));
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

  public static List<ProgramRestEntity> toRestEntities(ProgramService programService,
                                                       TranslationService translationService,
                                                       Locale locale,
                                                       List<ProgramDTO> programs,
                                                       String username) {
    return programs.stream().map(program -> toRestEntity(programService, translationService, program, locale, username)).toList();
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
    Identity identity = identityManager.getOrCreateUserIdentity(username);
    mapUserInfo(userContext, identity);
    if (program != null && !program.isOpen()) {
      long spaceId = program.getSpaceId();
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      Space space = spaceService.getSpaceById(String.valueOf(spaceId));
      if (space != null) {
        boolean isSuperManager = spaceService.isSuperManager(username);
        boolean isManager = isSuperManager || spaceService.isManager(space, username);
        boolean isMember = isManager || spaceService.isMember(space, username);
        boolean isRedactor = isManager || spaceService.isRedactor(space, username);
        userContext.setManager(isManager);
        userContext.setMember(isMember);
        userContext.setRedactor(isRedactor);
        userContext.setCanEdit(programService.isProgramOwner(program.getId(), username));
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
    userInfo.setAvatarUrl(identity.getProfile().getAvatarUrl());
    userInfo.setFullName(identity.getProfile().getFullName());
    userInfo.setRemoteId(identity.getRemoteId());
    userInfo.setId(identity.getId());
  }

}
