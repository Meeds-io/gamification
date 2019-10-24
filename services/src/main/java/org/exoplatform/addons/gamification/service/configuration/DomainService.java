package org.exoplatform.addons.gamification.service.configuration;

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
    public DomainDTO addDomain (DomainDTO domainDTO) throws PersistenceException {

        DomainEntity domainEntity = null;

        try {
            domainEntity = domainStorage.findDomainByTitle(domainDTO.getTitle());
            if(domainEntity==null){
                domainEntity = domainStorage.create(domainMapper.domainDTOToDomain(domainDTO));
            }else if(domainEntity.isDeleted()){
                Long id = domainEntity.getId();
                domainEntity = domainMapper.domainDTOToDomain(domainDTO);
                domainEntity.setId(id);
                domainEntity = domainStorage.update(domainEntity);
            }else{
                throw(new EntityExistsException());
            }

        } catch (Exception e) {
            LOG.error("Error to create badge with title {}", domainDTO.getTitle() , e);
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
            domainEntity = domainStorage.update(domainMapper.domainDTOToDomain(domainDTO));
            if(oldValue&&!domainDTO.isEnabled() ){
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
