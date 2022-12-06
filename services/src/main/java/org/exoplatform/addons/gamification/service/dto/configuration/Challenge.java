package org.exoplatform.addons.gamification.service.dto.configuration;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge implements Cloneable {

  /**
   * @param      id
   * @param      title
   * @param      description
   * @param      audience
   * @param      startDate
   * @param      endDate
   * @param      managers
   * @param      points
   * @param      program
   * @param      enabled
   * @deprecated             deprecated to use the full fields constructor
   *                         instead. Kept for API backward compatibility
   * @since                  05/112/2022
   */
  public Challenge(long id,
                   String title,
                   String description,
                   long audience,
                   String startDate,
                   String endDate,
                   List<Long> managers,
                   Long points,
                   String program,
                   boolean enabled) {
    this(id, title, description, audience, startDate, endDate, managers, points, program, 0, enabled);
  }

  public Challenge(long id,
                   String title,
                   String description,
                   long audience,
                   String startDate,
                   String endDate,
                   List<Long> managers,
                   Long points,
                   long programId,
                   boolean enabled) {
    this(id, title, description, audience, startDate, endDate, managers, points, null, programId, enabled);
  }

  private long       id;

  private String     title;

  private String     description;

  private long       audience;

  private String     startDate;

  private String     endDate;

  private List<Long> managers;

  private Long       points;

  /**
   * @deprecated not used for engagement center and kept for backward
   *             compatibility when engagement center is disabled
   */
  @Deprecated(forRemoval = true, since = "05/12/2022")
  private String     program;

  private long       programId;

  private boolean    enabled;

  @Override
  public Challenge clone() { // NOSONAR
    return new Challenge(id, title, description, audience, startDate, endDate, managers, points, program, programId, enabled);
  }

}
