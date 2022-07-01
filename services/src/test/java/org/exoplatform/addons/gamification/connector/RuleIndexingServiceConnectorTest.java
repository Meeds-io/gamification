package org.exoplatform.addons.gamification.connector;

import org.exoplatform.addons.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.addons.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.search.domain.Document;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class RuleIndexingServiceConnectorTest extends AbstractServiceTest {

  public static final String INDEX = "rules";

  @Test
  public void testGetConnectorName() {
    assertEquals(INDEX, ruleIndexingServiceConnector.getConnectorName());
  }

  @Test
  public void testCreate() {
    assertThrows(IllegalArgumentException.class, () -> ruleIndexingServiceConnector.update(""));
    assertThrows(IllegalStateException.class, () -> ruleIndexingServiceConnector.update("5122165"));

    RuleEntity rule = newRule("test", "gamification", 1l);
    Document document = ruleIndexingServiceConnector.create(String.valueOf(rule.getId()));
    assertNotNull(document);
    assertEquals((long) rule.getId(), Long.parseLong(document.getId()));
    assertNotNull(document.getFields());
  }

  @Test
  public void testUpdate() {
    assertThrows(IllegalArgumentException.class, () -> ruleIndexingServiceConnector.update(""));
    assertThrows(IllegalStateException.class, () -> ruleIndexingServiceConnector.update("5122165"));

    RuleEntity rule = newRule("test", "gamification", 1l);
    Document document = ruleIndexingServiceConnector.update(String.valueOf(rule.getId()));
    assertNotNull(document);
    assertEquals((long) rule.getId(), Long.parseLong(document.getId()));
    assertNotNull(document.getFields());
  }

  @Test
  public void getAllIds() {
    assertThrows(UnsupportedOperationException.class, () -> ruleIndexingServiceConnector.getAllIds(0, 10));
  }

}
