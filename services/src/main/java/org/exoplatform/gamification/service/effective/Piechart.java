package org.exoplatform.gamification.service.effective;

import java.io.Serializable;

public class Piechart implements Serializable {

    private String label;

    private long value;

    public Piechart() {
    }

    public Piechart(String label, long value) {
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
