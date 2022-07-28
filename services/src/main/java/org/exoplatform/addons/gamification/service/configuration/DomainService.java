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
package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.commons.exception.ObjectNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DomainService {

  /**
   * Retrieves all domains by user.
   *
   * @param offset index of the search
   * @param limit limit of results to return
   * @param domainFilter username accessing domains
   * @return A {@link List <DomainDTO>} object
   */
  List<DomainDTO> getAllDomains(int offset, int limit, DomainFilter domainFilter);

  /**
   * Return enabled domains within the DB
   *
   * @return A {@link List <DomainDTO>} object
   */
  List<DomainDTO> getEnabledDomains();

  /**
   * Find an enabled domain by title
   * 
   * @param domainTitle : domain title
   * @return found {@link DomainDTO}
   */
  DomainDTO findEnabledDomainByTitle(String domainTitle);

  /**
   * Find a domain by title
   * 
   * @param domainTitle : domain title
   * @return found {@link DomainDTO}
   */
  DomainDTO getDomainByTitle(String domainTitle);

  /**
   * Creates a new domain
   * 
   * @param domainDTO : an object of type DomainDTO
   * @param username Username creating domain
   * @param isAdministrator current user is Administrator
   * @return created {@link DomainDTO}
   * @throws IllegalAccessException when user is not authorized to create a domain
   *           for the designated owner defined in object
   */
  DomainDTO addDomain(DomainDTO domainDTO, String username, boolean isAdministrator) throws Exception; // NOSONAR

  /**
   * Update an existing Domain
   * 
   * @param username User name updating domain
   * @param domainDTO : an instance of type DomainDTO
   * @param isAdministrator current user is Administrator
   * @return updated object {@link DomainDTO}
   * @throws IllegalArgumentException when user is not authorized to update the
   *           domain
   * @throws ObjectNotFoundException when the domain identified by its technical
   *           identifier is not found
   * @throws IllegalAccessException when user is not authorized to create a domain
   *           for the designated owner defined in object
   */
  DomainDTO updateDomain(DomainDTO domainDTO, String username, boolean isAdministrator) throws Exception; // NOSONAR

  /**
   * Delete a DomainEntity using the id
   * 
   * @param id : domain id
   * @param username Username who want to delete domain * @param isAdministrator
   *          current user is Administrator
   * @throws IllegalAccessException when user is not authorized to delete domain
   * @throws ObjectNotFoundException domain not found
   */
  void deleteDomain(Long id, String username, boolean isAdministrator) throws Exception; // NOSONAR

  /**
   * Retrieves a domain identified by its technical identifier.
   * 
   * @param id : domain id
   * @return found {@link DomainDTO}
   */
  DomainDTO getDomainById(Long id);

  /**
   * Count all domains by filter
   *
   * @param domainFilter {@link DomainFilter} used to filter domains
   * @return domains count
   */
  int countDomains(DomainFilter domainFilter);

  /**
   * Retrieves a cover identified by its technical identifier.
   *
   * @param coverId : cover id
   * @return found {@link InputStream}
   */
  InputStream getFileDetailAsStream(long coverId) throws IOException;
}
