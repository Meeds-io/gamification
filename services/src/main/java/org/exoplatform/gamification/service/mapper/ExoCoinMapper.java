package org.exoplatform.gamification.service.mapper;

import org.exoplatform.gamification.entities.domain.effective.ExoCoinEntity;
import org.exoplatform.gamification.service.dto.effective.ExoCoinDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExoCoinMapper {

    public ExoCoinMapper() {
    }

    public ExoCoinDTO exoCoinToeXoCoinDTO(ExoCoinEntity eXoCoin) {
        return new ExoCoinDTO(eXoCoin);
    }

    public List<ExoCoinDTO> coinsToCoinDTOs(List<ExoCoinEntity> coins) {
        return coins.stream()
                .filter(Objects::nonNull)
                .map(this::exoCoinToeXoCoinDTO)
                .collect(Collectors.toList());
    }
    public ExoCoinEntity coinDTOToCoins(ExoCoinDTO coinDTO) {
        if (coinDTO == null) {
            return null;
        } else {
            ExoCoinEntity coin = new ExoCoinEntity();
            coin.setType (coinDTO.getTitle());
            coin.setHash (coinDTO.getHash());
            coin.setCount (coinDTO.getCount());
            return coin;
        }
    }

    public List<ExoCoinEntity> BadgeDTOsToBadges(List<ExoCoinDTO> coinDTOs) {
        return coinDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::coinDTOToCoins)
                .collect(Collectors.toList());
    }

    public ExoCoinEntity coinFromTitle(String title) {
        if (title == null) {
            return null;
        }
        ExoCoinEntity coin = new ExoCoinEntity();
        coin.setType(title);
        return coin;
    }
}
