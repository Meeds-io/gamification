package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.List;

public class DomainWithChallengesRestEntity extends DomainDTO {

  private List<ChallengeRestEntity> challenges;

  private int                       challengesOffset;

  private int                       challengesLimit;

  private int                       challengesSize;

  public DomainWithChallengesRestEntity(DomainDTO domain) {
    super(domain.getId(),
          domain.getTitle(),
          domain.getDescription(),
          domain.getPriority(),
          domain.getCreatedBy(),
          domain.getCreatedDate(),
          domain.getLastModifiedBy(),
          domain.getLastModifiedDate(),
          domain.isDeleted(),
          domain.isEnabled());
  }

  public List<ChallengeRestEntity> getChallenges() {
    return challenges;
  }

  public void setChallenges(List<ChallengeRestEntity> challenges) {
    this.challenges = challenges;
  }

  public int getChallengesOffset() {
    return challengesOffset;
  }

  public void setChallengesOffset(int challengesOffset) {
    this.challengesOffset = challengesOffset;
  }

  public int getChallengesLimit() {
    return challengesLimit;
  }

  public void setChallengesLimit(int challengesLimit) {
    this.challengesLimit = challengesLimit;
  }

  public int getChallengesSize() {
    return challengesSize;
  }

  public void setChallengesSize(int challengesSize) {
    this.challengesSize = challengesSize;
  }

}
