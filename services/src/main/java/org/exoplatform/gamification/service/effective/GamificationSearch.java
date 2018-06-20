package org.exoplatform.gamification.service.effective;

public class GamificationSearch {

    private String domain = "all";

    private String network = "everyone";

    public GamificationSearch() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
