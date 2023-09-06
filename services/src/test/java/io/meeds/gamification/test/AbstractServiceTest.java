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
package io.meeds.gamification.test;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import io.meeds.gamification.dao.*;
import io.meeds.gamification.service.*;
import io.meeds.gamification.service.impl.EventRegistryImpl;
import io.meeds.gamification.storage.EventStorage;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.junit.After;
import org.junit.Before;

import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.spi.SpaceService;

import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.constant.IdentityType;
import io.meeds.gamification.constant.RealizationStatus;
import io.meeds.gamification.constant.RecurrenceType;
import io.meeds.gamification.entity.BadgeEntity;
import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RealizationEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.BadgeDTO;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RealizationDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.rest.BadgeRest;
import io.meeds.gamification.rest.ProgramRest;
import io.meeds.gamification.search.RuleIndexingServiceConnector;
import io.meeds.gamification.storage.ProgramStorage;
import io.meeds.gamification.storage.RealizationStorage;
import io.meeds.gamification.storage.RuleStorage;
import io.meeds.gamification.storage.mapper.BadgeMapper;
import io.meeds.gamification.storage.mapper.ProgramMapper;
import io.meeds.gamification.storage.mapper.RealizationMapper;
import io.meeds.gamification.storage.mapper.RuleMapper;
import io.meeds.gamification.utils.Utils;

@ConfiguredBy({
    @ConfigurationUnit(scope = ContainerScope.ROOT, path = "conf/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/gamification-test-configuration.xml"),
})
public abstract class AbstractServiceTest extends BaseExoTestCase {

  public static final String             GAMIFICATION_DOMAIN = "TeamWork";

  public static final String             RULE_NAME           = "createNewTask";

  public static final String             BADGE_NAME          = "TeamLeader";

  /* Space */
  public static final String             TEST_SPACE_ID       = "150";

  /* Space */
  public static final String             TEST_SPACE2_ID      = "152";

  /* Receiver */
  public static final String             TEST_USER_RECEIVER  = "10";

  /* Sender */
  public static final String             TEST_USER_EARNER    = "1";

  /* Link to the activity stream */
  public static final String             ACTIVITY_ID         = "245590";

  public static final String             TEST_GLOBAL_SCORE   = "245590";

  public static final String             TEST__SCORE         = "50";

  public static final long               MILLIS_IN_A_DAY     = 1000 * 60 * 60 * 24;           // NOSONAR

  public static final TimeZone           DEFAULT_TIMEZONE    = TimeZone.getDefault();

  public static final int                offset              = 0;

  public static final int                limit               = 3;

  protected IdentityManager              identityManager;

  protected RelationshipManager          relationshipManager;

  protected ActivityManager              activityManager;

  protected EntityManagerService         entityManagerService;

  protected BadgeRest                    manageBadgesEndpoint;

  protected ProgramRest                  manageDomainsEndpoint;

  protected AnnouncementService          announcementService;

  protected RequestHandlerImpl           requestHandler;

  protected Identity                     rootIdentity;

  protected ResourceLauncher             launcher;

  protected ProviderBinder               providerBinder;

  protected ResourceBinder               resourceBinder;

  protected BadgeService                 badgeService;

  protected ProgramService               programService;

  protected RuleService                  ruleService;

  protected ListenerService              listenerService;

  protected RealizationService           realizationService;

  protected BadgeDAO                     badgeStorage;

  protected ProgramDAO                   programDAO;

  protected ProgramStorage               domainStorage;

  protected FileService                  fileService;

  protected RuleStorage                  ruleStorage;

  protected RuleDAO                      ruleDAO;

  protected RealizationDAO               realizationDAO;
  
  protected ConnectorAccountDAO          connectorAccountDAO;

  protected RealizationStorage           realizationsStorage;

  protected RuleIndexingServiceConnector ruleIndexingServiceConnector;
  
  protected ConnectorService             connectorService;
  
  protected ConnectorSettingService      connectorSettingService;

  protected EventDAO                     eventDAO;

  protected EventStorage                 eventStorage;

  protected EventService                 eventService;

  protected EventRegistryImpl            eventRegistry;

  protected SpaceService                 spaceService;
  
  protected SettingService               settingService;

  protected CodecInitializer               codecInitializer;

  protected IdentityRegistry             identityRegistry;

  Identity                               userIdentity        = new Identity(TEST_USER_EARNER);

  protected Date                         fromDate;

  protected Date                         toDate;

  protected Date                         OutOfRangeDate;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    ExoContainerContext.setCurrentContainer(getContainer());

    ruleDAO = ExoContainerContext.getService(RuleDAO.class);
    realizationDAO = ExoContainerContext.getService(RealizationDAO.class);
    connectorAccountDAO = ExoContainerContext.getService(ConnectorAccountDAO.class);
    identityManager = ExoContainerContext.getService(IdentityManager.class);
    domainStorage = ExoContainerContext.getService(ProgramStorage.class);
    fileService = ExoContainerContext.getService(FileService.class);
    ruleStorage = ExoContainerContext.getService(RuleStorage.class);
    activityManager = ExoContainerContext.getService(ActivityManager.class);
    relationshipManager = ExoContainerContext.getService(RelationshipManager.class);
    announcementService = ExoContainerContext.getService(AnnouncementService.class);
    badgeService = ExoContainerContext.getService(BadgeService.class);
    programService = ExoContainerContext.getService(ProgramService.class);
    ruleService = ExoContainerContext.getService(RuleService.class);
    listenerService = ExoContainerContext.getService(ListenerService.class);
    realizationService = ExoContainerContext.getService(RealizationService.class);
    entityManagerService = ExoContainerContext.getService(EntityManagerService.class);
    manageBadgesEndpoint = ExoContainerContext.getService(BadgeRest.class);
    manageDomainsEndpoint = ExoContainerContext.getService(ProgramRest.class);
    requestHandler = ExoContainerContext.getService(RequestHandlerImpl.class);
    rootIdentity = new Identity(OrganizationIdentityProvider.NAME, "root");
    badgeStorage = ExoContainerContext.getService(BadgeDAO.class);
    programDAO = ExoContainerContext.getService(ProgramDAO.class);
    realizationsStorage = ExoContainerContext.getService(RealizationStorage.class);
    ruleIndexingServiceConnector = ExoContainerContext.getService(RuleIndexingServiceConnector.class);
    connectorSettingService = ExoContainerContext.getService(ConnectorSettingService.class);
    connectorService = ExoContainerContext.getService(ConnectorService.class);
    eventDAO = ExoContainerContext.getService(EventDAO.class);
    eventStorage = ExoContainerContext.getService(EventStorage.class);
    eventService = ExoContainerContext.getService(EventService.class);
    eventRegistry = ExoContainerContext.getService(EventRegistryImpl.class);
    spaceService = ExoContainerContext.getService(SpaceService.class);
    settingService = ExoContainerContext.getService(SettingService.class);
    codecInitializer = ExoContainerContext.getService(CodecInitializer.class);
    identityRegistry = ExoContainerContext.getService(IdentityRegistry.class);
    resourceBinder = ExoContainerContext.getService(ResourceBinder.class);
    RequestHandlerImpl requestHandler = ExoContainerContext.getService(RequestHandlerImpl.class);
    // reset default providers to be sure it is clean.
    ProviderBinder.setInstance(new ProviderBinder());
    providerBinder = ProviderBinder.getInstance();

    fromDate = new Date(System.currentTimeMillis());
    toDate = new Date(fromDate.getTime() + MILLIS_IN_A_DAY);
    OutOfRangeDate = new Date(fromDate.getTime() - 2 * MILLIS_IN_A_DAY);

    resourceBinder.clear();
    ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providerBinder, null));
    launcher = new ResourceLauncher(requestHandler);
    begin();
  }

  @Override
  @After
  public void tearDown() {
    TimeZone.setDefault(DEFAULT_TIMEZONE);
    if (realizationDAO != null) {
      restartTransaction();
      realizationDAO.deleteAll();
      badgeStorage.deleteAll();
      ruleDAO.deleteAll();
      programDAO.deleteAll();
      connectorAccountDAO.deleteAll();
      eventDAO.deleteAll();
      domainStorage.clearCache();
      ruleStorage.clearCache();
      end();
    }
  }

  protected void registry(Class<?> resourceClass) {
    resourceBinder.addResource(resourceClass, null);
  }

  protected ConversationState startSessionAs(String username) {
    org.exoplatform.services.security.Identity identity = registerInternalUser(username);
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
    userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username);
    return state;
  }

  protected ConversationState startExternalSessionAs(String username) {
    org.exoplatform.services.security.Identity identity = registerExternalUser(username);
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
    userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username);
    return state;
  }

  protected void startSessionAsAdministrator(String user) {
    org.exoplatform.services.security.Identity identity = registerAdministratorUser(user);
    ConversationState state = new ConversationState(identity);
    ConversationState.setCurrent(state);
    userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);
  }

  protected org.exoplatform.services.security.Identity registerAdministratorUser(String user) {
    org.exoplatform.services.security.Identity identity =
                                                        new org.exoplatform.services.security.Identity(user,
                                                                                                       Arrays.asList(new MembershipEntry(Utils.ADMINS_GROUP)));
    identityRegistry.register(identity);
    return identity;
  }

  protected org.exoplatform.services.security.Identity registerExternalUser(String username) {
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity(username,
                                                                                                         Arrays.asList(new MembershipEntry("/platform/externals")));
    identityRegistry.register(identity);
    return identity;
  }

  protected org.exoplatform.services.security.Identity registerInternalUser(String username) {
    org.exoplatform.services.security.Identity identity = new org.exoplatform.services.security.Identity(username,
                                                                                                         Arrays.asList(new MembershipEntry("/platform/externals")));
    identityRegistry.register(identity);
    return identity;
  }

  protected RuleEntity newRule() {
    RuleEntity rule = ruleDAO.findRuleByTitle(GAMIFICATION_DOMAIN);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(RULE_NAME);
      rule.setDescription("Description");
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(RULE_NAME);
      rule.setCreatedBy(TEST_USER_EARNER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_EARNER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain());
      rule.setType(EntityType.AUTOMATIC);
      rule.setRecurrence(RecurrenceType.NONE);
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected RuleEntity newRule(String name, long domainId) {
    ProgramEntity domainEntity = programDAO.find(domainId);
    RuleEntity rule = ruleDAO.findActiveRuleByEventAndProgramId(name, domainId);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name);
      rule.setDescription("Description");
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_EARNER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_EARNER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(domainEntity);
      rule.setType(EntityType.AUTOMATIC);
      rule.setRecurrence(RecurrenceType.NONE);
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected RuleEntity newManualRule(String name, long domainId) {
    ProgramEntity domainEntity = programDAO.find(domainId);
    RuleEntity rule = ruleDAO.findActiveRuleByEventAndProgramId(name, domainId);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name);
      rule.setDescription("Description");
      rule.setEnabled(true);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_EARNER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_EARNER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(domainEntity);
      rule.setType(EntityType.MANUAL);
      rule.setRecurrence(RecurrenceType.NONE);
      rule.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          + 2 * MILLIS_IN_A_DAY))));
      rule.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          - 2 * MILLIS_IN_A_DAY))));
      rule = ruleDAO.create(rule);
      restartTransaction();
    }
    return rule;
  }

  protected RuleEntity newRule(String name, String domain, Boolean isEnabled) {
    return newRule(name, domain, isEnabled, EntityType.AUTOMATIC);
  }

  protected RuleEntity newRule(String name, String domain, Boolean isEnabled, EntityType ruleType) {
    RuleEntity rule = ruleDAO.findRuleByTitle(name);
    if (rule == null) {
      rule = new RuleEntity();
      rule.setScore(Integer.parseInt(TEST__SCORE));
      rule.setTitle(name);
      rule.setDescription("Description");
      rule.setEnabled(isEnabled);
      rule.setDeleted(false);
      rule.setEvent(name);
      rule.setCreatedBy(TEST_USER_EARNER);
      rule.setCreatedDate(new Date());
      rule.setLastModifiedBy(TEST_USER_EARNER);
      rule.setLastModifiedDate(new Date());
      rule.setDomainEntity(newDomain(domain));
      rule.setType(ruleType);
      rule.setRecurrence(RecurrenceType.NONE);
      rule.setEndDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          + 2 * MILLIS_IN_A_DAY))));
      rule.setStartDate(Utils.parseSimpleDate(Utils.toRFC3339Date(new Date(System.currentTimeMillis()
          - 2 * MILLIS_IN_A_DAY))));
      rule = ruleDAO.create(rule);
    }
    return rule;
  }

  protected ProgramEntity newDomain() {
    ProgramEntity domain = new ProgramEntity();
    domain.setTitle(GAMIFICATION_DOMAIN);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(true);
    domain.setLastModifiedDate(new Date());
    domain.setType(EntityType.AUTOMATIC);
    domain.setCreatedDate(new Date());
    domain.setAudienceId(1L);
    HashSet<Long> owners = new HashSet<Long>();
    owners.add(1L);
    domain.setOwners(owners);
    domain = programDAO.create(domain);
    domainStorage.clearCache();
    return domain;
  }

  protected ProgramEntity newDomain(EntityType entityType, String name, boolean status, Set<Long> owners) {
    return newDomain(entityType, name, status, owners, 1l);
  }

  protected ProgramEntity newDomain(EntityType entityType, String name, boolean status, Set<Long> owners, Long audienceId) {
    ProgramEntity domain = new ProgramEntity();
    domain.setTitle(name);
    domain.setDescription("Description");
    domain.setCreatedBy(TEST_USER_EARNER);
    domain.setLastModifiedBy(TEST_USER_EARNER);
    domain.setDeleted(false);
    domain.setEnabled(status);
    domain.setLastModifiedDate(new Date());
    domain.setType(entityType);
    domain.setCreatedDate(new Date());
    domain.setBudget(20L);
    domain.setCoverFileId(1L);
    domain.setAvatarFileId(2L);
    domain.setAudienceId(audienceId);
    domain.setOwners(owners);
    ProgramEntity createdDomain = programDAO.create(domain);
    domainStorage.clearCache();
    restartTransaction();
    return createdDomain;
  }

  protected ProgramDTO newProgram(EntityType entityType, String name, boolean status, Set<Long> owners) {
    return ProgramMapper.fromEntity(ruleDAO, newDomain(entityType, name, status, owners));
  }

  protected ProgramEntity newOpenProgram(String name) {
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
    domain = programDAO.create(domain);
    domainStorage.clearCache();
    restartTransaction();
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
    domainStorage.clearCache();
    restartTransaction();
    return domain;
  }

  protected BadgeEntity newBadge(long domainId) {
    ProgramEntity domainEntity = programDAO.find(domainId);
    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(10);
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_EARNER);
    badge.setCreatedDate(new Date());
    badge.setLastModifiedBy(TEST_USER_EARNER);
    badge.setLastModifiedDate(new Date());
    badge.setDomainEntity(domainEntity);
    badge = badgeStorage.create(badge);
    return badge;
  }

  protected BadgeEntity newBadgeWithScore() {
    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(BADGE_NAME);
    badge.setDescription("Description");
    badge.setNeededScore(160);
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_EARNER);
    badge.setCreatedDate(new Date());
    badge.setLastModifiedBy(TEST_USER_EARNER);
    badge.setLastModifiedDate(new Date());
    badge.setDomainEntity(newDomain());
    badge = badgeStorage.create(badge);
    return badge;
  }

  protected BadgeEntity newBadge(String name, long domainId) {
    ProgramEntity domainEntity = programDAO.find(domainId);
    BadgeEntity badge = new BadgeEntity();
    badge.setTitle(name);
    badge.setDescription("Description");
    badge.setNeededScore(Integer.parseInt(TEST_GLOBAL_SCORE));
    badge.setIconFileId(10245);
    badge.setEnabled(true);
    badge.setDeleted(false);
    badge.setCreatedBy(TEST_USER_EARNER);
    badge.setCreatedDate(new Date());
    badge.setLastModifiedBy(TEST_USER_EARNER);
    badge.setLastModifiedDate(new Date());
    badge.setDomainEntity(domainEntity);
    badge = badgeStorage.create(badge);
    return badge;
  }

  protected RealizationEntity newRealizationEntity(String ruleName, long domainId) {
    RuleEntity rule = newRule(ruleName, domainId);
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(RealizationStatus.ACCEPTED);
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_EARNER);
    gHistory.setEarnerId(TEST_USER_EARNER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setCreatedBy("gamification");
    gHistory.setObjectId("objectId");
    gHistory.setObjectType("objectType");
    gHistory.setCreatedDate(fromDate);
    gHistory.setType(rule.getType());
    gHistory = realizationDAO.create(gHistory);
    restartTransaction();
    return gHistory;
  }

  protected RealizationEntity newRealizationByRuleByEarnerId(RuleEntity rule, String earnerId) {
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(RealizationStatus.ACCEPTED);
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(earnerId);
    gHistory.setEarnerId(earnerId);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setCreatedBy("gamification");
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory.setType(EntityType.AUTOMATIC);
    gHistory = realizationDAO.create(gHistory);
    restartTransaction();
    return gHistory;
  }

  protected RealizationEntity newRealizationEntityWithRuleId(String actionTitle, Long ruleId) {
    RuleEntity rule = ruleDAO.find(ruleId);
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(RealizationStatus.ACCEPTED);
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_EARNER);
    gHistory.setEarnerId(TEST_USER_EARNER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(actionTitle);
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory.setCreatedBy("gamification");
    gHistory.setType(rule.getType());
    gHistory = realizationDAO.create(gHistory);
    return gHistory;
  }

  protected RealizationEntity newRealizationsByStatus(RealizationStatus status) {
    RuleEntity rule = newRule();
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(status);
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_EARNER);
    gHistory.setEarnerId(TEST_USER_EARNER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory.setType(rule.getType());
    gHistory = realizationDAO.create(gHistory);
    return gHistory;
  }

  protected RealizationEntity newRealizationsByEarnerId(String earnerId) {
    RuleEntity rule = newRule();
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(RealizationStatus.ACCEPTED);
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_EARNER);
    gHistory.setEarnerId(earnerId);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(rule.getTitle());
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(fromDate);
    gHistory.setType(rule.getType());
    gHistory = realizationDAO.create(gHistory);
    return gHistory;
  }

  protected RealizationEntity newRealizationToBeSortedByActionTypeInDateRange(Date createdDate,
                                                                              String actionTitle,
                                                                              Long ruleId) {
    RuleEntity rule = ruleDAO.find(ruleId);
    RealizationEntity gHistory = new RealizationEntity();
    gHistory.setStatus(RealizationStatus.ACCEPTED);
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setDomain(rule.getDomainEntity().getTitle());
    gHistory.setDomainEntity(rule.getDomainEntity());
    gHistory.setReceiver(TEST_USER_EARNER);
    gHistory.setEarnerId(TEST_USER_EARNER);
    gHistory.setEarnerType(IdentityType.USER);
    gHistory.setActionTitle(actionTitle);
    gHistory.setActionScore(rule.getScore());
    gHistory.setGlobalScore(rule.getScore());
    gHistory.setRuleEntity(rule);
    gHistory.setCreatedBy("gamification");
    gHistory.setDomainEntity(newDomain());
    gHistory.setObjectId("objectId");
    gHistory.setCreatedDate(createdDate);
    gHistory.setType(rule.getType());
    gHistory = realizationDAO.create(gHistory);
    return gHistory;
  }

  protected RealizationDTO newRealizationDTO() {
    ProgramEntity domainEntity = newDomain();
    return RealizationMapper.fromEntity(domainStorage, newRealizationEntity("rule", domainEntity.getId()));
  }

  protected RuleDTO newRuleDTO() {
    return RuleMapper.fromEntity(domainStorage, newRule());
  }

  protected RuleDTO newRuleDTO(String name, long domainId) {
    return RuleMapper.fromEntity(domainStorage, newRule(name, domainId));
  }

  protected ProgramDTO newProgram() {
    return ProgramMapper.fromEntity(ruleDAO, newDomain());
  }

  protected ProgramDTO newProgram(String name) {
    return ProgramMapper.fromEntity(ruleDAO, newDomain(name));
  }

  protected BadgeDTO newBadgeDTO() {
    return BadgeMapper.fromEntity(domainStorage, newBadge(1L));
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

  protected ContainerResponse getResponse(String method, String restPath, String input) throws Exception {
    byte[] jsonData = (StringUtils.isBlank(input) ? "" : input).getBytes("UTF-8");
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.putSingle("content-type", "application/json");
    headers.putSingle("content-length", "" + jsonData.length);
    return launcher.service(method, restPath, "", headers, jsonData, null);
  }

  public String getURLResource(String resourceURL) {
    return "/gamification/" + resourceURL;
  }
}
