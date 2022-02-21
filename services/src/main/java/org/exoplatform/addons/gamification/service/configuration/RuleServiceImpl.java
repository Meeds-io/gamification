package org.exoplatform.addons.gamification.service.configuration;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import javax.persistence.EntityExistsException;
import java.util.Date;
import java.util.List;

import static org.exoplatform.addons.gamification.GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX;

public class RuleServiceImpl implements RuleService {

  private static final Log LOG = ExoLogger.getLogger(RuleServiceImpl.class);

  private RuleStorage      ruleStorage;

  public RuleServiceImpl(RuleStorage ruleStorage) {
    this.ruleStorage = ruleStorage;
  }

  public RuleDTO findEnableRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("rule title is mandatory");
    }
    RuleDTO rule = ruleStorage.findEnableRuleByTitle(ruleTitle);
    return rule;
  }

  public RuleDTO findRuleById(Long id) throws IllegalArgumentException {
    if (id == null) {
      throw new IllegalArgumentException("rule id is mandatory");
    }
    return ruleStorage.findRuleById(id);
  }

  public List<RuleDTO> findEnabledRulesByEvent(String event) throws IllegalArgumentException {
    if (StringUtils.isBlank(event)) {
      throw new IllegalArgumentException("rule event is mandatory");
    }
    return ruleStorage.findEnabledRulesByEvent(event);

  }

  public RuleDTO findRuleByTitle(String ruleTitle) throws IllegalArgumentException {
    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("rule title is mandatory");
    }
    return ruleStorage.findRuleByTitle(ruleTitle);
  }

  public RuleDTO findRuleByEventAndDomain(String ruleTitle, String domain) throws IllegalArgumentException {

    if (StringUtils.isBlank(ruleTitle)) {
      throw new IllegalArgumentException("rule title is mandatory");
    }
    if (StringUtils.isBlank(domain)) {
      throw new IllegalArgumentException("rule domain is mandatory");
    }

    return ruleStorage.findRuleByEventAndDomain(ruleTitle, domain);

  }

  public List<RuleDTO> getAllAutomaticRules() {
    return ruleStorage.getAllAutomaticRules();
  }

  public List<RuleDTO> findAllRules() {
    return ruleStorage.findAllRules();
  }

  public List<RuleDTO> getActiveRules() {
    return ruleStorage.getActiveRules();
  }

  public List<RuleDTO> getAllRulesByDomain(String domain) throws IllegalArgumentException {
    if (StringUtils.isBlank(domain)) {
      throw new IllegalArgumentException("rule domain is mandatory");
    }
    return ruleStorage.getAllRulesByDomain(domain);

  }

  public List<RuleDTO> getAllRulesWithNullDomain() {
    return ruleStorage.getAllRulesWithNullDomain();
  }

  public List<String> getAllEvents() {
    return ruleStorage.getAllEvents();
  }

  public List<String> getDomainListFromRules() {
    return ruleStorage.getDomainListFromRules();
  }

  public void deleteRule(Long id) throws IllegalArgumentException, ObjectNotFoundException {
    if (id == null) {
      throw new IllegalArgumentException("rule id is mandatory");
    }
    RuleDTO rule = ruleStorage.findRuleById(id);

    if (rule == null) {
      throw new ObjectNotFoundException("Rule does not exist");
    }
    rule.setDeleted(true);
    ruleStorage.deleteRule(rule);
  }

  public RuleDTO addRule(RuleDTO ruleDTO) throws IllegalArgumentException, EntityExistsException {

    if (ruleDTO == null) {
      throw new IllegalArgumentException("rule is mandatory");
    }
    RuleDTO oldRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
    if (oldRule != null) {
      throw new EntityExistsException("Rule with same event and domain already exist");
    }
    return ruleStorage.saveRule(ruleDTO);
  }

  public RuleDTO updateRule(RuleDTO ruleDTO) throws ObjectNotFoundException {

    RuleDTO oldRule = ruleStorage.findRuleByEventAndDomain(ruleDTO.getEvent(), ruleDTO.getArea());
    if (oldRule == null) {
      throw new ObjectNotFoundException("Rule does not exist");
    }
    if (!ruleDTO.getTitle().startsWith(GAMIFICATION_DEFAULT_DATA_PREFIX)) {
      ruleDTO.setTitle(ruleDTO.getEvent() + "_" + ruleDTO.getArea());
    }
    ruleDTO.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
    return ruleStorage.saveRule(ruleDTO);
  }
}
