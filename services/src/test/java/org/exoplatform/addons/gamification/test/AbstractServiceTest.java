package org.exoplatform.addons.gamification.test;

import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.spi.SpaceService;

@ConfiguredBy({
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/exo.gamification.component.core.test.configuration.xml"),

    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.portal-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.identity-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.test.jcr-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/exo.gamification.test.portal-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/exo.gamification.test.jcr-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/exo.XXXXXXgamification.component.core.test.application.registry.configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/component.search.configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/gamification-test-configuration.xml") })
public class AbstractServiceTest extends BaseExoTestCase {

  protected SpaceService         spaceService;

  protected IdentityManager      identityManager;

  protected RelationshipManager  relationshipManager;

  protected ActivityManager      activityManager;

  protected EntityManagerService entityManagerService;

  protected GamificationService  gamificationService;

  @Override
  protected void setUp() throws Exception {
    begin();

    identityManager = CommonsUtils.getService(IdentityManager.class);
    activityManager = CommonsUtils.getService(ActivityManager.class);
    relationshipManager = CommonsUtils.getService(RelationshipManager.class);
    spaceService = CommonsUtils.getService(SpaceService.class);
    entityManagerService = CommonsUtils.getService(EntityManagerService.class);

    identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "root", false);
    identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "john", true);
    identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "mary", true);
    identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "demo", true);
    gamificationService = CommonsUtils.getService(GamificationService.class);

  }

  @Override
  protected void tearDown() throws Exception {

    //
    end();
  }

}
