package org.exoplatform.addons.gamification.service.dto.configuration;

public class UserInfo implements Cloneable {

  private String  id;

  private String  remoteId;

  private String  fullName;

  private String  avatarUrl;

  private boolean canEdit;

  private boolean canAnnounce;

  private boolean isMember;

  private boolean isRedactor;

  private boolean isManager;

  @Override
  protected UserInfo clone() {
    return new UserInfo(id, remoteId, fullName, avatarUrl, canEdit, canAnnounce, isMember, isRedactor, isManager);
  }

  public UserInfo(String id,
                  String remoteId,
                  String fullName,
                  String avatarUrl,
                  boolean canEdit,
                  boolean canAnnounce,
                  boolean isMember,
                  boolean isRedactor,
                  boolean isManager) {
    this.id = id;
    this.remoteId = remoteId;
    this.fullName = fullName;
    this.avatarUrl = avatarUrl;
    this.canEdit = canEdit;
    this.canAnnounce = canAnnounce;
    this.isMember = isMember;
    this.isRedactor = isRedactor;
    this.isManager = isManager;
  }

  public UserInfo() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRemoteId() {
    return remoteId;
  }

  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public boolean isCanEdit() {
    return canEdit;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public boolean isCanAnnounce() {
    return canAnnounce;
  }

  public void setCanAnnounce(boolean canAnnounce) {
    this.canAnnounce = canAnnounce;
  }

  public boolean isMember() {
    return isMember;
  }

  public void setMember(boolean member) {
    isMember = member;
  }

  public boolean isRedactor() {
    return isRedactor;
  }

  public void setRedactor(boolean redactor) {
    isRedactor = redactor;
  }

  public boolean isManager() {
    return isManager;
  }

  public void setManager(boolean manager) {
    isManager = manager;
  }
}
