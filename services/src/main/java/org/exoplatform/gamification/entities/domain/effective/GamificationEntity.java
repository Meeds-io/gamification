package org.exoplatform.gamification.entities.domain.effective;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "Gamification")
@NamedQuery(
        name="byItemsQuantity",
        query = "SELECT g FROM Gamification g JOIN g.badges b WHERE b.title = :title"
)
public class GamificationEntity implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected long id;

    @Column(name = "USERNAME", unique = true,   nullable = false)
    protected String username;

    @Column(name = "SCORE")
    protected int score;

    @ElementCollection
    private List<BadgeItemEntity> badges;

    @ElementCollection
    private List<MissionItemEntity> missions;

    @Embedded
    private ExoCoinEntity exoCoinEntity;

    @Column(name = "CREATED_DATE", nullable = false)
    @JsonIgnore
    //---Can't match java8 API with JPA/hibernate : we have to upgrade to jpa-2.2
    //private Instant createdDate = Instant.now();
    protected Date createdDate = new Date();

    @Column(name = "LAST_MODIFIED_DATE")
    @JsonIgnore
    //---Can't match java8 API with JPA/hibernate : we have to upgrade to jpa-2.2
    //private Instant lastModifiedDate = Instant.now();
    protected Date lastModifiedDate = null;

    public GamificationEntity() {

        this.badges = new ArrayList<BadgeItemEntity>();

        this.missions = new ArrayList<MissionItemEntity>();


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<BadgeItemEntity> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeItemEntity> badges) {
        this.badges = badges;
    }

    public List<MissionItemEntity> getMissions() {
        return missions;
    }

    public void setMissions(List<MissionItemEntity> missions) {
        this.missions = missions;
    }

    public ExoCoinEntity getExoCoinEntity() {
        return exoCoinEntity;
    }

    public void setExoCoinEntity(ExoCoinEntity exoCoinEntity) {
        this.exoCoinEntity = exoCoinEntity;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void addBadgeItem (BadgeItemEntity badgeItemEntity) {

        this.getBadges().add(badgeItemEntity);
    }

    public void addMissionItem (MissionItemEntity missionItemEntity) {
        this.getMissions().add(missionItemEntity);
    }
}
