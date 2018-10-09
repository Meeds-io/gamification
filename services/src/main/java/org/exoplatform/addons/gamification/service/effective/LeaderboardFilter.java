package org.exoplatform.addons.gamification.service.effective;

public class LeaderboardFilter {

    private String domain = "all";

    private Period period = Period.WEEK;

    enum Period { ALL, MONTH, WEEK }

    public LeaderboardFilter() {
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
}
