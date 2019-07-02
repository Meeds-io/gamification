package org.exoplatform.addons.gamification.service.mapper;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BadgeMapper {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private DomainMapper domainMapper;

    public BadgeMapper(DomainMapper domainMapper) {
        this.domainMapper=domainMapper;
    }

    public BadgeDTO badgeToBadgeDTO(BadgeEntity badge) {
        return new BadgeDTO(badge);
    }

    public List<BadgeDTO> badgesToBadgeDTOs(List<BadgeEntity> badges) {
        return badges.stream()
                .filter(Objects::nonNull)
                .map(this::badgeToBadgeDTO)
                .collect(Collectors.toList());
    }
    public BadgeEntity badgeDTOToBadge(BadgeDTO badgeDTO)  {
        try {
            if (badgeDTO == null) {
                return null;
            } else {
                BadgeEntity badge = new BadgeEntity();
                badge.setId(badgeDTO.getId());
                badge.setTitle(badgeDTO.getTitle());
                badge.setDescription(badgeDTO.getDescription());
                badge.setNeededScore(badgeDTO.getNeededScore());
                badge.setDomain(badgeDTO.getDomain());
                //TODO save an inputStream
                //badge.setIcon(badgeDTO.getIcon());
                badge.setIconFileId(badgeDTO.getIconFileId());
                if (badgeDTO.getStartValidityDate() != null) {
                    badge.setStartValidityDate(formatter.parse(badgeDTO.getStartValidityDate()));
                }
                if (badgeDTO.getEndValidityDate() != null) {
                    badge.setEndValidityDate(formatter.parse(badgeDTO.getEndValidityDate()));
                }
                badge.setEnabled(badgeDTO.isEnabled());
                badge.setCreatedBy(badgeDTO.getCreatedBy());
                if (badgeDTO.getCreatedDate() != null) {
                    badge.setCreatedDate(formatter.parse(badgeDTO.getCreatedDate()));
                }
                badge.setLastModifiedBy(badgeDTO.getLastModifiedBy());

                if (badgeDTO.getLastModifiedDate() != null) {
                    badge.setLastModifiedDate(formatter.parse(badgeDTO.getLastModifiedDate()));
                }
                badge.setDomainEntity(domainMapper.domainDTOToDomain(badgeDTO.getDomainDTO()));
                return badge;
            }

        } catch (ParseException pe) {


        }
        return null;

    }

    public List<BadgeEntity> badgeDTOsToBadges(List<BadgeDTO> BadgeDTOs) {
        return BadgeDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::badgeDTOToBadge)
                .collect(Collectors.toList());
    }

    public BadgeEntity badgeFromId(Long id) {
        if (id == null) {
            return null;
        }
        BadgeEntity badge = new BadgeEntity();
        badge.setId(id);
        return badge;
    }
}
