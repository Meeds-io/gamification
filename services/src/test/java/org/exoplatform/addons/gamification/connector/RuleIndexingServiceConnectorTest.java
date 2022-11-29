/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

package org.exoplatform.addons.gamification.connector;

import org.exoplatform.addons.gamification.entity.RuleEntity;
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
