package org.exoplatform.gamification.service.effective;

public class GamificationSearch {

    private String filter = "rank";

    private String zone = "all";

    private boolean isNetwork = false;

    public GamificationSearch() {
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public void setNetwork(boolean network) {
        isNetwork = network;
    }
}
