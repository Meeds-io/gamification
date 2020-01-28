package org.exoplatform.addons.gamification.test;

import org.exoplatform.addons.gamification.service.GamificationServiceTest;
import org.exoplatform.addons.gamification.service.configuration.BadgeServiceTest;
import org.exoplatform.addons.gamification.service.configuration.DomainServiceTest;
import org.exoplatform.addons.gamification.service.configuration.RuleServiceTest;
import org.exoplatform.addons.gamification.service.upgrade.RuleNameUpgradePluginTest;
import org.exoplatform.addons.gamification.storage.dao.BadgeDAOTest;
import org.exoplatform.addons.gamification.storage.dao.RuleDAOTest;
import org.exoplatform.addons.gamification.test.rest.TestManageBadgesEndpoint;
import org.exoplatform.addons.gamification.test.rest.TestManageDomainsEndpoint;
import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.exoplatform.commons.testing.ConfigTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        GamificationServiceTest.class,
        RuleServiceTest.class,
        BadgeServiceTest.class,
        DomainServiceTest.class,
        BadgeDAOTest.class,
        RuleDAOTest.class,
        TestManageDomainsEndpoint.class,
        TestManageBadgesEndpoint.class,
        RuleNameUpgradePluginTest.class
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
