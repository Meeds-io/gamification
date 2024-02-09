/**
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

package io.meeds.gamification.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.EntityVisibility;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "GamificationDomain")
@Table(name = "GAMIFICATION_DOMAIN")
@NamedQuery(name = "GamificationDomain.getAllDomains", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.isDeleted = false")
@NamedQuery(name = "GamificationDomain.findByIdWithOwners", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.id = :id")
@NamedQuery(name = "GamificationDomain.findDomainByTitle", query = "SELECT domain FROM GamificationDomain domain LEFT JOIN FETCH domain.owners WHERE domain.title = :domainTitle")
@NamedQuery(name = "GamificationDomain.deleteDomainByTitle", query = "DELETE FROM GamificationDomain domain WHERE domain.title = :domainTitle")
@NamedQuery(name = "GamificationDomain.countProgramColor", query = "SELECT COUNT(*) FROM GamificationDomain domain WHERE domain.color = :color AND domain.isDeleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProgramEntity extends AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = 6578902752036385060L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_DOMAIN_ID", sequenceName = "SEQ_GAMIFICATION_DOMAIN_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_DOMAIN_ID")
  protected Long             id;

  @Column(name = "TITLE", unique = true, nullable = false)
  protected String           title;

  @Column(name = "DESCRIPTION")
  protected String           description;

  @Column(name = "COLOR")
  protected String           color;

  @Column(name = "PRIORITY")
  protected int              priority;

  @Column(name = "DELETED", nullable = false)
  protected boolean          isDeleted;

  @Column(name = "ENABLED", nullable = false)
  protected boolean          isEnabled;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "TYPE", nullable = false)
  protected EntityType       type;

  @Column(name = "BUDGET")
  protected long             budget;

  @Column(name = "COVER_FILE_ID")
  protected long             coverFileId;

  @Column(name = "AVATAR_FILE_ID")
  protected long             avatarFileId;

  @Column(name = "AUDIENCE_ID")
  protected Long             audienceId;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "VISIBILITY")
  protected EntityVisibility visibility;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "GAMIFICATION_DOMAIN_OWNERS", joinColumns = @JoinColumn(name = "DOMAIN_ID"))
  @Column(name = "IDENTITY_ID")
  private Set<Long>         owners;

}
