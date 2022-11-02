package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge implements Cloneable {
  private long       id;

  private String     title;

  private String     description;

  private long       audience;

  private String     startDate;

  private String     endDate;

  private List<Long> managers;

  private Long       points;

  private String     program;

  private boolean    enabled;

  @Override
  public Challenge clone() { // NOSONAR
    return new Challenge(id, title, description, audience, startDate, endDate, managers, points, program, enabled);
  }

}
