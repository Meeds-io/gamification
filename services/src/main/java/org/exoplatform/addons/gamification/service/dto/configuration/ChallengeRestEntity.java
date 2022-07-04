package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.List;

import org.exoplatform.social.core.space.model.Space;

public class ChallengeRestEntity implements Cloneable {

  private long                         id;

  private String                       title;

  private String                       description;

  private Space                        space;

  private String                       startDate;

  private String                       endDate;

  private UserInfo                     userInfo;

  private List<UserInfo>               managers;

  private Long                         announcementsCount;

  private List<AnnouncementRestEntity> announcements;

  private Long                         points;

  private DomainDTO                    program;

  @Override
  public ChallengeRestEntity clone() { // NOSONAR
    return new ChallengeRestEntity(id,
                                   title,
                                   description,
                                   space,
                                   startDate,
                                   endDate,
                                   userInfo,
                                   managers,
                                   announcementsCount,
                                   announcements,
                                   points,
                                   program);
  }

  public ChallengeRestEntity(long id,
                             String title,
                             String description,
                             Space space,
                             String startDate,
                             String endDate,
                             UserInfo userInfo,
                             List<UserInfo> managers,
                             Long announcementsCount,
                             List<AnnouncementRestEntity> announcements,
                             Long points,
                             DomainDTO program) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.space = space;
    this.startDate = startDate;
    this.endDate = endDate;
    this.userInfo = userInfo;
    this.managers = managers;
    this.announcementsCount = announcementsCount;
    this.announcements = announcements;
    this.points = points;
    this.program = program;
  }

  public ChallengeRestEntity() {
  }

  public List<AnnouncementRestEntity> getAnnouncements() {
    return announcements;
  }

  public void setAnnouncements(List<AnnouncementRestEntity> announcements) {
    this.announcements = announcements;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public List<UserInfo> getManagers() {
    return managers;
  }

  public void setManagers(List<UserInfo> managers) {
    this.managers = managers;
  }

  public Long getAnnouncementsCount() {
    return announcementsCount;
  }

  public void setAnnouncementsCount(Long announcementsCount) {
    this.announcementsCount = announcementsCount;
  }

  public Long getPoints() {
    return points;
  }

  public void setPoints(Long points) {
    this.points = points;
  }

  public DomainDTO getProgram() {
    return program;
  }

  public void setProgram(DomainDTO program) {
    this.program = program;
  }
}
