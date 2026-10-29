/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

import io.meeds.mcp.server.constant.Origin;
import io.meeds.mcp.server.model.ActingIdentity;
import io.meeds.mcp.server.model.SimpleToolDefinition;
import io.meeds.mcp.server.model.SpaceScopeDeclaration;
import io.meeds.mcp.server.model.SpaceScopeRefusedException;
import io.meeds.mcp.server.plugin.SpaceScopeResolver;
import io.meeds.mcp.server.service.McpToolSpaceScopeService;
import io.meeds.mcp.server.util.McpToolUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Pins the {@code space_scope} declarations this module ships: which gamification
 * tools a call limited to one space may use, and on which arguments.
 */
public class GamificationToolDefinitionSpaceScopeTest {

  private static final String                DEFINITIONS      = "ai-tool-definitions.json";

  /**
   * The reads of a space, and the asker's own reads: the answer of the latter
   * is posted in the space, so they keep to the call's space like the former,
   * never declared unscoped.
   */
  private static final Set<String>           SPACE_READS      = Set.of("list_campaigns",
                                                                       "get_gamification_leaderboard",
                                                                       "get_my_gamification_score",
                                                                       "list_my_realizations",
                                                                       "list_my_announcements",
                                                                       "get_my_points_total",
                                                                       "get_my_score_breakdown");

  private static final Map<String, String>   PROGRAM_READS    = Map.of("get_campaign",
                                                                      "campaign_id",
                                                                      "list_quests",
                                                                      "campaign_id",
                                                                      "list_campaign_badges",
                                                                      "campaign_id");

  private static final Map<String, String>   RULE_READS       = Map.of("get_quest", "quest_id");

  /**
   * No gamification tool reads nothing that belongs to a space: every
   * campaign, quest, realization and announcement has the space of its
   * campaign.
   */
  private static final Set<String>           UNSCOPED_READS   = Set.of();

  private static final Set<String>           SPACE_WRITES     = Set.of("create_campaign");

  private static final Map<String, String>   RULE_WRITES      = Map.of("update_quest",
                                                                       "quest_id",
                                                                       "delete_quest",
                                                                       "quest_id",
                                                                       "announce_quest",
                                                                       "quest_id");

  private static final Map<String, String>   PROGRAM_WRITES   = Map.of("create_quest", "campaign_id");

  private static final Set<String>           SPACE_ADMIN      = Set.of("create_campaign",
                                                                       "create_quest",
                                                                       "update_quest",
                                                                       "delete_quest");

  /**
   * The schema properties a limited read deliberately leaves out, because
   * they reach a campaign or a quest, and so possibly another space or
   * person, with no resolver to tell.
   */
  private static final Map<String, Set<String>> EXCLUDED_READ_ARGS = Map.of("get_gamification_leaderboard",
                                                                           Set.of("campaign_id"),
                                                                           "get_my_gamification_score",
                                                                           Set.of("campaign_id"),
                                                                           "get_my_points_total",
                                                                           Set.of("campaign_id"),
                                                                           "list_my_realizations",
                                                                           Set.of("campaign_id"),
                                                                           "list_my_announcements",
                                                                           Set.of("quest_id"));

  /**
   * The tools that stay undeclared: personal across spaces, platform-wide, or
   * running as a different person than the call's asker.
   * {@code check_quest_availability} is undeclared for the last reason: under
   * a space scope, reads run as the asker while the announce that follows it
   * runs as the worker, so the pre-check would answer for the wrong person.
   */
  private static final Set<String>           UNDECLARED       = Set.of("get_my_campaigns",
                                                                       "list_joinable_campaigns",
                                                                       "check_quest_availability");

  /**
   * An argument shaped like a reference to a place or an object: an id or
   * ids, in snake or camel case, a folder, a space.
   */
  private static final Pattern               REFERENCE_SHAPED = Pattern.compile("(^|_)ids?$|[a-z]Ids?$|(?i:folder|space)");

  private static final JsonMapper            MAPPER           = JsonMapper.builder().build();

  /**
   * Exactly the expected tools declare a space scope, each of the expected
   * kind and resolver, and each well formed for what it is. Mutants: a
   * declaration dropped; {@code when_absent} dropped; a resolver renamed; a
   * personal tool declared; a personal read declared unscoped.
   */
  @Test
  public void declaredToolsAreTheExpectedOnes() {
    Map<String, SimpleToolDefinition> declared = declaredTools();

    Set<String> expected = new LinkedHashSet<>();
    expected.addAll(SPACE_READS);
    expected.addAll(PROGRAM_READS.keySet());
    expected.addAll(RULE_READS.keySet());
    expected.addAll(SPACE_WRITES);
    expected.addAll(RULE_WRITES.keySet());
    expected.addAll(PROGRAM_WRITES.keySet());
    expected.add("cancel_quest_announcement");
    assertEquals(expected, declared.keySet());
    assertEquals(17, declared.size());
    declared.forEach((name, tool) -> {
      boolean write = SPACE_WRITES.contains(name) || RULE_WRITES.containsKey(name) || PROGRAM_WRITES.containsKey(name)
          || "cancel_quest_announcement".equals(name);
      assertNotNull(tool.getAnnotations());
      assertEquals(name, !write, McpToolSpaceScopeService.isReadTool(tool));
      assertTrue(name, tool.getSpaceScope().isWellFormedFor(write));
    });
    SPACE_READS.forEach(name -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(name, scope.isSpaceKind());
      assertEquals(name, "space_id", scope.getSpaceArg());
      assertTrue(name, scope.isUseScopeWhenAbsent());
    });
    SPACE_WRITES.forEach(name -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(name, scope.isSpaceKind());
      assertEquals(name, "space_id", scope.getSpaceArg());
      assertNull(name, scope.getWhenAbsent());
    });
    declared.forEach((name, tool) -> assertEquals(name, UNSCOPED_READS.contains(name), tool.getSpaceScope().isUnscopedKind()));
    assertObjectKind(declared, PROGRAM_READS, ProgramSpaceScopeResolver.NAME);
    assertObjectKind(declared, PROGRAM_WRITES, ProgramSpaceScopeResolver.NAME);
    assertObjectKind(declared, RULE_READS, RuleSpaceScopeResolver.NAME);
    assertObjectKind(declared, RULE_WRITES, RuleSpaceScopeResolver.NAME);
    assertObjectKind(declared, Map.of("cancel_quest_announcement", "announcement_id"), AnnouncementSpaceScopeResolver.NAME);
  }

  /**
   * The three tools that are personal across spaces, platform-wide or run as
   * another person than the asker stay undeclared, so a call limited to a space
   * never runs them; in particular {@code check_quest_availability}, whose answer under a
   * scope would be the asker's while the announce runs as the worker. Mutant: a
   * space scope declared on one of them.
   */
  @Test
  public void personalAndPlatformWideToolsStayUndeclared() {
    List<SimpleToolDefinition> shipped = McpToolUtils.parseToolDefinitions(definitionsUrl());

    for (String name : UNDECLARED) {
      SimpleToolDefinition tool = shipped.stream().filter(t -> name.equals(t.getName())).findFirst().orElseThrow();
      assertNull(name, tool.getSpaceScope());
    }
    assertEquals(20, shipped.size());
  }

  /**
   * {@code cancel_quest_announcement} points to the announcement id that
   * {@code announce_quest} returns, which the announce result carries as
   * {@code announcement_id}, and not to {@code list_my_announcements}. Mutant:
   * the description pointing back to {@code list_my_announcements}.
   */
  @Test
  public void cancelAnnouncementDescriptionPointsToTheAnnounceResult() {
    String description = McpToolUtils.parseToolDefinitions(definitionsUrl())
                                     .stream()
                                     .filter(tool -> "cancel_quest_announcement".equals(tool.getName()))
                                     .findFirst()
                                     .orElseThrow()
                                     .getDescription();

    assertTrue(description, description.contains("announcement_id returned by announce_quest"));
    assertFalse(description, description.contains("list_my_announcements"));
  }

  /**
   * A read accepts exactly the tool's schema properties, but for the
   * reference-shaped ones deliberately left out; a write accepts only schema
   * properties, and leaves out only reference-shaped ones. Mutants: an
   * argument dropped from a read's allowed list; a property no schema holds
   * added; the leaderboard's {@code campaign_id} accepted.
   */
  @Test
  public void acceptedArgumentsAreTheSchemaProperties() {
    declaredTools().forEach((name, tool) -> {
      Set<String> schema = new LinkedHashSet<>(properties(tool));
      Set<String> accepted = acceptedArguments(tool.getSpaceScope());
      if (McpToolSpaceScopeService.isReadTool(tool)) {
        schema.removeAll(EXCLUDED_READ_ARGS.getOrDefault(name, Set.of()));
        assertEquals(name, schema, accepted);
      } else {
        assertTrue(name, properties(tool).containsAll(accepted));
        schema.stream()
              .filter(argument -> !accepted.contains(argument))
              .forEach(argument -> assertTrue(name + " leaves out " + argument, isReferenceShaped(argument)));
      }
    });
    EXCLUDED_READ_ARGS.forEach((name, excluded) -> excluded.forEach(argument -> {
      assertTrue(name + " " + argument, isReferenceShaped(argument));
      assertTrue(name + " " + argument, properties(declaredTools().get(name)).contains(argument));
    }));
  }

  /**
   * No tool lets a call limited to a space pass a reference to another place
   * or object besides the one it names as its place: nothing in an allowed
   * list is shaped like an id, a folder or a space. Mutant: {@code
   * campaign_id} or {@code space_id} added to an allowed list.
   */
  @Test
  public void allowedArgumentsCarryNoReference() {
    declaredTools().forEach((name, tool) -> {
      List<String> others = tool.getSpaceScope().getAllowedArgs() == null ? List.of() : tool.getSpaceScope().getAllowedArgs();
      others.forEach(argument -> assertFalse(name + " accepts " + argument, isReferenceShaped(argument)));
    });
    for (String reference : List.of("id", "ids", "campaign_id", "quest_id", "spaceId", "folderId", "Space", "space_id")) {
      assertTrue(reference, isReferenceShaped(reference));
    }
    for (String plain : List.of("title", "score", "comment", "limit", "period", "budget")) {
      assertFalse(plain, isReferenceShaped(plain));
    }
  }

  /**
   * {@code space_admin} sits exactly on the four writes that administer a
   * program: creating a campaign, creating, updating and deleting a quest;
   * announcing a quest and cancelling an announcement don't carry it.
   * Mutants: the flag dropped from one; the flag added to another.
   */
  @Test
  public void spaceAdminIsExactlyOnTheFourAdminWrites() {
    Set<String> flagged = declaredTools().entrySet()
                                         .stream()
                                         .filter(entry -> entry.getValue().getSpaceScope().isSpaceAdmin())
                                         .map(Map.Entry::getKey)
                                         .collect(Collectors.toSet());

    assertEquals(SPACE_ADMIN, flagged);
  }

  /**
   * The shipped definitions, run through the server's check with mocked
   * resolvers: an object of the call's space passes, one of another space or
   * of none is refused, a space read without a space is filled with the call's
   * space, a place elsewhere or an argument outside the list is refused, a
   * write is refused outside an approved proposal, and an undeclared tool is
   * refused. Mutants: a resolver name changed; a declaration dropped.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void shippedDeclarationsAreEnforcedUnderASpaceScope() {
    ObjectProvider<SpaceScopeResolver> provider = mock(ObjectProvider.class);
    when(provider.orderedStream()).thenAnswer(invocation -> Stream.of(resolver(ProgramSpaceScopeResolver.NAME, "5", "6"),
                                                                      resolver(RuleSpaceScopeResolver.NAME, "7", "8"),
                                                                      resolver(AnnouncementSpaceScopeResolver.NAME, "3", "4")));
    McpToolSpaceScopeService service = new McpToolSpaceScopeService(provider);
    ActingIdentity read = ActingIdentity.agent("agent-gamif", "GAMIF_ASSISTANT", Origin.MENTION, 12L);
    ActingIdentity write = ActingIdentity.agent("agent-gamif", "GAMIF_ASSISTANT", Origin.PROPOSAL, 12L);
    Map<String, SimpleToolDefinition> shipped = McpToolUtils.parseToolDefinitions(definitionsUrl())
                                                            .stream()
                                                            .collect(Collectors.toMap(SimpleToolDefinition::getName, t -> t));

    // objects, by id
    for (String[] tool : new String[][] { { "get_campaign", "campaign_id", "5", "6" },
                                          { "list_quests", "campaign_id", "5", "6" },
                                          { "list_campaign_badges", "campaign_id", "5", "6" },
                                          { "get_quest", "quest_id", "7", "8" } }) {
      assertAllowed(service, shipped, tool[0], "{\"" + tool[1] + "\":" + tool[2] + "}", read);
      assertRefused(service, shipped, tool[0], "{\"" + tool[1] + "\":" + tool[3] + "}", read);
      assertRefused(service, shipped, tool[0], "{\"" + tool[1] + "\":9}", read);
    }
    assertAllowed(service, shipped, "list_quests", "{\"campaign_id\":5,\"type\":\"MANUAL\",\"limit\":5}", read);
    assertRefused(service, shipped, "list_quests", "{}", read);
    assertRefused(service, shipped, "list_quests", "{\"type\":\"MANUAL\"}", read);
    // spaces
    String filled = service.enforce("list_campaigns", shipped.get("list_campaigns"), "{\"term\":\"x\"}", read);
    assertTrue(filled, filled.contains("\"space_id\":12"));
    filled = service.enforce("get_gamification_leaderboard", shipped.get("get_gamification_leaderboard"), "{}", read);
    assertTrue(filled, filled.contains("\"space_id\":12"));
    assertRefused(service, shipped, "list_campaigns", "{\"space_id\":13}", read);
    assertRefused(service, shipped, "get_gamification_leaderboard", "{\"space_id\":13}", read);
    assertRefused(service, shipped, "get_gamification_leaderboard", "{\"campaign_id\":6}", read);
    assertRefused(service, shipped, "get_gamification_leaderboard", "{\"campaign_id\":5}", read);
    // the asker's own reads, kept to the call's space
    for (String[] tool : new String[][] { { "get_my_gamification_score", "{}" },
                                          { "list_my_realizations", "{\"status\":\"ACCEPTED\",\"limit\":5}" },
                                          { "list_my_announcements", "{\"limit\":5,\"offset\":0}" },
                                          { "get_my_points_total", "{\"from_date\":\"2026-01-01\",\"to_date\":\"2026-02-01\"}" },
                                          { "get_my_score_breakdown", "{\"period\":\"MONTH\"}" } }) {
      filled = service.enforce(tool[0], shipped.get(tool[0]), tool[1], read);
      assertTrue(tool[0] + " " + filled, filled.contains("\"space_id\":12"));
      assertRefused(service, shipped, tool[0], "{\"space_id\":13}", read);
      assertAllowed(service, shipped, tool[0], "{\"space_id\":12}", read);
    }
    assertRefused(service, shipped, "get_my_points_total", "{\"campaign_id\":6}", read);
    assertRefused(service, shipped, "get_my_score_breakdown", "{\"campaign_id\":6}", read);
    assertRefused(service, shipped, "get_my_gamification_score", "{\"campaign_id\":6}", read);
    assertRefused(service, shipped, "list_my_realizations", "{\"campaign_id\":6}", read);
    assertRefused(service, shipped, "list_my_announcements", "{\"quest_id\":8}", read);
    assertRefused(service, shipped, "list_my_realizations", "{\"space_id\":12,\"campaign_id\":6}", read);
    // undeclared
    for (String name : UNDECLARED) {
      assertRefused(service, shipped, name, "{}", read);
    }
    // writes
    assertAllowed(service, shipped, "create_campaign", "{\"space_id\":12,\"title\":\"T\",\"budget\":10}", write);
    assertRefused(service, shipped, "create_campaign", "{\"space_id\":13,\"title\":\"T\"}", write);
    assertRefused(service, shipped, "create_campaign", "{\"title\":\"T\"}", write);
    assertRefused(service, shipped, "create_campaign", "{\"space_id\":12,\"title\":\"T\",\"open\":true}", write);
    assertRefused(service, shipped, "create_campaign", "{\"space_id\":12,\"title\":\"T\"}", read);
    assertAllowed(service, shipped, "create_quest", "{\"campaign_id\":5,\"title\":\"T\",\"score\":3}", write);
    assertRefused(service, shipped, "create_quest", "{\"campaign_id\":6,\"title\":\"T\"}", write);
    assertAllowed(service, shipped, "update_quest", "{\"quest_id\":7,\"title\":\"T\"}", write);
    assertRefused(service, shipped, "update_quest", "{\"quest_id\":8,\"title\":\"T\"}", write);
    assertAllowed(service, shipped, "delete_quest", "{\"quest_id\":7}", write);
    assertRefused(service, shipped, "delete_quest", "{\"quest_id\":8}", write);
    assertAllowed(service, shipped, "announce_quest", "{\"quest_id\":7,\"comment\":\"c\"}", write);
    assertRefused(service, shipped, "announce_quest", "{\"quest_id\":8}", write);
    assertAllowed(service, shipped, "cancel_quest_announcement", "{\"announcement_id\":3}", write);
    assertRefused(service, shipped, "cancel_quest_announcement", "{\"announcement_id\":4}", write);
    assertRefused(service, shipped, "cancel_quest_announcement", "{\"announcement_id\":3}", read);
  }

  /**
   * Asserts the server's check lets the call through untouched.
   */
  private static void assertAllowed(McpToolSpaceScopeService service,
                                    Map<String, SimpleToolDefinition> shipped,
                                    String name,
                                    String input,
                                    ActingIdentity identity) {
    assertEquals(name + " " + input, input, service.enforce(name, shipped.get(name), input, identity));
  }

  /**
   * Asserts the server's check refuses the call.
   */
  private static void assertRefused(McpToolSpaceScopeService service,
                                    Map<String, SimpleToolDefinition> shipped,
                                    String name,
                                    String input,
                                    ActingIdentity identity) {
    assertThrows(name + " " + input,
                 SpaceScopeRefusedException.class,
                 () -> service.enforce(name, shipped.get(name), input, identity));
  }

  /**
   * Asserts each tool declares the object kind on the given argument and resolver.
   */
  private static void assertObjectKind(Map<String, SimpleToolDefinition> declared, Map<String, String> tools, String resolver) {
    tools.forEach((name, argument) -> {
      SpaceScopeDeclaration scope = declared.get(name).getSpaceScope();
      assertTrue(name, scope.isObjectKind());
      assertEquals(name, argument, scope.getObjectArg());
      assertEquals(name, resolver, scope.getResolver());
    });
  }

  /**
   * @param name the resolver name
   * @param inSpace the object id that belongs to the call's space 12
   * @param elsewhere the object id that belongs to space 99
   * @return a mocked resolver, answering null for any other id
   */
  private static SpaceScopeResolver resolver(String name, String inSpace, String elsewhere) {
    SpaceScopeResolver resolver = mock(SpaceScopeResolver.class);
    when(resolver.getName()).thenReturn(name);
    when(resolver.spaceOf(inSpace)).thenReturn(12L);
    when(resolver.spaceOf(elsewhere)).thenReturn(99L);
    return resolver;
  }

  /**
   * Tells whether an argument is shaped like a reference to a place or an object.
   */
  private static boolean isReferenceShaped(String argument) {
    return REFERENCE_SHAPED.matcher(argument).find();
  }

  /**
   * Reads the property names of a tool's input schema.
   */
  private static Set<String> properties(SimpleToolDefinition tool) {
    JsonNode properties = MAPPER.readTree(tool.getInputSchema()).path("properties");
    return new LinkedHashSet<>(properties.propertyNames());
  }

  /**
   * Lists the arguments a limited call may carry under a declaration.
   */
  private static Set<String> acceptedArguments(SpaceScopeDeclaration scope) {
    Set<String> accepted = new LinkedHashSet<>();
    if (scope.getSpaceArg() != null) {
      accepted.add(scope.getSpaceArg());
    }
    if (scope.getObjectArg() != null) {
      accepted.add(scope.getObjectArg());
    }
    if (scope.getAllowedArgs() != null) {
      accepted.addAll(scope.getAllowedArgs());
    }
    if (scope.getFalseOnlyArgs() != null) {
      accepted.addAll(scope.getFalseOnlyArgs());
    }
    return accepted;
  }

  /**
   * Locates the shipped tool definitions.
   */
  private URL definitionsUrl() {
    return getClass().getClassLoader().getResource(DEFINITIONS);
  }

  /**
   * Lists the shipped tools that declare a space scope, by name.
   */
  private Map<String, SimpleToolDefinition> declaredTools() {
    return McpToolUtils.parseToolDefinitions(definitionsUrl())
                       .stream()
                       .filter(tool -> tool.getSpaceScope() != null)
                       .collect(Collectors.toMap(SimpleToolDefinition::getName, tool -> tool));
  }

}
