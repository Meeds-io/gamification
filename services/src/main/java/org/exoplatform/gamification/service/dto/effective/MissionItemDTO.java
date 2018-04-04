package org.exoplatform.gamification.service.dto.effective;

import org.exoplatform.gamification.entities.domain.effective.MissionItemEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

public class MissionItemDTO {

    @NotBlank
    protected String title;

    protected Date startDate;

    protected Date endDate;

    protected int wonScore;

    public MissionItemDTO(MissionItemEntity mission) {

        this.title = mission.getTitle();

        this.startDate = mission.getStartDate();

        this.endDate = mission.getEndDate();

        this.wonScore = mission.getWonScore();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getWonScore() {
        return wonScore;
    }

    public void setWonScore(int wonScore) {
        this.wonScore = wonScore;
    }

    @Override
    public String toString() {
        return "BadgeItemDTO{" +
                "title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", wonScore='" + wonScore + '\'' +
                "}";
    }
}
