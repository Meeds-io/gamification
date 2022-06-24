package org.exoplatform.addons.gamification.service.dto.configuration;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleFilter implements Serializable {

  private static final long serialVersionUID = 7863115218512008695L;

  private String            username;

  private String            term;

  private long              domainId;

  private List<Long>        spaceIds;

}
