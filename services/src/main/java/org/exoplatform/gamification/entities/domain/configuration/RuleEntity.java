package org.exoplatform.gamification.entities.domain.configuration;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Rule")
@ExoEntity
@Table(name = "RULE")
@NamedQueries({
        @NamedQuery(
                name = "Rule.getAllRules",
                query = "SELECT rule FROM Rule rule "
        ),
        @NamedQuery(
                name = "Rule.getEnabledRules",
                query = "SELECT rule FROM Rule rule where rule.isEnabled = :isEnabled "
        ),
        @NamedQuery(
                name = "Rule.findRuleByTitle",
                query = "SELECT rule FROM Rule rule where rule.title = :ruleTitle"
        ),
        @NamedQuery(
                name = "Rule.deleteRuleByTitle",
                query = "DELETE FROM Rule rule WHERE rule.title = :ruleTitle "
        ),
        @NamedQuery(
                name = "Badge.deleteRuleById",
                query = "DELETE FROM Rule rule WHERE rule.id = :badgeId "
        )
})
public class RuleEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "TITLE", unique = true,   nullable = false)
    protected String title;

    /**
     @Column(name = "MODULE", unique = true,   nullable = false)
     protected Module  module /**(module) List [Foreigh_Key to table CategoryType]
     */
    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "SCORE")
    protected int score;

    @Column(name = "AREA", unique = false,   nullable = false)
    protected String area;

    @Column(name = "RULE_IS_ENABLE",nullable = false)
    protected boolean isEnabled;

    public RuleEntity() {
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

        RuleEntity ruleEntity = (RuleEntity) o;
        return !(ruleEntity.getId() == null || getId() == null) && Objects.equals(getId(), ruleEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Badge{" +
                "title='" + title + '\'' +
                ", score='" + score + '\'' +
                ", area='" + area + '\'' +
                ", description='" + description + '\'' +
                ", enable='" + isEnabled + '\'' +
                "}";
    }
}
