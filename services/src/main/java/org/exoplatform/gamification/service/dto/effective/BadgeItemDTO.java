package org.exoplatform.gamification.service.dto.effective;

import org.exoplatform.gamification.entities.domain.effective.BadgeItemEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

public class BadgeItemDTO {

    @NotBlank
    protected String title;

    protected int requiredScore;

    protected int currentScore;

    protected Date affectedDate;

    public BadgeItemDTO(BadgeItemEntity badge) {

        this.title = badge.getTitle();

        this.requiredScore = badge.getRequiredScore();

        this.currentScore = badge.getCurrentScore();

        this.affectedDate = badge.getAffectedDate();
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

    @Override
    public String toString() {
        return "BadgeItemDTO{" +
                "title='" + title + '\'' +
                ", requiredScore='" + requiredScore + '\'' +
                ", currentScore='" + currentScore + '\'' +
                ", affectedDate='" + affectedDate + '\'' +
                "}";
    }
}
