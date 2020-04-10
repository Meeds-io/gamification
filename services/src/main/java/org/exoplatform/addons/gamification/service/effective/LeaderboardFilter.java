package org.exoplatform.addons.gamification.service.effective;

import org.exoplatform.addons.gamification.IdentityType;

public class LeaderboardFilter {

  private int          loadCapacity = 10;

  private String       domain       = "all";

  private IdentityType identityType = IdentityType.USER;

  private Period       period       = Period.WEEK;

  enum Period {
    ALL,
    MONTH,
    WEEK
  }

  public int getLoadCapacity() {
    return loadCapacity;
  }

  public void setLoadCapacity(String loadCapacity) {
    this.loadCapacity = Integer.parseInt(loadCapacity);
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPeriod() {
    return period.name();
  }

  public void setPeriod(String period) {
    this.period = Period.valueOf(period);
  }

  public IdentityType getIdentityType() {
    return identityType;
  }

  public void setIdentityType(IdentityType identityType) {
    this.identityType = identityType;
  }
}
