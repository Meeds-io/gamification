package org.exoplatform.addons.gamification.service.effective;

import org.exoplatform.addons.gamification.IdentityType;

public class LeaderboardFilter {

  private int          loadCapacity = 10;

  private String       currentUser  = null;

  private String       domain       = "all";

  private IdentityType identityType = IdentityType.USER;

  private Period       period       = Period.WEEK;

  public enum Period {
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

  public void setLoadCapacity(int limit) {
    this.loadCapacity = limit;
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
    this.period = Period.valueOf(period.toUpperCase());
  }

  public IdentityType getIdentityType() {
    return identityType;
  }

  public void setIdentityType(IdentityType identityType) {
    this.identityType = identityType;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

  public String getCurrentUser() {
    return currentUser;
  }
}
