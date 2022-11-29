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
package org.exoplatform.addons.gamification.service;

import java.util.List;

import org.exoplatform.addons.gamification.model.BadgeDTO;

public interface BadgeService {

    /**
     * Find a BadgeEntity by title
     * @param badgeTitle : badge title
     * @return an instance BadgeDTO
     */
    BadgeDTO findBadgeByTitle(String badgeTitle);

    /**
     * Find a BadgeEntity by id
     * @param badgeId : badge id
     * @return an instance BadgeDTO
     */
    BadgeDTO findBadgeById(Long badgeId);
    /**
     * Find a BadgeEntity by title
     * @param badgeTitle : badge title
     * @param domain : badge domain
     * @return an instance BadgeDTO
     */
    BadgeDTO findBadgeByTitleAndDomain(String badgeTitle, String domain);

    /**
     * Return all badges within the DB
     * @return a list of BadgeDTO
     */
    List<BadgeDTO> getAllBadges();


    /**
     * Add Badge to DB
     * @param badgeDTO : an object of type RuleDTO
     * @return BadgeDTO object
     */
    BadgeDTO addBadge(BadgeDTO badgeDTO);

    /**
     * Update Badge to DB
     * @param badgeDTO : an instance of type BadgeDTO
     * @return BadgeDTO object
     */
    BadgeDTO updateBadge(BadgeDTO badgeDTO);

    /**
     * Delete a BadgeEntity using the id
     * @param id : badge id
     */
    void deleteBadge(Long id);

    List<BadgeDTO> findBadgesByDomain(String badgeDomain);

    List<BadgeDTO> findEnabledBadgesByDomain(String badgeDomain);


    /**
     * Get all Rules by with null DomainDTO from DB
     * @return RuleDTO list
     */
    List<BadgeDTO> getAllBadgesWithNullDomain() throws Exception;


    /**
     * Get all Domains from Rules from DB
     * @return String list
     */
    List<String> getDomainListFromBadges();
}