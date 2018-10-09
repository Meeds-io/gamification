package org.exoplatform.addons.gamification.entities.domain.effective;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "GamificationUserReputation")
@ExoEntity
@Table(name = "GAMIFICATION_USER_REPUTATION")
@NamedQueries({
        @NamedQuery(
                name = "GamificationUserReputation.findGamificationContextByUsername",
                query = "SELECT game FROM GamificationUserReputation game where game.username = :username"
        ),
        @NamedQuery(
                name = "GamificationUserReputation.findLeaderboardByDomain",
                query = "SELECT game FROM GamificationUserReputation game INNER JOIN game.gamificationItems item where item.zone = :domain GROUP BY item.gamificationUserEntity"
        ),
        @NamedQuery(
                name = "GamificationUserReputation.findOverallLeaderboard",
                query = "SELECT game FROM GamificationUserReputation game ORDER BY game.score DESC"
        ),
        @NamedQuery(
                name = "GamificationUserReputation.findStatsByUserId",
                query = "SELECT new org.exoplatform.addons.gamification.service.effective.PiechartLeaderboard(item.zone,sum(item.score)) FROM GamificationUserReputation game INNER JOIN game.gamificationItems item where game.username = :userId GROUP BY item.zone"
        )

})
public class GamificationContextEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "USERNAME" , unique = true, nullable = false)
    protected String username;

    @Column(name = "SCORE")
    protected long score;

    // Inverse relationship  GamificationContext (one) -> GamificationContextItem (many) coming from the master relationship GamificationContextItem (many) -> GamificationContext (one)
    // cascade insert GamificationContext -> insert all items
    // cascade update GamificationContext -> update all items
    // cascade delete GamificationContext -> delete all items
    @OneToMany(mappedBy = "gamificationUserEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<GamificationContextItemEntity> gamificationItems = new HashSet<>();

    public GamificationContextEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public Set<GamificationContextItemEntity> getGamificationItems() {
        return gamificationItems;
    }

    public void setGamificationItems(Set<GamificationContextItemEntity> gamificationItems) {
        this.gamificationItems = gamificationItems;
    }

    // association bidirectionnelle Categorie <--> Article
    public void addGamificationItem(GamificationContextItemEntity gamificationItem) {
        // l'article est ajouté dans la collection des articles de la catégorie 40.
        gamificationItems.add(gamificationItem);
        // l'article change de catégorie
        gamificationItem.setGamificationUserEntity(this);
    }

    public String toString() {
        return String.format("Gamification Entity[%d,%s,%d]", id, username, score);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(username);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GamificationContextEntity)) {
            return false;
        }
        GamificationContextEntity that = (GamificationContextEntity) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(username, that.username);
        return eb.isEquals();
    }
}
