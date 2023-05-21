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
package io.meeds.gamification.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.dao.BadgeDAO;
import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.mapper.BadgeMapper;

public class BadgeService {

  private static final Log      LOG = ExoLogger.getLogger(BadgeService.class);

  protected final ProgramStorage programStorage;

  protected final BadgeDAO       badgeDAO;

  public BadgeService(ProgramStorage programStorage,
                      BadgeDAO badgeDAO) {
    this.programStorage = programStorage;
    this.badgeDAO = badgeDAO;
  }

  /**
   * Find a BadgeEntity by title
   *
   * @param  badgeTitle : badge title
   * @return            an instance BadgeDTO
   */
  public BadgeDTO findBadgeByTitle(String badgeTitle) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeDAO.findBadgeByTitle(badgeTitle);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.fromEntity(programStorage, entity);
      }

    } catch (Exception e) {
      LOG.error("Error to find Badge entity with title : {}", badgeTitle, e.getMessage());
    }
    return null;

  }

  /**
   * Find a BadgeEntity by id
   *
   * @param  badgeId : badge id
   * @return         an instance BadgeDTO
   */
  public BadgeDTO findBadgeById(Long badgeId) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeDAO.find(badgeId);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.fromEntity(programStorage, entity);
      }

    } catch (Exception e) {
      LOG.error("Error to find Badge entity with id : {}", badgeId, e.getMessage());
    }
    return null;

  }

  /**
   * Find a BadgeEntity by title
   *
   * @param  badgeTitle : badge title
   * @param  domainId   : badge domain id
   * @return            an instance BadgeDTO
   */
  public BadgeDTO findBadgeByTitleAndDomain(String badgeTitle, long domainId) {

    try {
      // --- Get Entity from DB
      BadgeEntity entity = badgeDAO.findBadgeByTitleAndDomain(badgeTitle, domainId);
      // --- Convert Entity to DTO
      if (entity != null) {
        return BadgeMapper.fromEntity(programStorage, entity);
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
    List<BadgeEntity> badges = badgeDAO.getAllBadges();
    if (CollectionUtils.isNotEmpty(badges)) {
      return BadgeMapper.fromEntities(programStorage, badges);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Add Badge to DB
   *
   * @param  badgeDTO                     : an object of type RuleDTO
   * @return                              BadgeDTO object
   * @throws ObjectAlreadyExistsException when badge already exists
   */
  public BadgeDTO addBadge(BadgeDTO badgeDTO) throws ObjectAlreadyExistsException {
    BadgeEntity badgeEntity = null;
    if (badgeDTO.getProgram() == null) {
      badgeDTO.setEnabled(false);
      badgeEntity = badgeDAO.create(BadgeMapper.toEntity(badgeDTO));
    } else {
      badgeEntity = badgeDAO.findBadgeByTitleAndDomain(badgeDTO.getTitle(), badgeDTO.getProgram().getId());
      if (badgeEntity == null) {
        if (!badgeDTO.getProgram().isEnabled()) {
          badgeDTO.setEnabled(false);
        }
        badgeEntity = badgeDAO.create(BadgeMapper.toEntity(badgeDTO));
      } else if (badgeEntity.isDeleted()) {
        Long id = badgeEntity.getId();
        badgeEntity = BadgeMapper.toEntity(badgeDTO);
        badgeEntity.setId(id);
        if (badgeDTO.getProgram() == null || !badgeDTO.getProgram().isEnabled()) {
          badgeDTO.setEnabled(false);
        }
        badgeEntity = badgeDAO.update(badgeEntity);
      } else {
        throw new ObjectAlreadyExistsException("Badge already exists");
      }
    }
    return BadgeMapper.fromEntity(programStorage, badgeEntity);
  }

  /**
   * Update Badge to DB
   *
   * @param  badgeDTO                     : an instance of type BadgeDTO
   * @return                              BadgeDTO object
   * @throws ObjectAlreadyExistsException when badge already exists
   */
  public BadgeDTO updateBadge(BadgeDTO badgeDTO) throws ObjectAlreadyExistsException {
    BadgeEntity badgeEntity = null;
    badgeEntity = badgeDAO.findBadgeByTitleAndDomain(badgeDTO.getTitle(), badgeDTO.getProgram().getId());
    if (badgeEntity != null && badgeDTO.getId() != null && badgeEntity.getId().longValue() != badgeDTO.getId().longValue()) {
      throw new ObjectAlreadyExistsException("Badge with same title and domain already exist");
    }
    if (badgeDTO.getProgram() == null || !badgeDTO.getProgram().isEnabled()) {
      badgeDTO.setEnabled(false);
    }
    badgeEntity = badgeDAO.update(BadgeMapper.toEntity(badgeDTO));
    return BadgeMapper.fromEntity(programStorage, badgeEntity);
  }

  /**
   * Delete a BadgeEntity using the id
   *
   * @param  id                      : badge id
   * @throws ObjectNotFoundException when badge doesn't exist
   */
  public void deleteBadge(Long id) throws ObjectNotFoundException {
    BadgeEntity badgeEntity = badgeDAO.find(id);
    if (badgeEntity != null) {
      badgeEntity.setDeleted(true);
      badgeDAO.update(badgeEntity);
    } else {
      throw new ObjectNotFoundException("Badge with id " + id + " not Found");
    }
  }

  public List<BadgeDTO> findBadgesByDomain(long domainId) {

    try {
      // --- load all Rules
      List<BadgeEntity> badges = badgeDAO.findBadgesByDomain(domainId);
      if (badges != null) {
        return BadgeMapper.fromEntities(programStorage, badges);
      }

    } catch (Exception e) {
      LOG.error("Error to find badges within domain id {}", domainId, e);
    }
    return Collections.emptyList();
  }

  public List<BadgeDTO> findEnabledBadgesByDomain(long badgeDomainId) {

    try {
      // --- load all Rules
      List<BadgeEntity> badges = badgeDAO.findEnabledBadgesByDomain(badgeDomainId);
      if (badges != null) {
        return BadgeMapper.fromEntities(programStorage, badges);
      }

    } catch (Exception e) {
      LOG.error("Error to find badges within domain id {}", badgeDomainId, e);
    }
    return Collections.emptyList();
  }

  /**
   * Get all Rules by with null ProgramDTO from DB
   *
   * @return RuleDTO list
   */
  public List<BadgeDTO> getAllBadgesWithNullDomain() {
    try {
      List<BadgeEntity> badgeEntities = badgeDAO.getAllBadgesWithNullDomain();
      if (badgeEntities != null) {
        return BadgeMapper.fromEntities(programStorage, badgeEntities);
      }
    } catch (Exception e) {
      LOG.error("Error to find Badges", e);
      throw (e);
    }
    return Collections.emptyList();
  }
}
