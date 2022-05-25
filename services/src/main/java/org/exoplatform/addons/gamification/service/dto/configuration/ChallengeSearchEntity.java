package org.exoplatform.addons.gamification.service.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeSearchEntity {

    private long       id;

    private String     title;

    private String     description;

    private long       audience;

    private String     startDate;

    private String     endDate;

    private List<Long> managers;

    private Long       points;

    private Long     programId;

    @Override
    public ChallengeSearchEntity clone() { // NOSONAR
        return new ChallengeSearchEntity(id, title, description, audience, startDate, endDate, managers, points, programId);
    }

}
