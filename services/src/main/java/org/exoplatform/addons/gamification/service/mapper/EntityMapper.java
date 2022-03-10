package org.exoplatform.addons.gamification.service.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.service.dto.configuration.*;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {

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
                         challengeEntity.getManagers(),
                         (long) challengeEntity.getScore(),
                         challengeEntity.getDomainEntity() != null ? challengeEntity.getDomainEntity().getTitle() : null);
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
    challengeEntity.setScore(challenge.getPoints().intValue());

    DomainDTO domain = Utils.getDomainByTitle(challenge.getProgram());
    if (domain != null ) {
      challengeEntity.setDomainEntity(DomainMapper.domainDTOToDomain(domain));
    }
    return challengeEntity;
  }

  public static List<Challenge> fromChallengeEntities(List<RuleEntity> challengeEntities) {
    if (CollectionUtils.isEmpty(challengeEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return challengeEntities.stream().map(EntityMapper::fromEntity).collect(Collectors.toList());
    }
  }

  public static AnnouncementRestEntity fromAnnouncement(Announcement announcement) {
    if (announcement == null) {
      return null;
    }
    return new AnnouncementRestEntity(announcement.getId(),
                                      Utils.getUserRemoteId(String.valueOf(announcement.getAssignee() != null ? announcement.getAssignee()
                                                                                           : announcement.getCreator())),
                                      announcement.getCreatedDate(),
                                      announcement.getActivityId());
  }

  public static Announcement fromEntity(GamificationActionsHistory announcementEntity) {
    if (announcementEntity == null) {
      return null;
    }
    return new Announcement(announcementEntity.getId(),
                            announcementEntity.getRuleId(),
                            Long.parseLong(announcementEntity.getEarnerId()),
                            announcementEntity.getComment(),
                            announcementEntity.getCreator(),
                            Utils.toRFC3339Date(announcementEntity.getCreatedDate()),
                            announcementEntity.getActivityId());
  }

  public static GamificationActionsHistory toEntity(Announcement announcement) {
    if (announcement == null) {
      return null;
    }
    GamificationActionsHistory announcementEntity = new GamificationActionsHistory();
    if (announcement.getId() != 0) {
      announcementEntity.setId(announcement.getId());
    }
    if (announcement.getActivityId() != null) {
      announcementEntity.setActivityId(announcement.getActivityId());
    }
    if (announcement.getAssignee() != null) {
      announcementEntity.setEarnerId(String.valueOf(announcement.getAssignee()));
    }

    Date createDate = Utils.parseRFC3339Date(announcement.getCreatedDate());
    announcementEntity.setComment(announcement.getComment());
    announcementEntity.setCreatedDate(createDate);
    announcementEntity.setRuleId(announcement.getChallengeId());
    announcementEntity.setCreator(announcement.getCreator());
    announcementEntity.setDate( createDate != null ? createDate : new Date(System.currentTimeMillis()));
    announcementEntity.setCreatedDate( createDate != null ? createDate : new Date(System.currentTimeMillis()));
    announcementEntity.setReceiver(String.valueOf(announcement.getCreator()));
    announcementEntity.setStatus(HistoryStatus.ACCEPTED);
    if (createDate != null) {
      announcementEntity.setCreatedDate(createDate);
    }
    announcementEntity.setLastModifiedDate(new Date(System.currentTimeMillis()));

    String creator = Utils.getUserRemoteId(String.valueOf(announcement.getCreator()));
    announcementEntity.setCreatedBy(creator != null ? creator : "Gamification Inner Process");
    announcementEntity.setLastModifiedBy(creator != null ? creator : "Gamification Inner Process");

    return announcementEntity;
  }

  public static List<Announcement> fromAnnouncementEntities(List<GamificationActionsHistory> announcementEntities) {
    if (CollectionUtils.isEmpty(announcementEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcementEntities.stream().map(EntityMapper::fromEntity).collect(Collectors.toList());
    }
  }

  public static List<AnnouncementRestEntity> fromAnnouncementList(List<Announcement> announcements) {
    if (CollectionUtils.isEmpty(announcements)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcements.stream().map(EntityMapper::fromAnnouncement).collect(Collectors.toList());
    }
  }

  public static ChallengeRestEntity fromChallenge(Challenge challenge, List<Announcement> challengeAnnouncements) {
    if (challenge == null) {
      return null;
    }
    List<AnnouncementRestEntity> announcementRestEntities = fromAnnouncementList(challengeAnnouncements);
    Space space = Utils.getSpaceById(String.valueOf(challenge.getAudience()));
    return new ChallengeRestEntity(challenge.getId(),
                                   challenge.getTitle(),
                                   challenge.getDescription(),
                                   space,
                                   challenge.getStartDate(),
                                   challenge.getEndDate(),
                                   Utils.createUser(Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME,
                                                                                 Utils.getCurrentUser()),
                                                    space,
                                                    challenge.getManagers()),
                                   Utils.getManagersByIds(challenge.getManagers()),
                                   Utils.countAnnouncementsByChallenge(challenge.getId()),
                                   announcementRestEntities,
                                   challenge.getPoints(),
                                   Utils.getDomainByTitle(challenge.getProgram()));
  }
}
