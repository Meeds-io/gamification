package org.exoplatform.addons.gamification.service.effective;

import java.io.Serializable;

public class PiechartLeaderboard extends AbstractLeaderboard implements Serializable {

    private String label;

    private long value;

    public PiechartLeaderboard(String label , long value ) {

        this.label = label;

        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
