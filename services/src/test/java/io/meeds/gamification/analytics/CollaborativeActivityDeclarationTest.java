/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import io.meeds.gamification.utils.Utils;

import tools.jackson.databind.ObjectMapper;

/**
 * The collaborative activity declaration analytics reads from this jar:
 * publishing a quest and submitting a contribution are not counted (spec
 * section 3.6, board task US3), so the type of the stream activity a quest
 * publishes and the type of the contribution comment are excluded. That
 * comment carries the member's own text under the member's identity: it is
 * excluded by the functional rule, not because the system writes it.
 * challenges-announcement is the type announcements had while they were
 * posts, before 1.5.0; events stored then keep it whatever happens to the
 * deprecated constant, so the test names it by its value. Nothing of the
 * gamification is counted.
 */
public class CollaborativeActivityDeclarationTest {

  private static final String DECLARATION_FILE = "analytics-collaborative-activities.json";

  @Test
  @SuppressWarnings("unchecked")
  public void theDeclarationExcludesTheQuestAndContributionTypes() throws IOException {
    Map<String, Object> declaration;
    try (InputStream content = getClass().getClassLoader().getResourceAsStream(DECLARATION_FILE)) {
      assertNotNull("the declaration ships at the root of the gamification services jar, which lands in /lib", content);
      declaration = new ObjectMapper().readValue(content, Map.class);
    }

    assertEquals(List.of("gamification"), declaration.get("modules"));
    assertFalse("creating a rule or a realization is not collaboration", declaration.containsKey("operations"));
    assertFalse(declaration.containsKey("legacyOperations"));
    assertFalse(declaration.containsKey("browserOperations"));
    Set<String> expectedTypes = Set.of(Utils.RULE_ACTIVITY_TYPE, "challenges-announcement", Utils.ANNOUNCEMENT_COMMENT_TYPE);
    List<String> excludedTypes = (List<String>) declaration.get("excludedActivityTypes");
    assertEquals(expectedTypes, new HashSet<>(excludedTypes));
    assertEquals("one entry per type", expectedTypes.size(), excludedTypes.size());

    assertFalse("nothing of the gamification is counted, so it has no help label", declaration.containsKey("labels"));
  }

}
