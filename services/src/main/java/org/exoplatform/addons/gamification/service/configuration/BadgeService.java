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
package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.mapper.BadgeMapper;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAO;
import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Collections;
import java.util.List;

public class BadgeService {

  private static final Log LOG = ExoLogger.getLogger(BadgeService.class);

  protected final BadgeDAO badgeStorage;

  public BadgeService() {
    this.badgeStorage = CommonsUtils.getService(BadgeDAO.class);
  }

  /**
   * Find a BadgeEntity by title
   *
   * @param badgeTitle : badge title
   * @return an instance BadgeDTO
   */
  public BadgeDTO findBadgeByTitle(String badgeTitle) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeStorage.findBadgeByTitle(badgeTitle);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.badgeToBadgeDTO(entity);
      }

    } catch (Exception e) {
      LOG.error("Error to find Badge entity with title : {}", badgeTitle, e.getMessage());
    }
    return null;

  }

  /**
   * Find a BadgeEntity by id
   *
   * @param badgeId : badge id
   * @return an instance BadgeDTO
   */
  public BadgeDTO findBadgeById(Long badgeId) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeStorage.find(badgeId);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.badgeToBadgeDTO(entity);
      }

    } catch (Exception e) {
      LOG.error("Error to find Badge entity with id : {}", badgeId, e.getMessage());
    }
    return null;

  }

  /**
   * Find a BadgeEntity by title
   *
   * @param badgeTitle : badge title
   * @param domainId : badge domain id
   * @return an instance BadgeDTO
   */
  public BadgeDTO findBadgeByTitleAndDomain(String badgeTitle, long domainId) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeStorage.findBadgeByTitleAndDomain(badgeTitle, domainId);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.badgeToBadgeDTO(entity);
      }
    } catch (Exception e) {
      LOG.error("Error to find Badge entity with title : {}", badgeTitle, e.getMessage());
    }
    return null;

  }

  /**
   * Return all badges within the DB
   *
   * @return a list of BadgeDTO
   */
  public List<BadgeDTO> getAllBadges() {
    // --- load all Rules
    List<BadgeEntity> badges = badgeStorage.getAllBadges();
    if (CollectionUtils.isNotEmpty(badges)) {
      return BadgeMapper.badgesToBadgeDTOs(badges);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Add Badge to DB
   *
   * @param badgeDTO : an object of type RuleDTO
   * @return BadgeDTO object
   * @throws ObjectAlreadyExistsException when badge already exists
   */
  public BadgeDTO addBadge(BadgeDTO badgeDTO) throws ObjectAlreadyExistsException {
    BadgeEntity badgeEntity = null;
    if (badgeDTO.getDomainDTO() == null) {
      badgeDTO.setEnabled(false);
      badgeEntity = badgeStorage.create(BadgeMapper.badgeDTOToBadge(badgeDTO));
    } else {
      badgeEntity = badgeStorage.findBadgeByTitleAndDomain(badgeDTO.getTitle(), badgeDTO.getDomainDTO().getId());
      if (badgeEntity == null) {
        if (!badgeDTO.getDomainDTO().isEnabled()) {
          badgeDTO.setEnabled(false);
        }
        badgeEntity = badgeStorage.create(BadgeMapper.badgeDTOToBadge(badgeDTO));
      } else if (badgeEntity.isDeleted()) {
        Long id = badgeEntity.getId();
        badgeEntity = BadgeMapper.badgeDTOToBadge(badgeDTO);
        badgeEntity.setId(id);
        if (badgeDTO.getDomainDTO() == null || !badgeDTO.getDomainDTO().isEnabled()) {
          badgeDTO.setEnabled(false);
        }
        badgeEntity = badgeStorage.update(badgeEntity);
      } else {
        throw new ObjectAlreadyExistsException("Badge already exists");
      }
    }
    return BadgeMapper.badgeToBadgeDTO(badgeEntity);
  }

  /**
   * Update Badge to DB
   *
   * @param badgeDTO : an instance of type BadgeDTO
   * @return BadgeDTO object
   * @throws ObjectAlreadyExistsException when badge already exists
   */
  public BadgeDTO updateBadge(BadgeDTO badgeDTO) throws ObjectAlreadyExistsException {
    BadgeEntity badgeEntity = null;
    badgeEntity = badgeStorage.findBadgeByTitleAndDomain(badgeDTO.getTitle(), badgeDTO.getDomainDTO().getId());
    if (badgeEntity != null && badgeDTO.getId() != null && badgeEntity.getId().longValue() != badgeDTO.getId().longValue()) {
      throw new ObjectAlreadyExistsException("Badge with same title and domain already exist");
    }
    if (badgeDTO.getDomainDTO() == null || !badgeDTO.getDomainDTO().isEnabled()) {
      badgeDTO.setEnabled(false);
    }
    badgeEntity = badgeStorage.update(BadgeMapper.badgeDTOToBadge(badgeDTO));
    return BadgeMapper.badgeToBadgeDTO(badgeEntity);
  }

  /**
   * Delete a BadgeEntity using the id
   *
   * @param id : badge id
   * @throws ObjectNotFoundException when badge doesn't exist
   */
  public void deleteBadge(Long id) throws ObjectNotFoundException {
    BadgeEntity badgeEntity = badgeStorage.find(id);
    if (badgeEntity != null) {
      badgeEntity.setDeleted(true);
      badgeStorage.update(badgeEntity);
    } else {
      throw new ObjectNotFoundException("Badge with id " + id + " not Found");
    }
  }

  public List<BadgeDTO> findBadgesByDomain(long domainId) {

    try {
      // --- load all Rules
      List<BadgeEntity> badges = badgeStorage.findBadgesByDomain(domainId);
      if (badges != null) {
        return BadgeMapper.badgesToBadgeDTOs(badges);
      }

    } catch (Exception e) {
      LOG.error("Error to find badges within domain id {}", domainId, e);
    }
    return Collections.emptyList();
  }

  public List<BadgeDTO> findEnabledBadgesByDomain(long badgeDomainId) {

    try {
      // --- load all Rules
      List<BadgeEntity> badges = badgeStorage.findEnabledBadgesByDomain(badgeDomainId);
      if (badges != null) {
        return BadgeMapper.badgesToBadgeDTOs(badges);
      }

    } catch (Exception e) {
      LOG.error("Error to find badges within domain id {}", badgeDomainId, e);
    }
    return Collections.emptyList();
  }

  /**
   * Get all Rules by with null DomainDTO from DB
   *
   * @return RuleDTO list
   */
  public List<BadgeDTO> getAllBadgesWithNullDomain() {
    try {
      List<BadgeEntity> rules = badgeStorage.getAllBadgesWithNullDomain();
      if (rules != null) {
        return BadgeMapper.badgesToBadgeDTOs(rules);
      }

    } catch (Exception e) {
      LOG.error("Error to find Badges", e);
      throw (e);
    }
    return Collections.emptyList();
  }
}
