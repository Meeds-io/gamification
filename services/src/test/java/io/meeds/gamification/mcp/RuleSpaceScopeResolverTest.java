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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.RuleService;

/**
 * Pins which space a quest belongs to for the space scope decision.
 */
public class RuleSpaceScopeResolverTest {

  private RuleService            ruleService;

  private RuleSpaceScopeResolver resolver;

  /**
   * Creates the resolver over a mocked service.
   */
  @Before
  public void setUp() {
    ruleService = mock(RuleService.class);
    resolver = new RuleSpaceScopeResolver(ruleService);
  }

  /**
   * A quest of a space campaign belongs to that campaign's space, and the
   * resolver answers to the name the tool definitions give. Mutants: the
   * program not read; the name changed.
   */
  @Test
  public void questOfASpaceCampaignBelongsToItsSpace() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, false, false), false));

    assertEquals("rule", resolver.getName());
    assertEquals(Long.valueOf(12L), resolver.spaceOf("7"));
  }

  /**
   * A quest of an open campaign belongs to no space. Mutant: the open check
   * removed.
   */
  @Test
  public void questOfAnOpenCampaignBelongsToNoSpace() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, true, false), false));

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * A quest of a campaign of no space belongs to none. Mutant: the
   * {@code spaceId > 0} check removed.
   */
  @Test
  public void questOfACampaignOfNoSpaceBelongsToNone() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(0L, false, false), false));

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * A quest of a deleted campaign belongs to no space. Mutant: the deleted
   * check of the campaign removed.
   */
  @Test
  public void questOfADeletedCampaignBelongsToNoSpace() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, false, true), false));

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * A deleted quest belongs to no space. Mutant: the deleted check of the
   * quest removed.
   */
  @Test
  public void deletedQuestBelongsToNoSpace() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, false, false), true));

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * A quest without a campaign, and a missing quest, belong to no space.
   */
  @Test
  public void questWithoutCampaignOrMissingBelongsToNoSpace() {
    when(ruleService.findRuleById(7L)).thenReturn(rule(null, false));
    when(ruleService.findRuleById(8L)).thenReturn(null);

    assertNull(resolver.spaceOf("7"));
    assertNull(resolver.spaceOf("8"));
  }

  /**
   * An id that isn't a number is never looked up. Mutant: the numeric check
   * removed.
   */
  @Test
  public void nonNumericIdIsNotLookedUp() {
    assertNull(resolver.spaceOf("abc"));
    assertNull(resolver.spaceOf("7 OR 1=1"));
    assertNull(resolver.spaceOf("-7"));
    assertNull(resolver.spaceOf(""));
    assertNull(resolver.spaceOf(null));

    verify(ruleService, never()).findRuleById(anyLong());
  }

  /**
   * A lookup that throws reads as no space. Mutant: the catch removed.
   */
  @Test
  public void failingLookupBelongsToNoSpace() {
    when(ruleService.findRuleById(7L)).thenThrow(new IllegalStateException("boom"));

    assertNull(resolver.spaceOf("7"));
  }

  /**
   * Test fixture builder.
   */
  private static RuleDTO rule(ProgramDTO program, boolean deleted) {
    RuleDTO rule = new RuleDTO();
    rule.setId(7L);
    rule.setProgram(program);
    rule.setDeleted(deleted);
    return rule;
  }

  /**
   * Test fixture builder.
   */
  private static ProgramDTO program(long spaceId, boolean open, boolean deleted) {
    ProgramDTO program = new ProgramDTO();
    program.setId(5L);
    program.setSpaceId(spaceId);
    program.setOpen(open);
    program.setDeleted(deleted);
    return program;
  }

}
