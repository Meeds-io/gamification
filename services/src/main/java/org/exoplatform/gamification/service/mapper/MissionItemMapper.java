package org.exoplatform.gamification.service.mapper;

import org.exoplatform.gamification.entities.domain.effective.MissionItemEntity;
import org.exoplatform.gamification.service.dto.effective.MissionItemDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MissionItemMapper {
    public MissionItemMapper() {
    }

    public MissionItemDTO missionToMissionDTO(MissionItemEntity mission) {
        return new MissionItemDTO(mission);
    }

    public List<MissionItemDTO> missionsToMissionDTOs(List<MissionItemEntity> missions) {
        return missions.stream()
                .filter(Objects::nonNull)
                .map(this::missionToMissionDTO)
                .collect(Collectors.toList());
    }

    public MissionItemEntity missionDTOToMission(MissionItemDTO missionDTO) {

        if (missionDTO == null) {
            return null;
        } else {

            MissionItemEntity mission = new MissionItemEntity();

            mission.setTitle(missionDTO.getTitle());

            mission.setWonScore(missionDTO.getWonScore());

            mission.setStartDate(missionDTO.getStartDate());

            mission.setEndDate(missionDTO.getEndDate());

            return mission;
        }
    }

    public List<MissionItemEntity> missionDTOsToMissions(List<MissionItemDTO> missionDTOs) {
        return missionDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::missionDTOToMission)
                .collect(Collectors.toList());
    }

    public MissionItemEntity missionFromTitle(String title) {
        if (title == null) {
            return null;
        }
        MissionItemEntity mission = new MissionItemEntity();
        mission.setTitle(title);
        return mission;
    }
}
