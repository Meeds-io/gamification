package org.exoplatform.gamification.service.mapper;

import org.exoplatform.gamification.entities.domain.effective.GamificationEntity;
import org.exoplatform.gamification.service.dto.effective.GamificationDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GamificationMapper {

    public GamificationMapper() {
    }

    public GamificationDTO gamificationToGamificationDTO(GamificationEntity gamification) {
        return new GamificationDTO(gamification);
    }

    public List<GamificationDTO> gamificationsToGamificationDTOs(List<GamificationEntity> gamifications) {
        return gamifications.stream()
                .filter(Objects::nonNull)
                .map(this::gamificationToGamificationDTO)
                .collect(Collectors.toList());
    }

    public GamificationEntity userDTOToUser(GamificationDTO gamificationDTO) {
        if (gamificationDTO == null) {
            return null;
        } else {

            GamificationEntity gamification = new GamificationEntity();

            gamification.setId(gamificationDTO.getId());

            gamification.setScore(gamificationDTO.getScore());

            gamification.setUsername(gamificationDTO.getUsername());

            gamification.setCreatedDate(gamificationDTO.getCreatedDate());

            gamification.setLastModifiedDate(gamificationDTO.getLastModifiedDate());

            return gamification;
        }
    }

    public List<GamificationEntity> gamificationDTOsToGamifications(List<GamificationDTO> gamificationDTOs) {
        return gamificationDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::userDTOToUser)
                .collect(Collectors.toList());
    }

    public GamificationEntity gamificationFromId(Long id) {
        if (id == null) {
            return null;
        }
        GamificationEntity gamification = new GamificationEntity();
        gamification.setId(id);
        return gamification;
    }



}
