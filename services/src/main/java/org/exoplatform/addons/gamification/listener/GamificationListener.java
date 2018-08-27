package org.exoplatform.addons.gamification.listener;

import org.exoplatform.addons.gamification.GamificationConstant;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.effective.GamificationContextHolder;

import java.util.List;

public interface GamificationListener extends GamificationConstant {

    List<GamificationContextHolder> gamify(RuleDTO ruleDto, String actor) throws Exception;

}
