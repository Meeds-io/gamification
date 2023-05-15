package org.exoplatform.addons.gamification.storage;

import static org.exoplatform.addons.gamification.GamificationConstant.ACTIVITY_OBJECT_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.service.dto.configuration.Announcement;
import org.exoplatform.addons.gamification.service.dto.configuration.ProgramDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RealizationDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.PeriodType;
import org.exoplatform.addons.gamification.utils.Utils;

public class AnnouncementStorage {

  public static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24; // NOSONAR

  private RealizationStorage realizationStorage;

  private RuleStorage        ruleStorage;

  public AnnouncementStorage(RealizationStorage realizationStorage,
                             RuleStorage ruleStorage) {
    this.realizationStorage = realizationStorage;
    this.ruleStorage = ruleStorage;
  }

  public Announcement saveAnnouncement(Announcement announcement) {
    if (announcement == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }
    RuleDTO rule = ruleStorage.findRuleById(announcement.getChallengeId());

    RealizationDTO announcementRealization = toRealization(announcement, rule);
    if (announcementRealization.getId() == null
        && StringUtils.isNotBlank(rule.getEndDate())
        && StringUtils.isNotBlank(rule.getStartDate())) {
      Date endDate = Utils.parseSimpleDate(rule.getEndDate());
      Date startDate = Utils.parseSimpleDate(rule.getStartDate());
      Date nextToEndDate = new Date(endDate.getTime() + MILLIS_IN_A_DAY);
      Date createdDate = new Date();
      if (!createdDate.before(nextToEndDate)) {
        throw new IllegalStateException("announcement is not allowed when rule has ended");
      }
      if (!createdDate.after(startDate)) {
        throw new IllegalStateException("announcement is not allowed when rule hasn't started yet");
      }
    }
    if (announcement.getActivityId() != null) {
      announcementRealization.setObjectType(ACTIVITY_OBJECT_TYPE);
      announcementRealization.setObjectId(String.valueOf(announcement.getActivityId()));
    }
    if (announcementRealization.getId() == null) {
      announcementRealization = realizationStorage.createRealization(announcementRealization);
    } else {
      announcementRealization = realizationStorage.updateRealization(announcementRealization);
    }
    return fromRealization(announcementRealization);
  }

  public Announcement deleteAnnouncement(Announcement announcement) {
    if (announcement == null) {
      throw new IllegalArgumentException("Announcement argument is null");
    }
    RuleDTO rule = ruleStorage.findRuleById(announcement.getChallengeId());
    RealizationDTO announcementRealization = toRealization(announcement, rule);
    announcementRealization.setStatus(HistoryStatus.CANCELED.name());
    announcementRealization.setActivityId(null);
    announcementRealization.setObjectId(null);
    announcementRealization = realizationStorage.updateRealization(announcementRealization);
    return fromRealization(announcementRealization);
  }

  public Announcement getAnnouncementById(long announcementId) {
    RealizationDTO announcementRealization = realizationStorage.getRealizationById(announcementId);
    return fromRealization(announcementRealization);
  }

  public List<Announcement> findAnnouncements(String earnerIdentityId) {
    List<RealizationDTO> announcementEntities = realizationStorage.findRealizationsByIdentityIdAndByType(earnerIdentityId,
                                                                                                         EntityType.MANUAL);
    return fromAnnouncementEntities(announcementEntities);
  }

  public List<Announcement> findAnnouncements(long ruleId,
                                              int offset,
                                              int limit,
                                              PeriodType periodType,
                                              IdentityType earnerType) {
    List<RealizationDTO> announcementEntities = realizationStorage.findRealizationsByRuleId(ruleId,
                                                                                            offset,
                                                                                            limit,
                                                                                            periodType,
                                                                                            earnerType);
    return fromAnnouncementEntities(announcementEntities);
  }

  public Long countAnnouncements(long ruleId) {
    return realizationStorage.countRealizationsByRuleId(ruleId);
  }

  public Long countAnnouncements(long ruleId, IdentityType earnerType) {
    return realizationStorage.countRealizationsByRuleIdAndEarnerType(ruleId, earnerType);
  }

  private RealizationDTO toRealization(Announcement announcement, RuleDTO rule) {
    if (announcement == null) {
      return null;
    }
    String creator = Utils.getUserRemoteId(String.valueOf(announcement.getCreator()));
    ProgramDTO program = rule.getProgram();

    RealizationDTO announcementRealization = new RealizationDTO();
    if (announcement.getId() != 0) {
      announcementRealization.setId(announcement.getId());
    }
    if (announcement.getActivityId() != null) {
      announcementRealization.setActivityId(announcement.getActivityId());
    }
    if (announcement.getAssignee() != null) {
      announcementRealization.setEarnerId(String.valueOf(announcement.getAssignee()));
    }
    announcementRealization.setComment(announcement.getComment());
    announcementRealization.setRuleId(rule.getId());
    announcementRealization.setActionTitle(announcement.getChallengeTitle() != null ? announcement.getChallengeTitle()
                                                                                    : rule.getTitle());
    announcementRealization.setCreator(announcement.getCreator());
    announcementRealization.setCreatedDate(announcement.getCreatedDate() == null ? Utils.toRFC3339Date(new Date())
                                                                                 : announcement.getCreatedDate());
    announcementRealization.setReceiver(String.valueOf(announcement.getCreator()));
    announcementRealization.setStatus(HistoryStatus.ACCEPTED.name());
    announcementRealization.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    announcementRealization.setCreatedBy(creator != null ? creator : "Gamification Inner Process");
    announcementRealization.setLastModifiedBy(creator != null ? creator : "Gamification Inner Process");
    announcementRealization.setEarnerType(IdentityType.USER.name());
    announcementRealization.setActionScore(rule.getScore());
    announcementRealization.setGlobalScore(realizationStorage.getScoreByIdentityId(String.valueOf(announcement.getAssignee())));
    announcementRealization.setProgram(program);
    announcementRealization.setDomainLabel(program == null ? null : program.getTitle());
    announcementRealization.setObjectId("");
    announcementRealization.setType(rule.getType());
    return announcementRealization;
  }

  private Announcement fromRealization(RealizationDTO realization) {
    if (realization == null) {
      return null;
    }
    return new Announcement(realization.getId(),
                            realization.getRuleId(),
                            realization.getActionTitle(),
                            Long.parseLong(realization.getEarnerId()),
                            realization.getComment(),
                            realization.getCreator(),
                            realization.getCreatedDate(),
                            realization.getActivityId());
  }

  private List<Announcement> fromAnnouncementEntities(List<RealizationDTO> announcementEntities) {
    if (CollectionUtils.isEmpty(announcementEntities)) {
      return new ArrayList<>(Collections.emptyList());
    } else {
      return announcementEntities.stream().map(this::fromRealization).toList();
    }
  }

}
