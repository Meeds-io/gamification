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
package io.meeds.gamification.test;

import io.meeds.gamification.listener.*;
import io.meeds.gamification.notification.plugin.ContributionStatusUpdatedNotificationPluginTest;
import io.meeds.gamification.plugin.*;
import io.meeds.gamification.service.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.exoplatform.commons.testing.ConfigTestCase;

import io.meeds.gamification.analytics.AnalyticsAnnouncementListenerTest;
import io.meeds.gamification.analytics.AnalyticsProgramListenerTest;
import io.meeds.gamification.analytics.AnalyticsRealizationListenerTest;
import io.meeds.gamification.analytics.AnalyticsRuleListenerTest;
import io.meeds.gamification.connector.RuleIndexingServiceConnectorTest;
import io.meeds.gamification.dao.BadgeDAOTest;
import io.meeds.gamification.dao.ConnectorAccountDAOTest;
import io.meeds.gamification.dao.ProgramDAOTest;
import io.meeds.gamification.dao.RealizationDAOTest;
import io.meeds.gamification.dao.RuleDAOTest;
import io.meeds.gamification.notification.plugin.ActionAnnouncedNotificationPluginTest;
import io.meeds.gamification.notification.plugin.NewActionAvailableNotificationPluginTest;
import io.meeds.gamification.rest.TestAnnouncementRest;
import io.meeds.gamification.rest.TestBadgeRest;
import io.meeds.gamification.rest.TestConnectorRest;
import io.meeds.gamification.rest.TestLeaderboardRest;
import io.meeds.gamification.rest.TestProgramRest;
import io.meeds.gamification.rest.TestRealizationRest;
import io.meeds.gamification.rest.TestRuleRest;
import io.meeds.gamification.rest.TestUserReputationEndpoint;
import io.meeds.gamification.scheduled.ProgramAutoDisableJobTest;
import io.meeds.gamification.search.RuleSearchConnectorTest;
import io.meeds.gamification.storage.AnnouncementStorageTest;
import io.meeds.gamification.storage.ConnectorAccountStorageTest;
import io.meeds.gamification.storage.ProgramStorageTest;
import io.meeds.gamification.storage.RealizationsStorageTest;
import io.meeds.gamification.storage.RuleStorageTest;
import io.meeds.gamification.upgrade.ProgramVisibilityUpgradePluginTest;
import io.meeds.gamification.utils.UtilsTest;
import io.meeds.gamification.web.filter.PublicActionAccessFilterTest;

@RunWith(Suite.class)
@SuiteClasses({
    PublicActionAccessFilterTest.class,
    RealizationServiceTest.class,
    BadgeServiceTest.class,
    BadgeRegistryTest.class,
    ProgramServiceTest.class,
    BadgeDAOTest.class,
    RuleDAOTest.class,
    RealizationDAOTest.class,
    ProgramDAOTest.class,
    ConnectorAccountDAOTest.class,
    ProgramStorageTest.class,
    AnnouncementStorageTest.class,
    RealizationsStorageTest.class,
    RuleStorageTest.class,
    ConnectorAccountStorageTest.class,
    AnnouncementServiceTest.class,
    RuleServiceTest.class,
    RuleTranslationPluginTest.class,
    RuleAttachmentPluginTest.class,
    ProgramTranslationPluginTest.class,
    RealizationServiceMockTest.class,
    ConnectorSettingServiceTest.class,
    ConnectorServiceTest.class,
    UtilsTest.class,
    TestProgramRest.class,
    TestUserReputationEndpoint.class,
    TestLeaderboardRest.class,
    TestBadgeRest.class,
    TestRealizationRest.class,
    TestRuleRest.class,
    TestAnnouncementRest.class,
    TestConnectorRest.class,
    GamificationProfileListenerTest.class,
    GamificationNotificationListenerTest.class,
    AnnouncementActivityUpdaterTest.class,
    AnnouncementRealizationUpdaterTest.class,
    RuleSearchConnectorTest.class,
    RuleIndexingServiceConnectorTest.class,
    RuleIndexingListenerTest.class,
    GamificationRelationshipListenerTest.class,
    AnalyticsAnnouncementListenerTest.class,
    AnalyticsProgramListenerTest.class,
    AnalyticsRuleListenerTest.class,
    AnalyticsRealizationListenerTest.class,
    GamificationSpaceListenerTest.class,
    GamificationActivityListenerTest.class,
    ProgramSpaceListenerTest.class,
    ProgramAutoDisableListenerTest.class,
    ProgramAutoDisableJobTest.class,
    ActionAnnouncedNotificationPluginTest.class,
    NewActionAvailableNotificationPluginTest.class,
    ContributionStatusUpdatedNotificationPluginTest.class,
    RuleActivityTypePluginTest.class,
    AnnouncementActivityTypePluginTest.class,
    EventServiceTest.class,
    EventRegistryTest.class,
    TriggerServiceTest.class,
    ProgramVisibilityUpgradePluginTest.class,
    ProgramImportServiceTest.class
})
@ConfigTestCase(AbstractServiceTest.class)
public class InitContainerTestSuite extends BaseExoContainerTestSuite {

  @BeforeClass
  public static void setUp() throws Exception {
    initConfiguration(InitContainerTestSuite.class);
    beforeSetup();
  }

  @AfterClass
  public static void tearDown() {
    afterTearDown();
  }

}
