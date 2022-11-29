package org.exoplatform.addons.gamification.rest.model;

import java.util.List;

import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.model.UserInfo;
import org.exoplatform.social.core.space.model.Space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChallengeRestEntity implements Cloneable {

  private long                         id;

  private String                       title;

  private String                       description;

  private Space                        space;

  private String                       startDate;

  private String                       endDate;

  private UserInfo                     userInfo;

  private List<UserInfo>               managers;

  private Long                         announcementsCount;

  private List<AnnouncementRestEntity> announcements;

  private Long                         points;

  private DomainDTO                    program;

  private boolean                      enabled;

  @Override
  public ChallengeRestEntity clone() { // NOSONAR
    return new ChallengeRestEntity(id,
                                   title,
                                   description,
                                   space,
                                   startDate,
                                   endDate,
                                   userInfo,
                                   managers,
                                   announcementsCount,
                                   announcements,
                                   points,
                                   program,
                                   enabled);
  }
}
