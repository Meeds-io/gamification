package io.meeds.gamification.storage;

import static io.meeds.gamification.constant.GamificationConstant.ACTIVITY_OBJECT_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.utils.Utils;

public class AnnouncementStorage {

  public static final long   MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24; // NOSONAR

  private RealizationStorage realizationStorage;

  private RuleStorage        ruleStorage;

  public AnnouncementStorage(RealizationStorage realizationStorage,
                             RuleStorage ruleStorage) {
    this.realizationStorage = realizationStorage;
    this.ruleStorage = ruleStorage;
  }

  public Announcement createAnnouncement(Announcement announcement) {
    RuleDTO rule = ruleStorage.findRuleById(announcement.getChallengeId());
    RealizationDTO announcementRealization = toRealization(announcement, rule);
    if (announcement.getActivityId() != null) {
      announcementRealization.setObjectType(ACTIVITY_OBJECT_TYPE);
      announcementRealization.setObjectId(String.valueOf(announcement.getActivityId()));
    }
    announcementRealization = realizationStorage.createRealization(announcementRealization);
    announcement.setId(announcementRealization.getId());
    return announcement;
  }

  public Announcement updateAnnouncementComment(long announcementId, String comment) throws ObjectNotFoundException {
    RealizationDTO realization = realizationStorage.getRealizationById(announcementId);
    if (realization == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    realization.setComment(comment);
    realizationStorage.updateRealization(realization);
    return fromRealization(realization);
  }

  public Announcement updateAnnouncementActivityId(long announcementId, long activityId) throws ObjectNotFoundException {
    RealizationDTO realization = realizationStorage.getRealizationById(announcementId);
    if (realization == null) {
      throw new ObjectNotFoundException("Announcement does not exist");
    }
    realization.setActivityId(activityId);
    realizationStorage.updateRealization(realization);
    return fromRealization(realization);
  }

  public Announcement deleteAnnouncement(long announcementId) {
    RealizationDTO announcementRealization = realizationStorage.getRealizationById(announcementId);
    announcementRealization.setStatus(RealizationStatus.CANCELED.name());
    announcementRealization.setActivityId(null);
    announcementRealization.setObjectId(null);
    announcementRealization = realizationStorage.updateRealization(announcementRealization);
    return fromRealization(announcementRealization);
  }

  public Announcement getAnnouncementById(long announcementId) {
    RealizationDTO announcementRealization = realizationStorage.getRealizationById(announcementId);
    return fromRealization(announcementRealization);
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
    announcementRealization.setStatus(RealizationStatus.ACCEPTED.name());
    announcementRealization.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    announcementRealization.setCreatedBy(creator != null ? creator : "Gamification Inner Process");
    announcementRealization.setLastModifiedBy(creator != null ? creator : "Gamification Inner Process");
    announcementRealization.setEarnerType(IdentityType.USER.name());
    announcementRealization.setActionScore(rule.getScore());
    announcementRealization.setGlobalScore(realizationStorage.getScoreByIdentityId(String.valueOf(announcement.getAssignee())));
    announcementRealization.setProgram(program);
    announcementRealization.setProgramLabel(program == null ? null : program.getTitle());
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
