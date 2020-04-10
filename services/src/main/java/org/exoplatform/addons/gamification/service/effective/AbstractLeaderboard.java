package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

public abstract class AbstractLeaderboard implements Serializable {

  private static final long serialVersionUID = 6094595264371166779L;

  protected String          earnerId;

  protected long            reputationScore;

  public String getEarnerId() {
    return earnerId;
  }

  public void setEarnerId(String earnerId) {
    this.earnerId = earnerId;
  }

  public long getReputationScore() {
    return reputationScore;
  }

  public void setReputationScore(long reputationScore) {
    this.reputationScore = reputationScore;
  }
}
