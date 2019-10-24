package org.exoplatform.addons.gamification.service.configuration;

import java.util.Date;

import org.junit.Test;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;

public class RuleServiceTest extends AbstractServiceTest {

  @Test
  public void testFindEnableRuleByTitle() {
    assertEquals(ruleStorage.findAll().size(), 0);
    assertNull(ruleService.findEnableRuleByTitle(RULE_NAME));
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleService.findEnableRuleByTitle(RULE_NAME));
    ruleEntity.setEnabled(false);
    ruleStorage.update(ruleEntity);
    assertNull(ruleService.findEnableRuleByTitle(RULE_NAME));
  }

  @Test
  public void testFindRuleById() {
    assertEquals(ruleStorage.findAll().size(), 0);
    RuleEntity ruleEntity = newRule();
    assertNotNull(ruleService.findRuleById(ruleEntity.getId()));
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    assertEquals(ruleStorage.findAll().size(), 0);
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 3);
    r1.setEnabled(false);
    ruleStorage.update(r1);
    assertEquals(ruleService.findEnabledRulesByEvent("rule1").size(), 2);
  }

  @Test
  public void testFindRuleByTitle() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule();
    assertNotNull(ruleService.findRuleByTitle(RULE_NAME));
  }

  @Test
  public void testGetAllRules() {
    assertEquals(ruleService.getAllRules().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule1", "domain3");
    assertEquals(ruleService.getAllRules().size(), 3);
  }

  @Test
  public void testGetAllRulesByDomain() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain1");
    newRule("rule3", "domain2");
    assertEquals(ruleService.getAllRulesByDomain("domain1").size(), 2);
    assertEquals(ruleService.getAllRulesByDomain("domain2").size(), 1);
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    assertEquals(ruleStorage.findAll().size(), 0);
    RuleEntity r1 = newRule("rule1", "domain1");
    RuleEntity r2 = newRule("rule1", "domain2");
    RuleEntity r3 = newRule("rule1", "domain3");
    assertEquals(ruleService.getAllRulesWithNullDomain().size(), 0);
    r1.setDomainEntity(null);
    ruleStorage.update(r1);
    assertEquals(ruleService.getAllRulesWithNullDomain().size(), 1);
  }

  @Test
  public void testGetAllEvents() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule1", "domain2");
    newRule("rule2", "domain3");
    assertEquals(ruleService.getAllEvents().size(), 2);
  }

  @Test
  public void testGetDomainListFromRules() {
    assertEquals(ruleStorage.findAll().size(), 0);
    newRule("rule1", "domain1");
    newRule("rule2", "domain2");
    newRule("rule3", "domain2");
    assertEquals(ruleService.getDomainListFromRules().size(), 2);
  }

  @Test
  public void testDeleteRule() {
    try {
      RuleEntity ruleEntity = newRule();
      assertEquals(ruleEntity.isDeleted(), false);
      ruleService.deleteRule(ruleEntity.getId());
      assertEquals(ruleEntity.isDeleted(), true);
    } catch (Exception e) {
      fail("Error to delete rule", e);
    }

  }

  @Test
  public void testAddRule() {
    try {
      assertEquals(ruleStorage.findAll().size(), 0);
      RuleEntity rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(RULE_NAME);
      rule.setDescription("Description");
      rule.setArea(GAMIFICATION_DOMAIN);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(RULE_NAME);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain());
      ruleService.addRule(ruleMapper.ruleToRuleDTO(rule));
      assertEquals(ruleStorage.findAll().size(), 1);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testUpdateRule() {
    try {
      assertEquals(ruleStorage.findAll().size(), 0);
      RuleEntity ruleEntity = newRule();
      ruleEntity.setDescription("new_description");
      ruleService.updateRule(ruleMapper.ruleToRuleDTO(ruleEntity));
      ruleEntity = ruleStorage.find(ruleEntity.getId());
      assertEquals(ruleEntity.getDescription(), "new_description");
    } catch (Exception e) {
      fail("Error to add rule", e);
    }

  }
}
