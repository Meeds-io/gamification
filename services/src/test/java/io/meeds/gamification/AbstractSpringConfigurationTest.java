/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification;

import java.util.Date;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.dao.EventDAO;
import io.meeds.gamification.dao.ProgramDAO;
import io.meeds.gamification.dao.RuleDAO;
import io.meeds.gamification.entity.EventEntity;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.storage.EventStorage;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.storage.mapper.ProgramMapper;
import io.meeds.gamification.storage.mapper.RuleMapper;
import io.meeds.kernel.test.AbstractSpringTest;
import io.meeds.spring.AvailableIntegration;

@SpringBootApplication(scanBasePackages = {
  "io.meeds.gamification",
  "io.meeds.social.common",
  "io.meeds.social.cms",
  "io.meeds.social.html",
  AvailableIntegration.KERNEL_TEST_MODULE,
  AvailableIntegration.JPA_MODULE,
}, exclude = {
  LiquibaseAutoConfiguration.class
})
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-common.properties")
@ConfiguredBy({
  @ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/configuration.xml"),
  @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/configuration.xml"),
  @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/gamification-test-configuration.xml"),
})
@EnableAutoConfiguration(exclude = {
  LiquibaseAutoConfiguration.class
})
@RunWith(SpringRunner.class)
public abstract class AbstractSpringConfigurationTest extends AbstractSpringTest {

  private static final String GAMIFICATION_DOMAIN = "TeamWork";

  private static final String RULE_NAME           = "createNewTask";

  private static final String TEST_USER_EARNER    = "1";

  private static final String TEST_SCORE          = "50";

  private static final String DESCRIPTION         = "Description";

  protected ProgramStorage    programStorage;

  protected EventStorage      eventStorage;

  protected RuleStorage       ruleStorage;

  protected ProgramDAO        programDAO;

  protected RuleDAO           ruleDAO;

  protected EventDAO          eventDAO;

  protected IdentityRegistry  identityRegistry;

  protected IdentityManager   identityManager;

  protected SpaceService      spaceService;

  protected AbstractSpringConfigurationTest() {
    AbstractSpringTest.setTestClass(AbstractSpringConfigurationTest.class);
  }

  @Before
  public void setUp() {
    begin();
    this.identityManager = getContainer().getComponentInstanceOfType(IdentityManager.class);
    this.spaceService = getContainer().getComponentInstanceOfType(SpaceService.class);
    this.programStorage = getContainer().getComponentInstanceOfType(ProgramStorage.class);
    this.eventStorage = getContainer().getComponentInstanceOfType(EventStorage.class);
    this.ruleStorage = getContainer().getComponentInstanceOfType(RuleStorage.class);
    this.programDAO = getContainer().getComponentInstanceOfType(ProgramDAO.class);
    this.ruleDAO = getContainer().getComponentInstanceOfType(RuleDAO.class);
    this.eventDAO = getContainer().getComponentInstanceOfType(EventDAO.class);
    this.identityRegistry = getContainer().getComponentInstanceOfType(IdentityRegistry.class);
  }

  @After
  public void tearDown() {
    end();
  }

  protected RuleDTO newRuleDTO() {
    return RuleMapper.fromEntity(programStorage, eventStorage, newRule());
  }

  protected ProgramDTO newProgram() {
    return ProgramMapper.fromEntity(ruleDAO, newDomain());
  }

  protected ProgramEntity newDomain() {
    ProgramEntity domain = new ProgramEntity();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription(DESCRIPTION);
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(new Date());
    domain.setType(EntityType.AUTOMATIC);
    domain.setCreatedDate(new Date());
    domain.setAudienceId(1L);
    HashSet<Long> owners = new HashSet<>();
    owners.add(1L);
    domain.setOwners(owners);
    domain = programDAO.create(domain);
    programStorage.clearCache();
    return domain;
  }

  protected ProgramEntity newDomain(String name) {
    ProgramEntity domain = new ProgramEntity();
    domain.setTitle(name);
    domain.setDescription(name);
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(new Date());
    domain.setType(EntityType.AUTOMATIC);
    domain.setCreatedDate(new Date());
    domain.setAudienceId(1L);
    domain = programDAO.create(domain);
    programStorage.clearCache();
    restartTransaction();
    return domain;
  }

  protected RuleEntity newRule() {
    RuleEntity rule = ruleDAO.findRuleByTitle(GAMIFICATION_DOMAIN);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST_SCORE));
      rule.setTitle(RULE_NAME);
      rule.setDescription(DESCRIPTION);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEventEntity(newEvent(RULE_NAME));
      rule.setCreatedBy(TEST_USER_EARNER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_EARNER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain());
      rule.setType(EntityType.AUTOMATIC);
      rule.setDefaultRealizationStatus(RealizationStatus.ACCEPTED);
      rule.setRecurrence(RecurrenceType.NONE);
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected EventEntity newEvent(String name) {
    EventEntity event = new EventEntity();
    event.setTitle(name);
    event.setTrigger(name);
    event.setType("eventType");
    event = eventDAO.create(event);
    return event;
  }

}
