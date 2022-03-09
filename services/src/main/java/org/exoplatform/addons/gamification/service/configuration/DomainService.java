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

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface DomainService {

  /**
   * Return all domains within the DB
   * 
   * @return a list of DomainDTO
   */
   List<DomainDTO> getAllDomains() ;

  /**
   * Return enabled domains within the DB
   * 
   * @return a list of enabled DomainDTO
   */
   List<DomainDTO> getEnabledDomains() ;

  /**
   * Find a DomainEntity by title
   * 
   * @param domainTitle : domain title
   * @return an instance DomainDTO
   */
   DomainDTO findDomainByTitle(String domainTitle);

  /**
     * Add Domain to DB
     * @param domainDTO : an object of type DomainDTO
     * @return BadgeDTO object
     */
     DomainDTO addDomain(DomainDTO domainDTO) ;

  /**
   * Update Domain
   * 
   * @param domainDTO : an instance of type DomainDTO
   * @return DomainDTO object
   */
  DomainDTO updateDomain(DomainDTO domainDTO) throws ObjectNotFoundException;

  /**
     * Delete a DomainEntity using the id
     * @param id : domain id
     */
    void deleteDomain (Long id) throws EntityNotFoundException;

}
