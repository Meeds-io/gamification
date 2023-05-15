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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.exoplatform.addons.gamification.connector.RuleIndexingServiceConnectorTest;
import org.exoplatform.addons.gamification.listener.AnnouncementActivityUpdaterTest;
import org.exoplatform.addons.gamification.listener.GamificationProfileListenerTest;
import org.exoplatform.addons.gamification.listener.GamificationRelationshipListenerTest;
import org.exoplatform.addons.gamification.listener.GamificationSpaceListenerTest;
import org.exoplatform.addons.gamification.listener.RulesESListenerTest;
import org.exoplatform.addons.gamification.rest.TestAnnouncementRest;
import org.exoplatform.addons.gamification.rest.TestBadgeRest;
import org.exoplatform.addons.gamification.rest.TestGamificationInformationsEndpoint;
import org.exoplatform.addons.gamification.rest.TestGamificationRestEndpoint;
import org.exoplatform.addons.gamification.rest.TestLeaderboardEndpoint;
import org.exoplatform.addons.gamification.rest.TestProgramRest;
import org.exoplatform.addons.gamification.rest.TestRealizationRest;
import org.exoplatform.addons.gamification.rest.TestRuleRest;
import org.exoplatform.addons.gamification.rest.TestSpaceLeaderboardEndpoint;
import org.exoplatform.addons.gamification.rest.TestUserReputationEndpoint;
import org.exoplatform.addons.gamification.search.RuleSearchConnectorTest;
import org.exoplatform.addons.gamification.service.AnnouncementServiceTest;
import org.exoplatform.addons.gamification.service.BadgeRegistryTest;
import org.exoplatform.addons.gamification.service.BadgeServiceTest;
import org.exoplatform.addons.gamification.service.ProgramServiceTest;
import org.exoplatform.addons.gamification.service.RealizationServiceITTest;
import org.exoplatform.addons.gamification.service.RealizationServiceTest;
import org.exoplatform.addons.gamification.service.RuleServiceTest;
import org.exoplatform.addons.gamification.storage.AnnouncementStorageTest;
import org.exoplatform.addons.gamification.storage.ProgramStorageTest;
import org.exoplatform.addons.gamification.storage.RealizationsStorageTest;
import org.exoplatform.addons.gamification.storage.RuleStorageTest;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAOTest;
import org.exoplatform.addons.gamification.storage.dao.ProgramDAOTest;
import org.exoplatform.addons.gamification.storage.dao.RealizationDAOTest;
import org.exoplatform.addons.gamification.storage.dao.RuleDAOTest;
import org.exoplatform.addons.gamification.upgrade.RuleIndexingUpgradePluginTest;
import org.exoplatform.addons.gamification.utils.UtilsTest;
import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.exoplatform.commons.testing.ConfigTestCase;

import io.meeds.gamification.analytics.AnalyticsAnnouncementListenerTest;
import io.meeds.gamification.analytics.AnalyticsProgramListenerTest;
import io.meeds.gamification.analytics.AnalyticsRuleListenerTest;

@RunWith(Suite.class)
@SuiteClasses({
    RealizationServiceITTest.class,
    BadgeServiceTest.class,
    BadgeRegistryTest.class,
    ProgramServiceTest.class,
    BadgeDAOTest.class,
    RuleDAOTest.class,
    RealizationDAOTest.class,
    ProgramDAOTest.class,
    ProgramStorageTest.class,
    AnnouncementStorageTest.class,
    RealizationsStorageTest.class,
    RuleStorageTest.class,
    AnnouncementServiceTest.class,
    RuleServiceTest.class,
    RealizationServiceTest.class,
    UtilsTest.class,
    TestGamificationInformationsEndpoint.class,
    TestProgramRest.class,
    TestUserReputationEndpoint.class,
    TestLeaderboardEndpoint.class,
    TestSpaceLeaderboardEndpoint.class,
    TestGamificationRestEndpoint.class,
    TestBadgeRest.class,
    TestRealizationRest.class,
    TestRuleRest.class,
    TestAnnouncementRest.class,
    GamificationProfileListenerTest.class,
    AnnouncementActivityUpdaterTest.class,
    RuleSearchConnectorTest.class,
    RuleIndexingUpgradePluginTest.class,
    RuleIndexingServiceConnectorTest.class,
    RulesESListenerTest.class,
    GamificationRelationshipListenerTest.class,
    AnalyticsAnnouncementListenerTest.class,
    AnalyticsProgramListenerTest.class,
    AnalyticsRuleListenerTest.class,
    GamificationSpaceListenerTest.class,
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
