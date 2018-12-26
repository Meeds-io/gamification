package org.exoplatform.addons.gamification.service;

import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.junit.Before;

public class TestGamificationService extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testAddActivityOnUserStream() throws Exception {
        Identity maryIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "mary", false);
    }
}
