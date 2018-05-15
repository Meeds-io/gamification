package org.exoplatform.gamification.entities.domain.configuration;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Badge")
@ExoEntity
@Table(name = "GAMIFICATION_BADGE")
@NamedQueries({
        @NamedQuery(
                name = "Badge.getAllBadges",
                query = "SELECT badge FROM Badge badge"
        ),
        @NamedQuery(
                name = "Badge.getEnabledBadges",
                query = "SELECT badge FROM Badge badge where badge.isEnabled = :isEnabled "
        ),
        @NamedQuery(
                name = "Badge.getValidBadges",
                query = "SELECT badge FROM Badge badge where (badge.startValidityDate BETWEEN :stDate AND :edDate) AND (badge.endValidityDate BETWEEN :stDate AND :edDate) "
        ),
        @NamedQuery(
                name = "Badge.findBadgeByNeededScore",
                query = "SELECT badge FROM Badge badge where badge.neededScore = :neededScore"
        ),
        @NamedQuery(
                name = "Badge.findBadgeByTitle",
                query = "SELECT badge FROM Badge badge where badge.title = :badgeTitle"
        ),
        @NamedQuery(
                name = "Badge.deleteBadgeByTitle",
                query = "DELETE FROM Badge badge WHERE badge.title = :badgeTitle "
        ),
        @NamedQuery(
                name = "Badge.deleteBadgeById",
                query = "DELETE FROM Badge badge WHERE badge.id = :badgeId "
        )
})
public class BadgeEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "TITLE", unique = true, nullable = false)
    protected String title;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "NEEDED_SCORE")
    protected int neededScore;

    @Lob
    @Column(name="ICON", nullable=false, columnDefinition="mediumblob")
    private byte[] icon;

    @Column(name = "VALIDITY_DATE_START")
    // When I used this annotation I get an issue with serialization within REST services
    //@Temporal(TemporalType.DATE)
    protected Date startValidityDate;

    @Column(name = "VALIDITY_DATE_END")
    // When I used this annotation I get an issue with serialization within REST services
    //@Temporal(TemporalType.DATE)
    protected Date endValidityDate;

    @Column(name = "BADGE_IS_ENABLE", nullable = false)
    protected boolean isEnabled;

    public BadgeEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public int getNeededScore() {
        return neededScore;
    }

    public void setNeededScore(int neededScore) {
        this.neededScore = neededScore;
    }

    public Date getStartValidityDate() {
        return startValidityDate;
    }

    public void setStartValidityDate(Date startValidityDate) {
        this.startValidityDate = startValidityDate;
    }

    public Date getEndValidityDate() {
        return endValidityDate;
    }

    public void setEndValidityDate(Date endValidityDate) {
        this.endValidityDate = endValidityDate;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BadgeEntity badgeEntity = (BadgeEntity) o;
        return !(badgeEntity.getId() == null || getId() == null) && Objects.equals(getId(), badgeEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Badge{" +
                "title='" + title + '\'' +
                ", needed score='" + neededScore + '\'' +
                ", icon='" + icon + '\'' +
                ", start validity date='" + startValidityDate + '\'' +
                ", end validity date='" + endValidityDate + '\'' +
                ", enable='" + isEnabled + '\'' +
                ", description='" + description + '\'' +
                "}";
    }
}
