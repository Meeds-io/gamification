package io.meeds.gamification.rest.model;

public class LeaderboardInfo {

  String technicalId;

  String socialId;

  String avatarUrl;

  String remoteId;

  String fullname;

  long   score;

  String profileUrl;

  int    rank;

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getRemoteId() {
    return remoteId;
  }

  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public long getScore() {
    return score;
  }

  public void setScore(long score) {
    this.score = score;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public void setProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
  }

  public String getSocialId() {
    return socialId;
  }

  public void setSocialId(String socialId) {
    this.socialId = socialId;
  }

  public String getTechnicalId() {
    return technicalId;
  }

  public void setTechnicalId(String technicalId) {
    this.technicalId = technicalId;
  }
}
