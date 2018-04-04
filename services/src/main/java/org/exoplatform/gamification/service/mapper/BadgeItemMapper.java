package org.exoplatform.gamification.service.mapper;

import org.exoplatform.gamification.entities.domain.effective.BadgeItemEntity;
import org.exoplatform.gamification.service.dto.effective.BadgeItemDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BadgeItemMapper {

    public BadgeItemMapper() {
    }

    public BadgeItemDTO badgeToBadgeDTO(BadgeItemEntity badge) {
        return new BadgeItemDTO(badge);
    }

    public List<BadgeItemDTO> badgesToBadgesDTOs(List<BadgeItemEntity> badges) {
        return badges.stream()
                .filter(Objects::nonNull)
                .map(this::badgeToBadgeDTO)
                .collect(Collectors.toList());
    }

    public BadgeItemEntity badgeDTOToBadge(BadgeItemDTO badgeDTO) {

        if (badgeDTO == null) {
            return null;
        } else {

            BadgeItemEntity badge = new BadgeItemEntity();

            badge.setTitle(badgeDTO.getTitle());
            badge.setCurrentScore(badgeDTO.getCurrentScore());
            badge.setRequiredScore(badgeDTO.getRequiredScore());
            badge.setAffectedDate(badgeDTO.getAffectedDate());

            return badge;
        }
    }

    public List<BadgeItemEntity> badgeDTOsToBadges(List<BadgeItemDTO> badgeDTOs) {
        return badgeDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::badgeDTOToBadge)
                .collect(Collectors.toList());
    }

    public BadgeItemEntity badgeFromTitle(String title) {
        if (title == null) {
            return null;
        }
        BadgeItemEntity mission = new BadgeItemEntity();
        mission.setTitle(title);
        return mission;
    }
}
