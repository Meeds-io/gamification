package org.exoplatform.addons.gamification.service.configuration;

import java.util.List;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

public class DomainService {

    private static final Log LOG = ExoLogger.getLogger(DomainService.class);

    protected final DomainDAO domainStorage;
    protected final DomainMapper domainMapper;

    public DomainService(DomainDAO domainDAO, DomainMapper domainMapper) {
        this.domainStorage = domainDAO;
        this.domainMapper = domainMapper;

    }

    /**
     * Return all domains within the DB
     * @return a list of DomainDTO
     */
    public List<DomainDTO> getAllDomains() {
        try {
            //--- load all Domains
            List<DomainEntity> badges = domainStorage.findAll();
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
            DomainEntity entity = domainStorage.findBadgeByTitle(domainTitle);
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
    public DomainDTO addDomain (DomainDTO domainDTO) throws PersistenceException {

        DomainEntity domainEntity = null;

        try {
            domainEntity = domainStorage.create(domainMapper.domainDTOToDomain(domainDTO));
            return domainMapper.domainToDomainDTO(domainEntity);
        } catch (Exception e) {
            LOG.error("Error to create badge with title {}", domainDTO.getTitle() , e);
            throw(e);
        }
    }

    /**
     * Update Domain
     * @param domainDTO : an instance of type DomainDTO
     * @return DomainDTO object
     */
    @ExoTransactional
    public DomainDTO updateBadge (DomainDTO domainDTO) throws PersistenceException  {

        DomainEntity domainEntity = null;

        try {
            domainEntity = domainStorage.update(domainMapper.domainDTOToDomain(domainDTO));
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
    public void deleteDomain (Long id) throws EntityNotFoundException{

        try {
            DomainEntity domainEntity = domainStorage.find(id);
            if(domainEntity!=null){
                domainStorage.delete(domainEntity);
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
