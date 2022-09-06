package org.exoplatform.addons.gamification.entities.domain.configuration;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.exoplatform.commons.api.persistence.ExoEntity;

@ExoEntity
@Entity(name = "GamificationDomainOwner")
@Table(name = "GAMIFICATION_DOMAIN_OWNERS")
@NamedQuery(name = "GamificationDomainOwner.getDomainOwners", query = "SELECT o FROM GamificationDomainOwner o WHERE o.domain.id = :domainId")
public class DomainOwnerEntity implements Serializable {

  private static final long serialVersionUID = 5875077131395950233L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_DOMAIN_OWNER_ID", sequenceName = "SEQ_GAMIFICATION_DOMAIN_OWNER_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_DOMAIN_OWNER_ID")
  @Column(name = "DOMAIN_OWNER_ID")
  private Long              id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DOMAIN_ID", referencedColumnName = "ID")
  private DomainEntity      domain;

  @Column(name = "IDENTITY_ID", nullable = false)
  private long              identityId;

  public DomainOwnerEntity() {
    // Empty constructor
  }

  public DomainOwnerEntity(DomainEntity domain, long identityId) {
    this.domain = domain;
    this.identityId = identityId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public DomainEntity getDomain() {
    return domain;
  }

  public void setDomain(DomainEntity domain) {
    this.domain = domain;
  }

  public long getIdentityId() {
    return identityId;
  }

  public void setIdentityId(long identityId) {
    this.identityId = identityId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DomainOwnerEntity other = (DomainOwnerEntity) obj;
    return Objects.equals(domain.getId(), other.domain.getId()) && Objects.equals(id, other.id) && identityId == other.identityId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(domain.getId(), id, identityId);
  }

}
