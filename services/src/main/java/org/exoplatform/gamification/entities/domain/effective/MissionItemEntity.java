package org.exoplatform.gamification.entities.domain.effective;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class MissionItemEntity implements Serializable {

    @Column(name = "TITLE")
    protected String title;

    @Column(name = "START_DATE")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date startDate;

    @Column(name = "END_DATE")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date endDate;

    @Column(name = "WON_SCORE")
    protected int wonScore;

    public MissionItemEntity() {
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
}
