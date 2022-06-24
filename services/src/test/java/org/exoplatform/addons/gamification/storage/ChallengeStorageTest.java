package org.exoplatform.addons.gamification.storage;


import org.exoplatform.addons.gamification.storage.dao.RuleDAO;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class ChallengeStorageTest {
  private RuleDAO     challengeDAO;

  private RuleStorage challengeStorage;

  @Before
  public void setUp() throws Exception { // NOSONAR
    challengeDAO = mock(RuleDAO.class);
  }

}
