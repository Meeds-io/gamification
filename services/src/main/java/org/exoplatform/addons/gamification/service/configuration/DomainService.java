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

import java.io.InputStream;
import java.util.List;

import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.Identity;

public interface DomainService {

  /**
   * Gets domains by filter.
   *
   * @param domainFilter {@link DomainFilter} used to filter results
   * @param offset index of the search
   * @param limit limit of results to return
   * @return A {@link List <DomainDTO>} object
   */
  List<DomainDTO> getDomainsByFilter(DomainFilter domainFilter, int offset, int limit);

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
   * @param aclIdentity Security identity of user attempting to create a
   *          program/domain
   * @return created {@link DomainDTO}
   * @throws IllegalAccessException when user is not authorized to create a
   *           domain for the designated owner defined in object
   */
  DomainDTO createDomain(DomainDTO domainDTO, Identity aclIdentity) throws IllegalAccessException;

  /**
   * Creates a new domain
   * 
   * @param domainDTO : an object of type DomainDTO
   * @return created {@link DomainDTO}
   */
  DomainDTO createDomain(DomainDTO domainDTO);

  /**
   * Update an existing Domain
   * 
   * @param domainDTO : an instance of type DomainDTO
   * @param aclIdentity Security identity of user attempting to update a
   *          program/domain
   * @return updated object {@link DomainDTO}
   * @throws IllegalArgumentException when user is not authorized to update the
   *           domain
   * @throws ObjectNotFoundException when the domain identified by its technical
   *           identifier is not found
   * @throws IllegalAccessException when user is not authorized to create a
   *           domain for the designated owner defined in object
   */
  DomainDTO updateDomain(DomainDTO domainDTO, Identity aclIdentity) throws ObjectNotFoundException, IllegalAccessException;

  /**
   * Update an existing Domain
   *
   * @param domainDTO : an instance of type DomainDTO
   *
   * @return updated object {@link DomainDTO}
   */
  DomainDTO updateDomain(DomainDTO domainDTO);
  /**
   * Deletes an existing domain by id
   *
   * @param domainId Domain technical identifier to delete
   * @param aclIdentity Security identity of user attempting to delete a domain
   *
   * @return deleted {@link DomainDTO}
   *
   * @throws IllegalAccessException when user is not authorized to delete domain
   * @throws ObjectNotFoundException domain not found
   */
  DomainDTO deleteDomainById(long domainId, Identity aclIdentity) throws ObjectNotFoundException, IllegalAccessException; // NOSONAR

  /**
   * Retrieves a domain identified by its technical identifier.
   * 
   * @param id : domain id
   * @return found {@link DomainDTO}
   */
  DomainDTO getDomainById(long id);

  /**
   * Count all domains by filter
   *
   * @param domainFilter {@link DomainFilter} used to filter domains
   * @return domains count
   */
  int countDomains(DomainFilter domainFilter);

  /**
   * Retrieves a cover identified by domain technical identifier.
   *
   * @param domainId domain unique identifier
   * @return found {@link InputStream}
   * @throws ObjectNotFoundException domain not found
   */
  InputStream getFileDetailAsStream(long domainId) throws ObjectNotFoundException;

  /**
   * Check whether user can add programs or not
   * 
   * @param aclIdentity Security identity of user
   * @return true if user has enough privileges to create a program, else false
   */
  boolean canAddDomain(Identity aclIdentity);

  /**
   * Check whether user can add programs or not
   * 
   * @param domainId technical identifier of domain/program
   * @param aclIdentity Security identity of user
   * @return true if user has enough privileges to create a program, else false
   */
  boolean canUpdateDomain(long domainId, Identity aclIdentity);

}
