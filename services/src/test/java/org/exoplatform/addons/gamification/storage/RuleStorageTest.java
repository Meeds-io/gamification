package org.exoplatform.addons.gamification.storage;

import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.addons.gamification.utils.Utils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Collections;

public class RuleStorageTest extends AbstractServiceTest {

  @Test
  public void testSaveRule() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = new RuleDTO();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(RULE_NAME);
      rule.setDescription("Description");
      rule.setArea(GAMIFICATION_DOMAIN);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(RULE_NAME);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setCreatedDate(Utils.toRFC3339Date(new Date()));
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
      rule.setDomainDTO(newDomainDTO());
      rule.setType(TypeRule.AUTOMATIC);
      ruleStorage.saveRule(rule);
      assertEquals(ruleStorage.findAllRules().size(), 1);
      assertEquals(ruleStorage.getAllAutomaticRules().size(), 1);

    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindEnableRuleByTitle() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findEnableRuleByTitle(rule.getTitle()).getTitle(), rule.getTitle());
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindRuleById() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindEnabledRulesByEvent() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findEnabledRulesByEvent(rule.getEvent()).size(), 1);
      rule.setEnabled(false);
      ruleStorage.saveRule(rule);
      assertEquals(ruleStorage.findEnabledRulesByEvent(rule.getEvent()).size(), 0);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindRuleByTitle() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findEnableRuleByTitle(rule.getTitle()).getTitle(), rule.getTitle());
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindRuleByEventAndDomain() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findRuleByEventAndDomain(rule.getEvent(), rule.getArea()).getTitle(), rule.getTitle());
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetAllAutomaticRules() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule1 = newRuleDTO();
      RuleDTO rule2 = newRuleDTO();
      RuleDTO rule3 = newRuleDTO();
      assertEquals(ruleStorage.getAllAutomaticRules().size(), 3);
      rule1.setType(TypeRule.MANUAL);
      ruleStorage.saveRule(rule1);
      assertEquals(ruleStorage.getAllAutomaticRules().size(), 2);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindAllRules() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule1 = newRuleDTO();
      RuleDTO rule2 = newRuleDTO();
      RuleDTO rule3 = newRuleDTO();
      RuleDTO manualRule = new RuleDTO();
      manualRule.setScore(Integer.parseInt(TEST__SCORE));
      manualRule.setTitle(RULE_NAME);
      manualRule.setDescription("Description");
      manualRule.setArea(GAMIFICATION_DOMAIN);
      manualRule.setEnabled(true);
      manualRule.setDeleted(false);
      manualRule.setEvent(RULE_NAME);
      manualRule.setCreatedBy(TEST_USER_SENDER);
      manualRule.setCreatedDate(Utils.toRFC3339Date(new Date()));
      manualRule.setLastModifiedBy(TEST_USER_SENDER);
      manualRule.setLastModifiedDate(Utils.toRFC3339Date(new Date()));
      manualRule.setDomainDTO(newDomainDTO());
      manualRule.setType(TypeRule.MANUAL);
      ruleStorage.saveRule(manualRule);
      assertEquals(ruleStorage.getAllAutomaticRules().size(), 3);
      assertEquals(ruleStorage.findAllRules().size(), 4);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetActiveRules() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule1 = newRuleDTO();
      RuleDTO rule2 = newRuleDTO();
      RuleDTO rule3 = newRuleDTO();
      assertEquals(ruleStorage.getActiveRules().size(), 3);
      rule1.setEnabled(false);
      ruleStorage.saveRule(rule1);
      assertEquals(ruleStorage.getActiveRules().size(), 2);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetAllRulesByDomain() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      newRule("rule1", "domain");
      newRule("rule2", "domain");
      newRule("rule3", "domain");
      assertEquals(ruleStorage.getAllRulesByDomain("domain").size(), 3);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetAllRulesWithNullDomain() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule1 = newRuleDTO();
      RuleDTO rule2 = newRuleDTO();
      RuleDTO rule3 = newRuleDTO();
      assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 0);
      rule1.setDomainDTO(null);
      ruleStorage.saveRule(rule1);
      assertEquals(ruleStorage.getAllRulesWithNullDomain().size(), 1);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetAllEvents() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      newRule("rule1", "domain1");
      newRule("rule2", "domain2");
      assertEquals(ruleStorage.getAllEvents().size(), 2);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testGetDomainListFromRules() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      newRule("rule1", "domain1");
      newRule("rule2", "domain2");
      newRule("rule3", "domain2");
      assertEquals(ruleStorage.getDomainListFromRules().size(), 2);
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testDeleteRule() {
    try {
      assertEquals(ruleStorage.findAllRules().size(), 0);
      RuleDTO rule = newRuleDTO();
      assertEquals(ruleStorage.findRuleById(rule.getId()).getTitle(), rule.getTitle());
      assertFalse(rule.isDeleted());
      rule.setDeleted(true);
      ruleStorage.deleteRule(rule);
      rule = ruleStorage.findRuleById(rule.getId());
      assertTrue(rule.isDeleted());
    } catch (Exception e) {
      fail("Error to add rule", e);
    }
  }

  @Test
  public void testFindAllChallengesByUserByDomain() {
    assertEquals(ruleStorage.findAllChallengesByUserByDomain(0, 0, 0, Collections.singletonList(0l)).size(), 0);
    DomainEntity domain1 = newDomain("domain1");
    DomainEntity domain2 = newDomain("domain2");
    newChallenge("rule1", domain1.getTitle(), 1l);
    newChallenge("rule2", domain1.getTitle(), 2l);
    newChallenge("rule3", domain2.getTitle(), 3l);
    assertEquals(1, ruleStorage.findAllChallengesByUserByDomain(domain1.getId(), 0, 4, Collections.singletonList(1l)).size());
    assertEquals(2, ruleStorage.findAllChallengesByUserByDomain(domain1.getId(), 0, 4 , Arrays.asList(1l, 2l)).size());
    assertEquals(1, ruleStorage.findAllChallengesByUserByDomain(domain1.getId(), 0, 4 , Arrays.asList(1l, 3l)).size());
    assertEquals(1, ruleStorage.findAllChallengesByUserByDomain(domain2.getId(), 0, 4 , Arrays.asList(2l, 3l)).size());
  }

  @Test
  public void testCountAllChallengesByUserByDomain() {
    assertEquals(ruleStorage.countAllChallengesByUserByDomain(0, Collections.singletonList(0l)), 0);
    DomainEntity domain1 = newDomain("domain1");
    DomainEntity domain2 = newDomain("domain2");
    newChallenge("rule1", domain1.getTitle(), 1l);
    newChallenge("rule2", domain1.getTitle(), 2l);
    newChallenge("rule3", domain2.getTitle(), 3l);
    assertEquals(1, ruleStorage.countAllChallengesByUserByDomain(domain1.getId(), Collections.singletonList(1l)));
    assertEquals(2, ruleStorage.countAllChallengesByUserByDomain(domain1.getId(), Arrays.asList(1l, 2l)));
    assertEquals(1, ruleStorage.countAllChallengesByUserByDomain(domain1.getId(), Arrays.asList(1l, 3l)));
    assertEquals(1, ruleStorage.countAllChallengesByUserByDomain(domain2.getId(), Arrays.asList(2l, 3l)));
  }
}
