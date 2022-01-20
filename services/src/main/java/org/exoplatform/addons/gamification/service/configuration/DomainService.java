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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class DomainService {

    private static final Log LOG = ExoLogger.getLogger(DomainService.class);

    protected final DomainDAO domainStorage;
    protected final DomainMapper domainMapper;
    protected final ListenerService listenerService;



  public DomainService() {
    this.domainStorage = CommonsUtils.getService(DomainDAO.class);
    this.domainMapper = CommonsUtils.getService(DomainMapper.class);
    this.listenerService = CommonsUtils.getService(ListenerService.class);

  }

    /**
     * Return all domains within the DB
     * @return a list of DomainDTO
     */
    @ExoTransactional
    public List<DomainDTO> getAllDomains() {
        try {
            //--- load all Domains
            List<DomainEntity> badges = domainStorage.getAllDomains();
            if (badges != null) {
                return domainMapper.domainssToDomainDTOs(badges);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Domains", e);
            throw (e);
        }
    }

    /**
     * Return enabled domains within the DB
     * @return a list of enabled DomainDTO
     */
    public List<DomainDTO> getEnabledDomains() {
        try {
            //--- load all Domains
            List<DomainEntity> badges = domainStorage.getEnabledDomains();
            if (badges != null) {
                return domainMapper.domainssToDomainDTOs(badges);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Domains", e);
            throw (e);
        }
    }

    /**
     * Find a DomainEntity by title
     * @param domainTitle : domain title
     * @return an instance DomainDTO
     */
    @ExoTransactional
    public DomainDTO findDomainByTitle(String domainTitle) {

        try {
            //--- Get Entity from DB
            DomainEntity entity = domainStorage.findDomainByTitle(domainTitle);
            //--- Convert Entity to DTO
            if (entity != null) {
                return domainMapper.domainToDomainDTO(entity);
            }else{
                return null;
            }

        } catch (Exception e) {
            LOG.error("Error to find Domain entity with title : {}", domainTitle, e);
            throw (e);
        }

    }

    /**
     * Add Domain to DB
     * @param domainDTO : an object of type DomainDTO
     * @return BadgeDTO object
     */
    @ExoTransactional
    public DomainDTO addDomain(DomainDTO domainDTO) throws PersistenceException {

        DomainEntity domainEntity = null;

        try {
            domainEntity = domainStorage.findDomainByTitle(domainDTO.getTitle());
            if(domainEntity==null){
                domainEntity = domainMapper.domainDTOToDomain(domainDTO);
                domainEntity.setCreatedDate(new Date());
                domainEntity.setLastModifiedDate(new Date());
                domainEntity = domainStorage.create(domainEntity);
            }else if(domainEntity.isDeleted()){
                Long id = domainEntity.getId();
                domainEntity = domainMapper.domainDTOToDomain(domainDTO);
                domainEntity.setId(id);
                domainEntity.setLastModifiedDate(new Date());
                domainEntity = domainStorage.update(domainEntity);
            }else{
                throw(new EntityExistsException());
            }

        } catch (Exception e) {
            LOG.error("Error to create domain with title {}", domainDTO.getTitle() , e);
            throw(e);
        }
        return domainMapper.domainToDomainDTO(domainEntity);
    }

    /**
     * Update Domain
     * @param domainDTO : an instance of type DomainDTO
     * @return DomainDTO object
     */
    @ExoTransactional
    public DomainDTO updateDomain(DomainDTO domainDTO) throws Exception {

        DomainEntity domainEntity = null;

        try {
            Boolean oldValue = domainStorage.find(domainDTO.getId()).isEnabled();
            domainEntity = domainMapper.domainDTOToDomain(domainDTO);
            domainEntity.setLastModifiedDate(new Date());
            domainEntity = domainStorage.update(domainEntity);
            if(oldValue && !domainDTO.isEnabled() ){
                listenerService.broadcast("exo.gamification.domain.action", domainEntity, "disable");
            }
            if(!oldValue&&domainDTO.isEnabled() ){
                listenerService.broadcast("exo.gamification.domain.action", domainEntity, "enable");
            }
            return domainMapper.domainToDomainDTO(domainEntity);

        } catch (Exception e) {
            LOG.error("Error to update domain {} with title {}", domainDTO.getId() , domainDTO.getTitle() , e);
            throw (e);
        }


    }

    /**
     * Delete a DomainEntity using the id
     * @param id : domain id
     */
    @ExoTransactional
    public void deleteDomain (Long id) throws Exception {

        try {
            DomainEntity domainEntity = domainStorage.find(id);
            if(domainEntity!=null){
                domainEntity.setDeleted(true);
                domainStorage.update(domainEntity);
                listenerService.broadcast("exo.gamification.domain.action", domainEntity, "delete");
            }else{
                LOG.warn("Domain {} not Found",id);
                throw(new EntityNotFoundException());
            }

        } catch (Exception e) {
            LOG.error("Error to delete domain {}", id, e);
            throw (e);
        }


    }


}
