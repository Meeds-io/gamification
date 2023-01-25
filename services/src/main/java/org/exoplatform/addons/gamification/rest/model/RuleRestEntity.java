package org.exoplatform.addons.gamification.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.UserInfo;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RuleRestEntity implements Cloneable {

  protected Long                       id;

  protected String                     title;

  protected String                     description;

  protected int                        score;

  protected String                     area;

  private DomainDTO                    domainDTO;

  protected boolean                    enabled;

  protected boolean                    deleted;

  private String                       createdBy;

  private String                       createdDate;

  private String                       lastModifiedBy;

  private String                       event;

  private String                       lastModifiedDate;

  private long                         audience;

  private String                       startDate;

  private String                       endDate;

  private EntityType                   type;

  private List<Long>                   managers;

  private List<AnnouncementRestEntity> announcements;

  private long                         announcementsCount;

  private UserInfo                     userInfo;

  @Override
  public RuleRestEntity clone() { // NOSONAR
    return new RuleRestEntity(id,
                              title,
                              description,
                              score,
                              area,
                              domainDTO,
                              enabled,
                              deleted,
                              createdBy,
                              createdDate,
                              lastModifiedBy,
                              event,
                              lastModifiedDate,
                              audience,
                              startDate,
                              endDate,
                              type,
                              managers,
                              announcements,
                              announcementsCount,
                              userInfo);
  }

}
