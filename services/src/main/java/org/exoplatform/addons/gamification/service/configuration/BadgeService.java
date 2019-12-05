package org.exoplatform.addons.gamification.service.configuration;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.mapper.BadgeMapper;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAO;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

public class BadgeService {

    private static final Log LOG = ExoLogger.getLogger(BadgeService.class);

    protected final BadgeDAO badgeStorage;
    protected final BadgeMapper badgeMapper;

    public BadgeService() {
        this.badgeStorage = CommonsUtils.getService(BadgeDAO.class);
        this.badgeMapper = CommonsUtils.getService(BadgeMapper.class);

    }

    /**
     * Find a BadgeEntity by title
     * @param badgeTitle : badge title
     * @return an instance BadgeDTO
     */
    @ExoTransactional
    public BadgeDTO findBadgeByTitle(String badgeTitle) {

        try {
            //--- Get Entity from DB
            BadgeEntity entity = badgeStorage.findBadgeByTitle(badgeTitle);
            //--- Convert Entity to DTO
            if (entity != null) {
                return badgeMapper.badgeToBadgeDTO(entity);
            }

        } catch (Exception e) {
            LOG.error("Error to find Badge entity with title : {}", badgeTitle, e.getMessage());
        }
        return null;

    }
    /**
     * Find a BadgeEntity by id
     * @param badgeId : badge id
     * @return an instance BadgeDTO
     */
    @ExoTransactional
    public BadgeDTO findBadgeById(Long badgeId) {

        try {
            //--- Get Entity from DB
            BadgeEntity entity = badgeStorage.find(badgeId);
            //--- Convert Entity to DTO
            if (entity != null) {
                return badgeMapper.badgeToBadgeDTO(entity);
            }

        } catch (Exception e) {
            LOG.error("Error to find Badge entity with id : {}", badgeId, e.getMessage());
        }
        return null;

    }
    /**
     * Find a BadgeEntity by title
     * @param badgeTitle : badge title
     * @param domain : badge domain
     * @return an instance BadgeDTO
     */
    @ExoTransactional
    public BadgeDTO findBadgeByTitleAndDomain(String badgeTitle, String domain) {

        try {
            //--- Get Entity from DB
            BadgeEntity entity = badgeStorage.findBadgeByTitleAndDomain(badgeTitle,domain);
            //--- Convert Entity to DTO
            if (entity != null) {
                return badgeMapper.badgeToBadgeDTO(entity);
            }

        } catch (Exception e) {
            LOG.error("Error to find Badge entity with title : {}", badgeTitle, e.getMessage());
        }
        return null;

    }

    /**
     * Return all badges within the DB
     * @return a list of BadgeDTO
     */
    public List<BadgeDTO> getAllBadges() {
        try {
            //--- load all Rules
            List<BadgeEntity> badges = badgeStorage.getAllBadges();
            if (badges != null) {
                return badgeMapper.badgesToBadgeDTOs(badges);
            }

        } catch (Exception e) {
            LOG.error("Error to find Badges", e.getMessage());
        }
        return null;

    }


    /**
     * Add Badge to DB
     * @param badgeDTO : an object of type RuleDTO
     * @return BadgeDTO object
     */
    @ExoTransactional
    public BadgeDTO addBadge (BadgeDTO badgeDTO) {

        BadgeEntity badgeEntity = null;
        try {
            if(badgeDTO.getDomainDTO()!=null){
                badgeDTO.setDomain(badgeDTO.getDomainDTO().getTitle());
            }
            badgeEntity = badgeStorage.findBadgeByTitleAndDomain(badgeDTO.getTitle(),badgeDTO.getDomain());
            if(badgeEntity==null){
                if(badgeDTO.getDomainDTO()==null || !badgeDTO.getDomainDTO().isEnabled()){
                    badgeDTO.setEnabled(false);
                }
                badgeEntity = badgeStorage.create(badgeMapper.badgeDTOToBadge(badgeDTO));
            }else if(badgeEntity.isDeleted()){
                Long id = badgeEntity.getId();
                badgeEntity = badgeMapper.badgeDTOToBadge(badgeDTO);
                badgeEntity.setId(id);
                if(badgeDTO.getDomainDTO()==null || !badgeDTO.getDomainDTO().isEnabled()){
                    badgeDTO.setEnabled(false);
                }
                badgeEntity = badgeStorage.update(badgeEntity);
            }else{
                throw(new EntityExistsException());
            }

        } catch (Exception e) {
            LOG.error("Error to create badge with title {}", badgeDTO.getTitle() , e);
            throw(e);
        }

        return badgeMapper.badgeToBadgeDTO(badgeEntity);
    }

    /**
     * Update Badge to DB
     * @param badgeDTO : an instance of type BadgeDTO
     * @return BadgeDTO object
     */
    @ExoTransactional
    public BadgeDTO updateBadge (BadgeDTO badgeDTO) {

        BadgeEntity badgeEntity = null;
        try {
            if(badgeDTO.getDomainDTO()!=null){
                badgeDTO.setDomain(badgeDTO.getDomainDTO().getTitle());
            }
            badgeEntity = badgeStorage.findBadgeByTitleAndDomain(badgeDTO.getTitle(),badgeDTO.getDomain());
            if(  badgeEntity!=null && badgeEntity.getId()!=badgeDTO.getId()){
                throw(new EntityExistsException("Badge with same title and domain already exist"));
            }
                if(badgeDTO.getDomainDTO()==null || !badgeDTO.getDomainDTO().isEnabled()){
                    badgeDTO.setEnabled(false);
                }
                badgeEntity = badgeStorage.update(badgeMapper.badgeDTOToBadge(badgeDTO));

        } catch (Exception e) {
            LOG.error("Error to update with title {}", badgeDTO.getTitle() , e);
            throw(e);
        }

        return badgeMapper.badgeToBadgeDTO(badgeEntity);
    }

    /**
     * Delete a BadgeEntity using the id
     * @param id : badge id
     */
    @ExoTransactional
    public void deleteBadge (Long id) {

        try {

            BadgeEntity badgeEntity = badgeStorage.find(id);
            if(badgeEntity!=null){
                badgeEntity.setDeleted(true);
                badgeStorage.update(badgeEntity);
            }else{
                LOG.warn("Badge {} not Found",id);
                throw(new EntityNotFoundException());
            }


        } catch (Exception e) {
            LOG.error("Error to delete badge with title {}", id, e);
        }


    }

    @ExoTransactional
    public List<BadgeDTO> findBadgesByDomain(String badgeDomain) {

        try {
            //--- load all Rules
            List<BadgeEntity> badges = badgeStorage.findBadgesByDomain(badgeDomain);
            if (badges != null) {
                return badgeMapper.badgesToBadgeDTOs(badges);
            }

        } catch (Exception e) {
            LOG.error("Error to find badges within domain {}", badgeDomain, e);
        }
        return null;


    }

    @ExoTransactional
    public List<BadgeDTO> findEnabledBadgesByDomain(String badgeDomain) {

        try {
            //--- load all Rules
            List<BadgeEntity> badges = badgeStorage.findEnabledBadgesByDomain(badgeDomain);
            if (badges != null) {
                return badgeMapper.badgesToBadgeDTOs(badges);
            }

        } catch (Exception e) {
            LOG.error("Error to find badges within domain {}", badgeDomain, e);
        }
        return null;


    }


    /**
     * Get all Rules by with null DomainDTO from DB
     * @return RuleDTO list
     */
    @ExoTransactional
    public List<BadgeDTO> getAllBadgesWithNullDomain() throws Exception{
        try {
            List<BadgeEntity> rules =  badgeStorage.getAllBadgesWithNullDomain();
            if (rules != null) {
                return badgeMapper.badgesToBadgeDTOs(rules);
            }

        } catch (Exception e) {
            LOG.error("Error to find Badges",e);
            throw (e);
        }
        return null;
    }


    /**
     * Get all Domains from Rules from DB
     * @return String list
     */
    @ExoTransactional
    public List<String> getDomainListFromBadges() {
        return badgeStorage.getDomainList();
    }
}
