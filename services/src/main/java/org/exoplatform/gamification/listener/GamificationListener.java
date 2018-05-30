package org.exoplatform.gamification.listener;

import org.exoplatform.gamification.GamificationConstant;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.service.dto.effective.GamificationContextHolder;

import java.util.List;

public interface GamificationListener extends GamificationConstant {

    List<GamificationContextHolder> gamify(RuleDTO ruleDto, String actor) throws Exception;

}
