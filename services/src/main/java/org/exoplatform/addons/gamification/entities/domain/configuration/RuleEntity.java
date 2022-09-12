/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.entities.domain.configuration;

import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(name = "Rule")
@ExoEntity
@Table(name = "GAMIFICATION_RULE")
@NamedQuery(name = "Rule.getAllRules", query = "SELECT rule FROM Rule rule WHERE rule.isDeleted = false ORDER BY rule.createdDate desc")
@NamedQuery(name = "Rule.getEnabledRules", query = "SELECT rule FROM Rule rule where rule.isEnabled = :isEnabled AND rule.isDeleted = false and rule.type = :type")
@NamedQuery(name = "Rule.getAllRulesByDomain", query = "SELECT rule FROM Rule rule where LOWER(rule.area) = LOWER(:domain) AND rule.isDeleted = false and rule.type = :type")
@NamedQuery(name = "Rule.getAllRulesWithNullDomain", query = "SELECT rule FROM Rule rule where rule.domainEntity IS NULL and rule.type = :type ")
@NamedQuery(name = "Rule.findEnabledRuleByTitle", query = "SELECT rule FROM Rule rule where LOWER(rule.title) = LOWER(:ruleTitle) and rule.isEnabled = true and rule.type = :type")
@NamedQuery(name = "Rule.findRuleByEventAndDomain", query = "SELECT rule FROM Rule rule where LOWER(rule.event) = LOWER(:event) and LOWER(rule.area) = LOWER(:domain)  and rule.type = :type")
@NamedQuery(name = "Rule.findEnabledRulesByEvent", query = "SELECT rule FROM Rule rule where LOWER(rule.event) = LOWER(:event) and rule.isEnabled = true AND rule.isDeleted = false and rule.type = :type")
@NamedQuery(name = "Rule.findRuleByTitle", query = "SELECT rule FROM Rule rule where LOWER(rule.title) = LOWER(:ruleTitle) and rule.type = :type")
@NamedQuery(name = "Rule.getDomainList", query = "SELECT DISTINCT(rule.area) FROM Rule rule where rule.type = :type ")
@NamedQuery(name = "Rule.getEventList", query = "SELECT DISTINCT(rule.event) FROM Rule rule where rule.type = :type")
@NamedQuery(name = "Rule.getRuleIdsByType", query = "SELECT rule.id FROM Rule rule where rule.type = :type")
@NamedQuery(name = "Rule.deleteRuleByTitle", query = "DELETE FROM Rule rule WHERE LOWER(rule.title) = LOWER(:ruleTitle) ")
@NamedQuery(name = "Rule.deleteRuleById", query = "DELETE FROM Rule rule WHERE rule.id = :ruleId ")
@NamedQuery(name = "Rule.getDomainsByUser", query = "SELECT DISTINCT r.area FROM Rule r where r.audience in (:ids)")
public class RuleEntity extends AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_RULE_ID", sequenceName = "SEQ_GAMIFICATION_RULE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_RULE_ID")
  protected Long            id;

  @Column(name = "TITLE", unique = true, nullable = false)
  protected String          title;

  @Column(name = "DESCRIPTION")
  protected String          description;

  @Column(name = "SCORE")
  protected int             score;

  @Column(name = "AREA")
  protected String          area;

  @Column(name = "EVENT")
  protected String          event;

  @ManyToOne
  @JoinColumn(name = "DOMAIN_ID")
  private DomainEntity      domainEntity;

  @Column(name = "ENABLED", nullable = false)
  protected boolean         isEnabled;

  @Column(name = "DELETED", nullable = false)
  protected boolean         isDeleted;

  @Column(name = "AUDIENCE_ID")
  private Long              audience;

  @Column(name = "START_DATE")
  private Date              startDate;

  @Column(name = "END_DATE")
  private Date              endDate;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  protected EntityType        type;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CHALLENGE_MANAGER_RULE", joinColumns = @JoinColumn(name = "ID"))
  @Column(name = "MANAGER_ID")
  private List<Long> managers;

  public List<Long> getManagers() {
    return managers;
  }

  public void setManagers(List<Long> managers) {
    this.managers = managers;
  }

  public EntityType getType() {
    return type;
  }

  public void setType( EntityType type) {
    this.type = type;
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

  public Long getAudience() {
    return audience;
  }

  public void setAudience(Long audience) {
    this.audience = audience;
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

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public DomainEntity getDomainEntity() {
    return domainEntity;
  }

  public void setDomainEntity(DomainEntity domainEntity) {
    this.domainEntity = domainEntity;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
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
