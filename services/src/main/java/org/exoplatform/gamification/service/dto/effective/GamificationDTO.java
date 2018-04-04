package org.exoplatform.gamification.service.dto.effective;

import org.exoplatform.gamification.entities.domain.effective.ExoCoinEntity;
import org.exoplatform.gamification.entities.domain.effective.GamificationEntity;
import org.exoplatform.gamification.service.mapper.BadgeItemMapper;
import org.exoplatform.gamification.service.mapper.BadgeMapper;
import org.exoplatform.gamification.service.mapper.MissionItemMapper;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GamificationDTO {

    @NotBlank
    protected long id;

    @Size(max = 256)
    protected String username;

    protected int score;

    private List<BadgeItemDTO> badges;

    private List<MissionItemDTO> missions;

    private ExoCoinEntity exoCoinEntity;

    protected Date createdDate = null;

    protected Date lastModifiedDate = null;

    public GamificationDTO (GamificationEntity gamification) {

        this.setId(gamification.getId());

        this.setUsername (gamification.getUsername());

        this.setScore (gamification.getScore());

        this.setBadges ((new BadgeItemMapper()).badgesToBadgesDTOs(gamification.getBadges()));

        this.setMissions ((new MissionItemMapper()).missionsToMissionDTOs(gamification.getMissions()));

        this.setExoCoinEntity (gamification.getExoCoinEntity());

        this.setCreatedDate (gamification.getCreatedDate());

        this.setLastModifiedDate (gamification.getLastModifiedDate());

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

    public List<BadgeItemDTO> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeItemDTO> badges) {
        this.badges = badges;
    }

    public List<MissionItemDTO> getMissions() {
        return missions;
    }

    public void setMissions(List<MissionItemDTO> missions) {
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

    @Override
    public String toString() {
        return "GamificationDTO{" +
                "username='" + username + '\'' +
                ", score='" + score + '\'' +
                ", exoCoin='" + exoCoinEntity + '\'' +
                ", badges='" + badges + '\'' +
                ", missions=" + missions +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +

                "}";
    }
}
