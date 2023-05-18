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

package io.meeds.gamification.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.UserInfo;
import io.meeds.gamification.rest.model.ProgramRestEntity;

public class ProgramBuilder {

  private static final Log LOG = ExoLogger.getLogger(ProgramBuilder.class);

  private ProgramBuilder() {
    // Class with static methods
  }

  public static ProgramRestEntity toRestEntity(ProgramDTO program, String username) {
    if (program == null) {
      return null;
    }
    return new ProgramRestEntity(program.getId(), // NOSONAR
                                 program.getTitle(),
                                 program.getDescription(),
                                 program.getAudienceId(),
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
                                 program.getAudienceId() > 0 ? Utils.getSpaceById(String.valueOf(program.getAudienceId())) : null,
                                 Utils.toUserContext(program, username),
                                 getProgramOwnersByIds(program.getOwnerIds(), program.getAudienceId()));
  }

  public static List<ProgramRestEntity> toRestEntities(List<ProgramDTO> domains, String username) {
    return domains.stream().map(program -> toRestEntity(program, username)).toList();
  }

  public static List<ProgramDTO> fromEntities(RuleDAO ruleDAO,
                                              List<ProgramEntity> domains) {
    return domains.stream()
                  .filter(Objects::nonNull)
                  .map(entity -> fromEntity(ruleDAO, entity))
                  .toList();
  }

  public static ProgramEntity toEntity(ProgramDTO program) {
    if (program == null) {
      return null;
    }
    ProgramEntity domain = new ProgramEntity();
    domain.setId(program.getId());
    domain.setTitle(program.getTitle());
    domain.setDescription(program.getDescription());
    domain.setCreatedBy(program.getCreatedBy());
    domain.setLastModifiedBy(program.getLastModifiedBy());
    domain.setDeleted(program.isDeleted());
    domain.setEnabled(program.isEnabled());
    if (program.getAudienceId() > 0) {
      domain.setAudienceId(program.getAudienceId());
    }
    if (program.getCreatedDate() != null) {
      domain.setCreatedDate(Utils.parseRFC3339Date(program.getCreatedDate()));
    }
    domain.setLastModifiedDate(Utils.parseRFC3339Date(program.getLastModifiedDate()));
    domain.setPriority(program.getPriority());
    domain.setBudget(program.getBudget());
    domain.setCoverFileId(program.getCoverFileId());
    if (StringUtils.isBlank(program.getType())) {
      domain.setType(EntityType.AUTOMATIC);
    } else {
      domain.setType(EntityType.valueOf(program.getType()));
    }
    if (program.getOwnerIds() != null) {
      domain.setOwners(program.getOwnerIds());
    } else {
      domain.setOwners(Collections.emptySet());
    }
    return domain;
  }

  public static ProgramDTO fromEntity(RuleDAO ruleDAO, ProgramEntity domainEntity) {
    if (domainEntity == null) {
      return null;
    }
    long lastUpdateTime = domainEntity.getLastModifiedDate() == null ? 0 : domainEntity.getLastModifiedDate().getTime();
    String coverUrl = Utils.buildAttachmentUrl(String.valueOf(domainEntity.getId()),
                                               lastUpdateTime,
                                               Utils.TYPE,
                                               domainEntity.getCoverFileId() == 0);
    ProgramDTO program = new ProgramDTO();
    program.setId(domainEntity.getId());
    program.setTitle(domainEntity.getTitle());
    program.setDescription(domainEntity.getDescription());
    if (domainEntity.getAudienceId() != null) {
      program.setAudienceId(domainEntity.getAudienceId());
    }
    program.setCreatedBy(domainEntity.getCreatedBy());
    program.setCreatedDate(Utils.toRFC3339Date(domainEntity.getCreatedDate()));
    program.setLastModifiedBy(domainEntity.getLastModifiedBy());
    program.setLastModifiedDate(Utils.toRFC3339Date(domainEntity.getLastModifiedDate()));
    program.setDeleted(domainEntity.isDeleted());
    program.setEnabled(domainEntity.isEnabled());
    program.setBudget(domainEntity.getBudget());
    program.setType(domainEntity.getType().name());
    program.setCoverFileId(domainEntity.getCoverFileId());
    program.setCoverUrl(coverUrl);
    program.setOwnerIds(domainEntity.getOwners());
    program.setRulesTotalScore(domainEntity.isDeleted()
        || !domainEntity.isEnabled() ? 0 : getRulesTotalScoreByDomain(ruleDAO, domainEntity.getId()));
    return program;
  }

  public static long getRulesTotalScoreByDomain(RuleDAO ruleDAO, long domainId) {
    if (domainId <= 0) {
      return 0;
    }
    return ruleDAO.getRulesTotalScoreByDomain(domainId);
  }

  private static List<UserInfo> getProgramOwnersByIds(Set<Long> ids, long spaceId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space space = spaceService.getSpaceById(String.valueOf(spaceId));
    return ids.stream()
              .distinct()
              .map(id -> {
                try {
                  Identity identity = identityManager.getIdentity(String.valueOf(id));
                  if (identity != null
                      && OrganizationIdentityProvider.NAME.equals(identity.getProviderId())
                      && !spaceService.isManager(space, identity.getRemoteId())
                      && spaceService.isMember(space, identity.getRemoteId())) {
                    return Utils.toUserInfo(identity);
                  }
                } catch (Exception e) {
                  LOG.warn("Error when getting domain owner with id {}. Ignore retrieving identity information", id, e);
                }
                return null;
              })
              .filter(Objects::nonNull)
              .toList();
  }

}
