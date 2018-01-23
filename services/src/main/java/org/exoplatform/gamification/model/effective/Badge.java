package org.exoplatform.gamification.model.effective;

import java.io.Serializable;
import java.util.Date;

public class Badge implements Serializable {

    String title;

    String icon;

    int scorescore;

    Date acquiredDate;

    public Badge() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScorescore() {
        return scorescore;
    }

    public void setScorescore(int scorescore) {
        this.scorescore = scorescore;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getAcquiredDate() {
        return acquiredDate;
    }

    public void setAcquiredDate(Date acquiredDate) {
        this.acquiredDate = acquiredDate;
    }
}
