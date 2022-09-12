/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exoplatform.addons.gamification.test;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import org.exoplatform.addons.gamification.IdentityType;
import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnector;
import org.exoplatform.addons.gamification.entities.domain.configuration.BadgeEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.DomainOwnerEntity;
import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.entities.domain.effective.GamificationActionsHistory;
import org.exoplatform.addons.gamification.rest.ManageBadgesEndpoint;
import org.exoplatform.addons.gamification.rest.ManageDomainsEndpoint;
import org.exoplatform.addons.gamification.service.AnnouncementService;
import org.exoplatform.addons.gamification.service.ChallengeService;
import org.exoplatform.addons.gamification.service.configuration.BadgeService;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.configuration.RuleService;
import org.exoplatform.addons.gamification.service.dto.configuration.BadgeDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.GamificationActionsHistoryDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.HistoryStatus;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.addons.gamification.service.mapper.BadgeMapper;
import org.exoplatform.addons.gamification.service.mapper.DomainMapper;
import org.exoplatform.addons.gamification.service.mapper.GamificationActionsHistoryMapper;
import org.exoplatform.addons.gamification.service.mapper.RuleMapper;
import org.exoplatform.addons.gamification.storage.ChallengeStorage;
import org.exoplatform.addons.gamification.storage.DomainStorage;
import org.exoplatform.addons.gamification.storage.RealizationsStorage;
import org.exoplatform.addons.gamification.storage.RuleStorage;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAO;
import org.exoplatform.addons.gamification.storage.dao.DomainDAO;
import org.exoplatform.addons.gamification.storage.dao.DomainOwnerDAO;
import org.exoplatform.addons.gamification.storage.dao.GamificationHistoryDAO;
import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.exoplatform.addons.gamification.utils.Utils;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.ExoContainerContext;
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
import org.exoplatform.social.core.space.spi.SpaceService;

@ConfiguredBy({ @ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.identity-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.social.component.service-dependencies-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.social.component.service-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/gamification-test-configuration.xml") })

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public abstract class AbstractServiceTest extends BaseExoTestCase {

  protected static final String          GAMIFICATION_DOMAIN = "TeamWork";

  protected static final String          RULE_NAME           = "createNewTask";

  protected static final String          BADGE_NAME          = "TeamLeader";

  /* Space */
  protected static final String          TEST_SPACE_ID       = "150";

  /* Space */
  protected static final String          TEST_SPACE2_ID      = "152";

  /* Receiver */
  protected static final String          TEST_USER_RECEIVER  = "55";

  /* Sender */
  protected static final String          TEST_USER_SENDER    = "1";

  /* Link to the activity stream */
  protected static final String          TEST_LINK_ACTIVITY  = "/portal/intranet//activity?id=245590";

  protected static final String          TEST_GLOBAL_SCORE   = "245590";

  protected static final String          TEST__SCORE         = "50";

  protected static final long            MILLIS_IN_A_DAY     = 1000 * 60 * 60 * 24;                               // NOSONAR

  protected static final TimeZone        DEFAULT_TIMEZONE     = TimeZone.getDefault();

  protected static final int             offset              = 0;

  protected static final int             limit               = 3;

  protected IdentityManager              identityManager;

  protected RelationshipManager          relationshipManager;

  protected ActivityManager              activityManager;

  protected EntityManagerService         entityManagerService;

  protected ManageBadgesEndpoint         manageBadgesEndpoint;

  protected ManageDomainsEndpoint        manageDomainsEndpoint;

  protected ChallengeService             challengeService;

  protected AnnouncementService          announcementService;

  protected RequestHandlerImpl           requestHandler;

  protected Identity                     rootIdentity;

  protected ResourceLauncher             launcher;

  protected ProviderBinder               providers;

  protected ResourceBinder               binder;

  protected BadgeService                 badgeService;

  protected DomainService                domainService;

  protected RuleService                  ruleService;

  protected GamificationService          gamificationService;

  protected BadgeDAO                     badgeStorage;

  protected DomainOwnerDAO               domainOwnerDAO;

  protected DomainDAO                    domainDAO;

  protected DomainStorage                domainStorage;

  protected FileService                  fileService;

  protected RuleStorage                  ruleStorage;

  protected ChallengeStorage             challengeStorage;

  protected RuleDAO                      ruleDAO;

  protected GamificationHistoryDAO       gamificationHistoryDAO;

  protected RealizationsStorage          realizationsStorage;

  protected RuleIndexingServiceConnector ruleIndexingServiceConnector;

  protected SpaceService                 spaceService;

  Identity                               userIdentity        = new Identity(TEST_USER_SENDER);

  protected Date                         fromDate;

  protected Date                         toDate;

  protected Date                         OutOfRangeDate;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    ExoContainerContext.setCurrentContainer(getContainer());

    ruleDAO = ExoContainerContext.getService(RuleDAO.class);
    gamificationHistoryDAO = ExoContainerContext.getService(GamificationHistoryDAO.class);
    identityManager = ExoContainerContext.getService(IdentityManager.class);
    domainStorage = ExoContainerContext.getService(DomainStorage.class);
    fileService = ExoContainerContext.getService(FileService.class);
    ruleStorage = ExoContainerContext.getService(RuleStorage.class);
    activityManager = ExoContainerContext.getService(ActivityManager.class);
    relationshipManager = ExoContainerContext.getService(RelationshipManager.class);
    challengeService = ExoContainerContext.getService(ChallengeService.class);
    announcementService = ExoContainerContext.getService(AnnouncementService.class);
    badgeService = ExoContainerContext.getService(BadgeService.class);
    domainService = ExoContainerContext.getService(DomainService.class);
    ruleService = ExoContainerContext.getService(RuleService.class);
    gamificationService = ExoContainerContext.getService(GamificationService.class);
    entityManagerService = ExoContainerContext.getService(EntityManagerService.class);
    manageBadgesEndpoint = ExoContainerContext.getService(ManageBadgesEndpoint.class);
    manageDomainsEndpoint = ExoContainerContext.getService(ManageDomainsEndpoint.class);
    requestHandler = ExoContainerContext.getService(RequestHandlerImpl.class);
    rootIdentity = new Identity(OrganizationIdentityProvider.NAME, "root");
    badgeStorage = ExoContainerContext.getService(BadgeDAO.class);
    challengeStorage = ExoContainerContext.getService(ChallengeStorage.class);
    domainDAO = ExoContainerContext.getService(DomainDAO.class);
    domainOwnerDAO = ExoContainerContext.getService(DomainOwnerDAO.class);
    realizationsStorage = ExoContainerContext.getService(RealizationsStorage.class);
    ruleIndexingServiceConnector = ExoContainerContext.getService(RuleIndexingServiceConnector.class);
    spaceService = ExoContainerContext.getService(SpaceService.class);
    binder = ExoContainerContext.getService(ResourceBinder.class);
    RequestHandlerImpl requestHandler = ExoContainerContext.getService(RequestHandlerImpl.class);
    // reset default providers to be sure it is clean.
    ProviderBinder.setInstance(new ProviderBinder());
    providers = ProviderBinder.getInstance();

    fromDate = new Date(System.currentTimeMillis());
    toDate = new Date(fromDate.getTime() + MILLIS_IN_A_DAY);
    OutOfRangeDate = new Date(fromDate.getTime() - 2 * MILLIS_IN_A_DAY);

    binder.clear();
    ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers, null));
    launcher = new ResourceLauncher(requestHandler);
    begin();
  }

  @Override
  @After
  public void tearDown() {
    TimeZone.setDefault(DEFAULT_TIMEZONE);
    restartTransaction();
    gamificationHistoryDAO.deleteAll();
    badgeStorage.deleteAll();
    ruleDAO.deleteAll();
    domainOwnerDAO.deleteAll();
    domainDAO.deleteAll();
    domainStorage.clearCache();
    ruleStorage.clearCache();
    end();
  }

  protected void registry(Class<?> resourceClass) {
    binder.addResource(resourceClass, null);
  }

  protected void startSessionAs(String user) {
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity(user);
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
    userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);
  }

  protected void startSessionAsAdministrator(String user) {
    org.exoplatform.services.security.Identity identity =
                                                        new org.exoplatform.services.security.Identity(user,
                                                                                                       Collections.singleton(new MembershipEntry("/platform/administrators")));
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
    userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);
  }

  protected RuleEntity newRule() {
    RuleEntity rule = ruleDAO.findRuleByTitle(GAMIFICATION_DOMAIN);
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
      rule.setType(EntityType.AUTOMATIC);
      rule.setManagers(Collections.emptyList());
      rule.setAudience(1L);
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected RuleEntity newRule(String name, String domain) {

    RuleEntity rule = ruleDAO.findRuleByTitle(name + "_" + domain);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name + "_" + domain);
      rule.setDescription("Description");
      rule.setArea(domain);
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain(domain));
      rule.setType(EntityType.AUTOMATIC);
      rule.setManagers(Collections.emptyList());
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected RuleEntity newRule(String name, String domain, Long audience) {
    RuleEntity challenge = ruleDAO.findRuleByTitle(name + "_" + domain);
    if (challenge == null) {
      challenge = new RuleEntity();
      challenge.setScore(Integer.parseInt(TEST__SCORE));
      challenge.setTitle(name + "_" + domain);
      challenge.setDescription("Description");
      challenge.setArea(domain);
      challenge.setEnabled(true);
      challenge.setDeleted(false);
      challenge.setEvent(name);
      challenge.setCreatedBy(TEST_USER_SENDER);
      challenge.setLastModifiedBy(TEST_USER_SENDER);
      challenge.setLastModifiedDate(new Date());
      challenge.setDomainEntity(newDomain(domain));
      challenge.setType(EntityType.MANUAL);
      challenge.setAudience(audience);
      challenge.setManagers(Collections.singletonList(1l));
      challenge.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          + 2 * MILLIS_IN_A_DAY))));
      challenge.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          - 2 * MILLIS_IN_A_DAY))));
      challenge = ruleDAO.create(challenge);
      restartTransaction();
    }
    return challenge;
  }

  protected RuleEntity newRule(String name, String domain, Boolean isEnabled) {
    return newRule(name, domain, isEnabled, EntityType.AUTOMATIC);
  }

  protected RuleEntity newRule(String name, String domain, Boolean isEnabled, EntityType ruleType) {

    RuleEntity rule = ruleDAO.findRuleByTitle(name + "_" + domain);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name + "_" + domain);
      rule.setDescription("Description");
      rule.setArea(domain);
      rule.setEnabled(isEnabled);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_SENDER);
      rule.setLastModifiedBy(TEST_USER_SENDER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain(domain));
      rule.setType(ruleType);
      rule.setManagers(Collections.emptyList());
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected DomainEntity newDomain() {
    DomainEntity domain = domainDAO.findEnabledDomainByTitle(GAMIFICATION_DOMAIN);
    if (domain == null) {
      domain = new DomainEntity();
      domain.setTitle(GAMIFICATION_DOMAIN);
      domain.setDescription("Description");
      domain.setCreatedBy(TEST_USER_SENDER);
      domain.setLastModifiedBy(TEST_USER_SENDER);
      domain.setDeleted(false);
      domain.setEnabled(true);
      domain.setLastModifiedDate(new Date());
      domain.setType(EntityType.AUTOMATIC);
      domain.setCreatedDate(new Date());
      domain = domainDAO.create(domain);
      domainStorage.clearCache();
    }
    return domain;
  }

  protected DomainEntity newDomain(EntityType entityType, String name, boolean status, Set<Long> owners) {
    DomainEntity domain = domainDAO.findEnabledDomainByTitle(name);
    if (domain == null) {
      domain = new DomainEntity();
      domain.setTitle(name);
      domain.setDescription("Description");
      domain.setCreatedBy(TEST_USER_SENDER);
      domain.setLastModifiedBy(TEST_USER_SENDER);
      domain.setDeleted(false);
      domain.setEnabled(status);
      domain.setLastModifiedDate(new Date());
      domain.setType(entityType);
      domain.setCreatedDate(new Date());
      domain.setBudget(20L);
      domain.setCoverFileId(1L);
      DomainEntity createdDomain = domainDAO.create(domain);
      domainStorage.clearCache();
      List<DomainOwnerEntity> ownerEntities = new ArrayList<>();
      if (owners != null) {
        owners.forEach(owner -> {
          DomainOwnerEntity domainOwnerEntity = domainOwnerDAO.create(new DomainOwnerEntity(createdDomain, owner));
          ownerEntities.add(domainOwnerEntity);
        });
      }
      restartTransaction();
      return createdDomain;
    } else {
      return domain;
    }
  }

  protected DomainDTO newDomainDTO(EntityType entityType, String name, boolean status, Set<Long> owners) {
    return DomainMapper.domainEntityToDomainDTO(newDomain(entityType, name, status, owners), domainOwnerDAO);
  }

  protected DomainEntity newDomain(String name) {
    DomainEntity domain = domainDAO.findEnabledDomainByTitle(name);
    if (domain == null) {
      domain = new DomainEntity();
      domain.setTitle(name);
      domain.setDescription(name);
      domain.setCreatedBy(TEST_USER_SENDER);
      domain.setLastModifiedBy(TEST_USER_SENDER);
      domain.setDeleted(false);
      domain.setEnabled(true);
      domain.setLastModifiedDate(new Date());
      domain.setType(EntityType.AUTOMATIC);
      domain.setCreatedDate(new Date());
      domain = domainDAO.create(domain);
      domainStorage.clearCache();
      restartTransaction();
    }
    return domain;
  }

  protected BadgeEntity newBadge() {

    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(10);
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

  protected BadgeEntity newBadgeWithScore(int score) {

    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(score);
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

  protected GamificationActionsHistory newGamificationActionsHistory() {
    RuleEntity rule = newRule();
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(HistoryStatus.ACCEPTED);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_SENDER);
    gHistory.setEarnerId(TEST_USER_SENDER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(1L);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    restartTransaction();
    return gHistory;
  }

  protected GamificationActionsHistory newGamificationActionsHistoryByRuleByEarnerId(RuleEntity rule, String earnerId) {
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(HistoryStatus.ACCEPTED);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(earnerId);
    gHistory.setEarnerId(earnerId);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(1L);
    gHistory.setCreatedBy("gamification");
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    restartTransaction();
    return gHistory;
  }
  
  protected GamificationActionsHistory newGamificationActionsHistoryByRuleByStatus(RuleEntity rule, HistoryStatus status, String earnerId) {
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(status);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(earnerId);
    gHistory.setEarnerId(earnerId);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(1L);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    restartTransaction();
    return gHistory;
  }
  
  protected GamificationActionsHistory newGamificationActionsHistoryWithRuleId(String actionTitle, Long ruleId) {
    RuleEntity rule = newRule();
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(HistoryStatus.ACCEPTED);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_SENDER);
    gHistory.setEarnerId(TEST_USER_SENDER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(actionTitle);
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(ruleId);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    return gHistory;
  }
  
  protected GamificationActionsHistory newGamificationActionsHistoryByStatus(HistoryStatus status) {
    RuleEntity rule = newRule();
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(status);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_SENDER);
    gHistory.setEarnerId(TEST_USER_SENDER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(1L);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    return gHistory;
  }
  
  protected GamificationActionsHistory newGamificationActionsHistoryByEarnerId(String earnerId) {
    RuleEntity rule = newRule();
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(HistoryStatus.ACCEPTED);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_SENDER);
    gHistory.setEarnerId(earnerId);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(1L);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    return gHistory;
  }
  
  protected GamificationActionsHistory newGamificationActionsHistoryToBeSortedByActionTypeInDateRange(Date createdDate, String actionTitle, Long ruleId) {
    RuleEntity rule = newRule();
    GamificationActionsHistory gHistory = new GamificationActionsHistory();
    gHistory.setStatus(HistoryStatus.ACCEPTED);
    gHistory.setDomain(rule.getArea());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_SENDER);
    gHistory.setEarnerId(TEST_USER_SENDER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(actionTitle);
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleId(ruleId);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(createdDate);
    gHistory = gamificationHistoryDAO.create(gHistory);
    return gHistory;
  }

  protected GamificationActionsHistoryDTO newGamificationActionsHistoryDTO() {
    return GamificationActionsHistoryMapper.fromEntity(newGamificationActionsHistory());
  }

  protected RuleDTO newRuleDTO() {
    return RuleMapper.ruleToRuleDTO(newRule());
  }

  protected DomainDTO newDomainDTO() {
    return DomainMapper.domainEntityToDomainDTO(newDomain(), domainOwnerDAO);
  }

  protected DomainDTO newDomainDTO(String name) {
    return DomainMapper.domainEntityToDomainDTO(newDomain(name), domainOwnerDAO);
  }

  protected BadgeDTO newBadgeDTO() {
    return BadgeMapper.badgeToBadgeDTO(newBadge());
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

  public boolean isThisDateWithinThisRange(Date date) {
    return (date.before(toDate) || date.equals(toDate)) && (date.after(fromDate) || date.equals(fromDate));
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
