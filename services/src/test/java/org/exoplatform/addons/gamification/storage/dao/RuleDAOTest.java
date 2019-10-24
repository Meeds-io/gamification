package org.exoplatform.addons.gamification.storage.dao;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class RuleDAOTest extends AbstractServiceTest {

  @Test
  public void testFindEnableRuleByTitle() {
    assertEquals(ruleStorage.findAll().size(), 0);
    assertNull(ruleStorage.findEnableRuleByTitle(RULE_NAME));
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleStorage.findEnableRuleByTitle(RULE_NAME));
    ruleEntity.setEnabled(false);
    ruleStorage.update(ruleEntity);
    assertNull(ruleStorage.findEnableRuleByTitle(RULE_NAME));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleStorage.findAll().size(), 0);
    assertEquals(ruleStorage.findEnabledRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleStorage.findEnabledRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleStorage.update(r1);
    assertEquals(ruleStorage.findEnabledRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule();
    assertNotNull(ruleStorage.findRuleByTitle(RULE_NAME));
  }

  @Test
  public void testGetAllRules() {
    assertEquals(ruleStorage.getAllRules().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule1", "domain3");
    assertEquals(ruleStorage.getAllRules().size(), 3);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain1");
    newRule("rule3", "domain2");
    assertEquals(ruleStorage.getAllRulesByDomain("domain1").size(), 2);
    assertEquals(ruleStorage.getAllRulesByDomain("domain2").size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleStorage.findAll().size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleStorage.update(r1);
    assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetDomainList() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain2");
    newRule("rule3", "domain2");
    assertEquals(ruleStorage.getDomainList().size(), 2);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule2", "domain3");
    assertEquals(ruleStorage.getAllEvents().size(), 2);
  }
}
