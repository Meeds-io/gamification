package org.exoplatform.gamification.entities.domain.effective;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class BadgeItemEntity implements Serializable {

    @Column(name = "TITLE")
    protected String title;

    @Column(name = "REQUIRED_SCORE")
    protected int requiredScore;

    @Column(name = "CURRENT_SCORE")
    protected int currentScore;

    @Column(name = "AFFECTED_SCORE")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date affectedDate;

    public BadgeItemEntity() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public void setRequiredScore(int requiredScore) {
        this.requiredScore = requiredScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public Date getAffectedDate() {
        return affectedDate;
    }

    public void setAffectedDate(Date affectedDate) {
        this.affectedDate = affectedDate;
    }

}
