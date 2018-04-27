package org.exoplatform.gamification.service.mapper;

import org.exoplatform.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.gamification.service.dto.configuration.BadgeDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BadgeMapper {

    public BadgeMapper() {
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
    public BadgeEntity badgeDTOToBadge(BadgeDTO badgeDTO) {
        if (badgeDTO == null) {
            return null;
        } else {
            BadgeEntity badge = new BadgeEntity();
            badge.setId(badgeDTO.getId());
            badge.setTitle(badgeDTO.getTitle());
            badge.setDescription(badgeDTO.getDescription());
            badge.setNeededScore(badgeDTO.getNeededScore());
            badge.setIcon(badgeDTO.getIcon());
            badge.setStartValidityDate(badgeDTO.getStartValidityDate());
            badge.setEndValidityDate(badgeDTO.getEndValidityDate());
            badge.setEnabled(badgeDTO.isEnabled());
            badge.setCreatedBy(badgeDTO.getCreatedBy());
            badge.setCreatedDate(badgeDTO.getCreatedDate());
            badge.setLastModifiedBy(badgeDTO.getLastModifiedBy());
            badge.setLastModifiedDate(badgeDTO.getLastModifiedDate());

            return badge;
        }
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
