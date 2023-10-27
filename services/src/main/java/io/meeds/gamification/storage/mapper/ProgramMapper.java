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
package io.meeds.gamification.storage.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.utils.Utils;

@SuppressWarnings("deprecation")
public class ProgramMapper {

  private ProgramMapper() {
    // Class with static methods
  }

  public static List<ProgramDTO> fromEntities(RuleDAO ruleDAO,
                                              List<ProgramEntity> programs) {
    return programs.stream()
                   .filter(Objects::nonNull)
                   .map(entity -> fromEntity(ruleDAO, entity))
                   .toList();
  }

  public static ProgramEntity toEntity(ProgramDTO program) {
    if (program == null) {
      return null;
    }
    ProgramEntity programEntity = new ProgramEntity();
    programEntity.setId(program.getId());
    programEntity.setTitle(program.getTitle());
    programEntity.setDescription(program.getDescription());
    programEntity.setColor(StringUtils.upperCase(program.getColor()));
    programEntity.setCreatedBy(program.getCreatedBy());
    programEntity.setLastModifiedBy(program.getLastModifiedBy());
    programEntity.setDeleted(program.isDeleted());
    programEntity.setEnabled(program.isEnabled());
    if (program.getSpaceId() > 0 && !program.isOpen()) {
      programEntity.setAudienceId(program.getSpaceId());
      programEntity.setVisibility(program.getVisibility());
    } else {
      programEntity.setAudienceId(null);
      programEntity.setVisibility(EntityVisibility.OPEN);
    }
    if (program.getCreatedDate() != null) {
      programEntity.setCreatedDate(Utils.parseRFC3339Date(program.getCreatedDate()));
    }
    programEntity.setLastModifiedDate(Utils.parseRFC3339Date(program.getLastModifiedDate()));
    programEntity.setPriority(program.getPriority());
    programEntity.setBudget(program.getBudget());
    programEntity.setCoverFileId(program.getCoverFileId());
    programEntity.setAvatarFileId(program.getAvatarFileId());
    if (StringUtils.isBlank(program.getType())) {
      programEntity.setType(EntityType.AUTOMATIC);
    } else {
      programEntity.setType(EntityType.valueOf(program.getType()));
    }
    if (program.getOwnerIds() != null) {
      programEntity.setOwners(program.getOwnerIds());
    } else {
      programEntity.setOwners(Collections.emptySet());
    }
    return programEntity;
  }

  public static ProgramDTO fromEntity(RuleDAO ruleDAO, ProgramEntity programEntity) {
    if (programEntity == null) {
      return null;
    }
    long lastUpdateTime = programEntity.getLastModifiedDate() == null ? 0 : programEntity.getLastModifiedDate().getTime();
    String coverUrl = Utils.buildAttachmentUrl(String.valueOf(programEntity.getId()),
                                               lastUpdateTime,
                                               Utils.ATTACHMENT_COVER_TYPE,
                                               Utils.DEFAULT_COVER_REMOTE_ID,
                                               programEntity.getCoverFileId() == 0);
    String avatarUrl = Utils.buildAttachmentUrl(String.valueOf(programEntity.getId()),
                                               lastUpdateTime,
                                               Utils.ATTACHMENT_AVATAR_TYPE,
                                               Utils.DEFAULT_AVATAR_REMOTE_ID,
                                               programEntity.getAvatarFileId() == 0);
    ProgramDTO program = new ProgramDTO();
    program.setId(programEntity.getId());
    program.setTitle(programEntity.getTitle());
    program.setDescription(programEntity.getDescription());
    program.setColor(StringUtils.upperCase(programEntity.getColor()));
    if (programEntity.getAudienceId() != null) {
      program.setSpaceId(programEntity.getAudienceId());
    }
    program.setCreatedBy(programEntity.getCreatedBy());
    program.setCreatedDate(Utils.toRFC3339Date(programEntity.getCreatedDate()));
    program.setLastModifiedBy(programEntity.getLastModifiedBy());
    program.setLastModifiedDate(Utils.toRFC3339Date(programEntity.getLastModifiedDate()));
    program.setDeleted(programEntity.isDeleted());
    program.setEnabled(programEntity.isEnabled());
    program.setBudget(programEntity.getBudget());
    program.setType(programEntity.getType().name());
    program.setCoverFileId(programEntity.getCoverFileId());
    program.setAvatarFileId(programEntity.getAvatarFileId());
    program.setCoverUrl(coverUrl);
    program.setAvatarUrl(avatarUrl);
    program.setOwnerIds(programEntity.getOwners());
    program.setOpen(programEntity.getAudienceId() == null);
    program.setVisibility(programEntity.getVisibility());
    if (program.getVisibility() == null) {
      program.setVisibility(EntityVisibility.RESTRICTED);
    }
    program.setRulesTotalScore(programEntity.isDeleted() || !programEntity.isEnabled() ? 0
                                                                                       : ruleDAO.getRulesTotalScoreByProgramId(programEntity.getId()));
    return program;
  }

}
