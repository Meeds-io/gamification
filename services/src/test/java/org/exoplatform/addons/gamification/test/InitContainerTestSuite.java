package org.exoplatform.addons.gamification.test;

import org.exoplatform.addons.gamification.service.TestGamificationService;
import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.exoplatform.commons.testing.ConfigTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.exoplatform.addons.gamification.test.rest.TestManageBadgesEndpoint;

@RunWith(Suite.class)
@SuiteClasses({
        TestGamificationService.class,
        TestManageBadgesEndpoint.class
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
