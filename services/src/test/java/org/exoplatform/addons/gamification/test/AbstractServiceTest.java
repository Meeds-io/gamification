/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.test;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.ws.rs.core.SecurityContext;

import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.rest.ManageBadgesEndpoint;
import org.exoplatform.addons.gamification.rest.ManageDomainsEndpoint;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.TypeRule;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.mapper.BadgeMapper;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAO;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.naming.InitialContextInitializer;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.storage.api.IdentityStorage;

@ConfiguredBy({
    @ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.portal-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.identity-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.test.jcr-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.social.component.service-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/gamification-test-configuration.xml") })

public abstract class AbstractServiceTest extends BaseExoTestCase {

  protected static final String           GAMIFICATION_DOMAIN = "TeamWork";

  protected static final String           RULE_NAME           = "createNewTask";

  protected static final String           BADGE_NAME          = "TeamLeader";

  /* Space */
  protected static final String           TEST_SPACE_ID  = "150";

  /* Space */
  protected static final String           TEST_SPACE2_ID  = "152";

  /* Receiver */
  protected static final String           TEST_USER_RECEIVER  = "55";

  /* Sender */
  protected static final String           TEST_USER_SENDER    = "1";

  /* Link to the activity stream */
  protected static final String           TEST_LINK_ACTIVITY  = "/portal/intranet//activity?id=245590";

  protected static final String           TEST_GLOBAL_SCORE   = "245590";

  protected static final String           TEST__SCORE         = "50";
  protected IdentityManager               identityManager;
  protected RelationshipManager           relationshipManager;
  protected ActivityManager               activityManager;
  protected EntityManagerService          entityManagerService;
  protected GamificationService           gamificationService;
  protected ManageBadgesEndpoint          manageBadgesEndpoint;
  protected ManageDomainsEndpoint         manageDomainsEndpoint;
  protected RequestHandlerImpl            requestHandler;
  protected Identity                      rootIdentity;
  protected ResourceLauncher              launcher;
  protected ProviderBinder                providers;
  protected ResourceBinder                binder;
  protected BadgeService                  badgeService;
  protected DomainService                 domainService;
  protected RuleService                   ruleService;
  protected BadgeDAO                      badgeStorage;
  protected DomainDAO                     domainStorage;
  protected RuleDAO                       ruleStorage;
  protected BadgeMapper                   badgeMapper;
  protected DomainMapper                  domainMapper;
  protected RuleMapper                    ruleMapper;
  protected GamificationHistoryDAO        gamificationHistoryDAO;
  Identity                                testUserReceiver    = new Identity(TEST_USER_RECEIVER);
  Identity                                testUserSender      = new Identity(TEST_USER_SENDER);

  @Override
  protected void setUp() throws Exception {
    begin();
    gamificationService = getContainer().getComponentInstanceOfType(GamificationService.class);
    identityManager = CommonsUtils.getService(IdentityManager.class);
    activityManager = CommonsUtils.getService(ActivityManager.class);
    relationshipManager = CommonsUtils.getService(RelationshipManager.class);
    badgeService = CommonsUtils.getService(BadgeService.class);
    ruleService = CommonsUtils.getService(RuleService.class);
    domainService = CommonsUtils.getService(DomainService.class);
    entityManagerService = CommonsUtils.getService(EntityManagerService.class);
    manageBadgesEndpoint = CommonsUtils.getService(ManageBadgesEndpoint.class);
    manageDomainsEndpoint = CommonsUtils.getService(ManageDomainsEndpoint.class);
    requestHandler = getContainer().getComponentInstanceOfType(RequestHandlerImpl.class);
    rootIdentity = new Identity(OrganizationIdentityProvider.NAME, "root");
    badgeStorage = CommonsUtils.getService(BadgeDAO.class);
    badgeMapper = CommonsUtils.getService(BadgeMapper.class);
    domainStorage = CommonsUtils.getService(DomainDAO.class);
    ruleStorage = CommonsUtils.getService(RuleDAO.class);
    ruleMapper = CommonsUtils.getService(RuleMapper.class);
    domainMapper = CommonsUtils.getService(DomainMapper.class);
    gamificationHistoryDAO = CommonsUtils.getService(GamificationHistoryDAO.class);
    ExoContainer container = getContainer();
    binder = container.getComponentInstanceOfType(ResourceBinder.class);
    RequestHandlerImpl requestHandler = container.getComponentInstanceOfType(RequestHandlerImpl.class);
    // reset default providers to be sure it is clean.
    ProviderBinder.setInstance(new ProviderBinder());
    providers = ProviderBinder.getInstance();

    binder.clear();
    ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers, null));
    launcher = new ResourceLauncher(requestHandler);
  }

  @Override
  protected void tearDown() throws Exception {
    end();
    RequestLifeCycle.begin(getContainer());
    gamificationHistoryDAO.deleteAll();
    ruleStorage.deleteAll();
    badgeStorage.deleteAll();
    domainStorage.deleteAll();
    end();
  }

  protected void registry(Class<?> resourceClass) throws Exception {
    binder.addResource(resourceClass, null);
  }

  protected void startSessionAs(String user) {
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity(user);
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
  }

  protected RuleEntity newRule() {
    RuleEntity rule = ruleStorage.findRuleByTitle(GAMIFICATION_DOMAIN);
    if (rule == null) {
      rule = new RuleEntity();
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
      rule.setType(TypeRule.AUTOMATIC);
      rule.setManagers(Collections.emptyList());
      rule = ruleStorage.create(rule);
    }
    return rule;
  }
  protected RuleEntity newRule(String name, String domain) {

    RuleEntity rule = ruleStorage.findRuleByTitle(name+"_"+domain);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name+"_"+domain);
      rule.setDescription("Description");
      rule.setArea(domain);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain(domain));
      rule.setType(TypeRule.AUTOMATIC);
      rule.setManagers(Collections.emptyList());
      rule = ruleStorage.create(rule);
    }
    return rule;
  }

  protected RuleEntity newRule(String name, String domain, Boolean isEnabled) {

    RuleEntity rule = ruleStorage.findRuleByTitle(name+"_"+domain);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name+"_"+domain);
      rule.setDescription("Description");
      rule.setArea(domain);
      rule.setEnabled(isEnabled);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain(domain));
      rule.setType(TypeRule.AUTOMATIC);
      rule.setManagers(Collections.emptyList());
      rule = ruleStorage.create(rule);
    }
    return rule;
  }

  protected DomainEntity newDomain() {
    DomainEntity domain = domainStorage.findDomainByTitle(GAMIFICATION_DOMAIN);
    if (domain == null) {
      domain = new DomainEntity();
      domain.setTitle(GAMIFICATION_DOMAIN);
      domain.setDescription("Description");
      domain.setCreatedBy(TEST_USER_SENDER);
      domain.setLastModifiedBy(TEST_USER_SENDER);
      domain.setDeleted(false);
      domain.setEnabled(true);
      domain.setLastModifiedDate(new Date());
      domain = domainStorage.create(domain);
    }
    return domain;
  }

  protected DomainEntity newDomain(String name) {
    DomainEntity domain = domainStorage.findDomainByTitle(name);
    if (domain == null) {
      domain = new DomainEntity();
      domain.setTitle(name);
      domain.setDescription(name);
      domain.setCreatedBy(TEST_USER_SENDER);
      domain.setLastModifiedBy(TEST_USER_SENDER);
      domain.setDeleted(false);
      domain.setEnabled(true);
      domain.setLastModifiedDate(new Date());
      domain = domainStorage.create(domain);
    }
    return domain;
  }

  protected BadgeEntity newBadge() {

    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(Integer.parseInt(TEST_GLOBAL_SCORE));
    badge.setDomain(GAMIFICATION_DOMAIN);
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_SENDER);
    badge.setCreatedDate(new Date());
    badge.setLastModifiedBy(TEST_USER_SENDER);
    badge.setLastModifiedDate(new Date());
    badge.setDomainEntity(newDomain());
    badge = badgeStorage.create(badge);
    return badge;
  }

  protected BadgeEntity newBadge(String name, String domain) {

    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(name);
    badge.setDescription("Description");
    badge.setNeededScore(Integer.parseInt(TEST_GLOBAL_SCORE));
    badge.setDomain(domain);
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_SENDER);
    badge.setCreatedDate(new Date());
    badge.setLastModifiedBy(TEST_USER_SENDER);
    badge.setLastModifiedDate(new Date());
    badge.setDomainEntity(newDomain(domain));
    badge = badgeStorage.create(badge);
    return badge;
  }

  protected RuleDTO newRuleDTO() {
    return ruleMapper.ruleToRuleDTO(newRule());
  }

  protected DomainDTO newDomainDTO() {
    return domainMapper.domainToDomainDTO(newDomain());
  }

  protected BadgeDTO newBadgeDTO() {
    return badgeMapper.badgeToBadgeDTO(newBadge());
  }

  public void compareHistory(GamificationActionsHistory h1, GamificationActionsHistory h2) {
    assertEquals(h1.getActionScore(), h2.getActionScore());
    assertEquals(h1.getActionTitle(), h2.getActionTitle());
    assertEquals(h1.getDomain(), h2.getDomain());
    assertEquals(h1.getGlobalScore(), h2.getGlobalScore());
    assertEquals(h1.getObjectId(), h2.getObjectId());
    assertEquals(h1.getReceiver(), h2.getReceiver());
    assertEquals(h1.getEarnerId(), h2.getEarnerId());
    assertEquals(h1.getCreatedBy(), h2.getCreatedBy());
  }

  protected static class MockSecurityContext implements SecurityContext {

    private final String username;

    public MockSecurityContext(String username) {
      this.username = username;
    }

    public Principal getUserPrincipal() {
      return new MockPrincipal(username);
    }

    public boolean isUserInRole(String role) {
      return false;
    }

    public boolean isSecure() {
      return false;
    }

    public String getAuthenticationScheme() {
      return null;
    }
  }
}
