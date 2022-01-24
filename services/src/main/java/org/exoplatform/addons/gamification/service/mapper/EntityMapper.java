package org.exoplatform.addons.gamification.service.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.Challenge;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {
  private static final Log LOG = ExoLogger.getLogger(EntityMapper.class);

  private EntityMapper() {
  }

  public static Challenge fromEntity(RuleEntity challengeEntity) {
    if (challengeEntity == null) {
      return null;
    }
    return new Challenge(challengeEntity.getId(),
                         challengeEntity.getTitle(),
                         challengeEntity.getDescription(),
                         challengeEntity.getAudience(),
                         challengeEntity.getStartDate() == null ? null : Utils.toRFC3339Date(challengeEntity.getStartDate()),
                         challengeEntity.getEndDate() == null ? null : Utils.toRFC3339Date(challengeEntity.getEndDate()),
                         Utils.canEditChallenge(challengeEntity.getManagers()),
                         Utils.canAnnounce(String.valueOf(challengeEntity.getAudience())),
                         challengeEntity.getManagers());
  }

  public static RuleEntity toEntity(Challenge challenge) {
    if (challenge == null) {
      return null;
    }
    RuleEntity challengeEntity = new RuleEntity();
    if (challenge.getId() != 0) {
      challengeEntity.setId(challenge.getId());
    }
    if (StringUtils.isNotBlank(challenge.getTitle())) {
      challengeEntity.setTitle(challenge.getTitle());
    }
    if (StringUtils.isNotBlank(challenge.getDescription())) {
      challengeEntity.setDescription(challenge.getDescription());
    }
    if (challenge.getEndDate() != null) {
      challengeEntity.setEndDate(Utils.parseRFC3339Date(challenge.getEndDate()));
    }
    if (challenge.getStartDate() != null) {
      challengeEntity.setStartDate(Utils.parseRFC3339Date(challenge.getStartDate()));
    }
    if (challenge.getManagers() == null || challenge.getManagers().isEmpty()) {
      challengeEntity.setManagers(Collections.emptyList());
    } else {
      challengeEntity.setManagers(challenge.getManagers());
    }
    challengeEntity.setAudience(challenge.getAudience());
    challengeEntity.setManagers(challenge.getManagers());
    return challengeEntity;
  }

  public static List<Challenge> fromChallengeEntities(List<RuleEntity> challengeEntities) {
    if (CollectionUtils.isEmpty(challengeEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      List<Challenge> challenges = challengeEntities.stream()
                                                    .map(challengeEntity -> fromEntity(challengeEntity))
                                                    .collect(Collectors.toList());
      return challenges;
    }
  }

}
