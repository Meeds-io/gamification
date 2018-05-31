package org.exoplatform.gamification.entities.domain.effective;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "GamificationContext")
@ExoEntity
@Table(name = "GAMIFICATION_CONTEXT")
@NamedQueries({
        @NamedQuery(
                name = "GamificationContext.findGamificationContextByUsername",
                query = "SELECT game FROM GamificationContext game where game.username = :username"
        ),
        @NamedQuery(
                name = "GamificationContext.getUserGlobalScore",
                query = "SELECT game FROM GamificationContext game where game.username = :username"
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
        return String.format("GamificationGloba[%d,%s,%d]", id, username, score);
    }
}
