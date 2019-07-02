package org.exoplatform.addons.gamification.service.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


public class DomainMapper {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final Log LOG = ExoLogger.getLogger(DomainMapper.class);

    public DomainMapper() {
    }

    public DomainDTO domainToDomainDTO(DomainEntity domain) {
        return new DomainDTO(domain);
    }

    public List<DomainDTO> domainssToDomainDTOs(List<DomainEntity> domains) {
        return domains.stream()
                .filter(Objects::nonNull)
                .map((DomainEntity domain) -> domainToDomainDTO(domain))
                .collect(Collectors.toList());
    }

    public DomainEntity domainDTOToDomain(DomainDTO domainDTO) {

        if (domainDTO == null) {
            return null;
        } else {
            DomainEntity domain = new DomainEntity();
            domain.setId(domainDTO.getId());
            domain.setTitle(domainDTO.getTitle());
            domain.setDescription(domainDTO.getDescription());
            domain.setCreatedBy(domainDTO.getCreatedBy());
            domain.setLastModifiedBy(domainDTO.getLastModifiedBy());
            if (domainDTO.getLastModifiedDate() != null) {
                try {
                    domain.setLastModifiedDate(formatter.parse(domainDTO.getLastModifiedDate()));
                } catch (ParseException e) {
                    LOG.warn("Cannot parse the domain {} last modified date", domain.getId(), e);
                }
            }
            return domain;
        }
    }

    public List<DomainEntity> domainDTOsToDomains(List<DomainDTO> domainDTOs) {
        return domainDTOs.stream()
                .filter(Objects::nonNull)
                .map((DomainDTO domainDTO) -> domainDTOToDomain(domainDTO))
                .collect(Collectors.toList());
    }

    public DomainEntity domainFromId(Long id) {
        if (id == null) {
            return null;
        }
        DomainEntity domain = new DomainEntity();
        domain.setId(id);
        return domain;
    }

}
