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

import io.meeds.gamification.model.Announcement;
import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.RuleService;

/**
 * Pins which space a quest announcement belongs to for the space scope
 * decision.
 */
public class AnnouncementSpaceScopeResolverTest {

  private AnnouncementService            announcementService;

  private RuleService                    ruleService;

  private AnnouncementSpaceScopeResolver resolver;

  /**
   * Creates the resolver over mocked services.
   */
  @Before
  public void setUp() {
    announcementService = mock(AnnouncementService.class);
    ruleService = mock(RuleService.class);
    resolver = new AnnouncementSpaceScopeResolver(announcementService, ruleService);
  }

  /**
   * An announcement of a quest of a space campaign belongs to that campaign's
   * space, and the resolver answers to the name the tool definitions give.
   * Mutants: the quest not followed to its campaign; the name changed.
   */
  @Test
  public void announcementOfAQuestOfASpaceCampaignBelongsToItsSpace() {
    when(announcementService.getAnnouncementById(3L)).thenReturn(announcement(7L));
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, false, false), false));

    assertEquals("announcement", resolver.getName());
    assertEquals(Long.valueOf(12L), resolver.spaceOf("3"));
  }

  /**
   * An announcement of a quest of an open campaign belongs to no space.
   * Mutant: the open check removed.
   */
  @Test
  public void announcementOfAQuestOfAnOpenCampaignBelongsToNoSpace() {
    when(announcementService.getAnnouncementById(3L)).thenReturn(announcement(7L));
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, true, false), false));

    assertNull(resolver.spaceOf("3"));
  }

  /**
   * An announcement of a quest of a campaign of no space, of a campaign with
   * none, or of a deleted quest belongs to no space.
   */
  @Test
  public void announcementOfAQuestOfNoSpaceOrDeletedBelongsToNoSpace() {
    when(announcementService.getAnnouncementById(3L)).thenReturn(announcement(7L));
    when(ruleService.findRuleById(7L)).thenReturn(rule(program(0L, false, false), false));
    assertNull(resolver.spaceOf("3"));

    when(ruleService.findRuleById(7L)).thenReturn(rule(null, false));
    assertNull(resolver.spaceOf("3"));

    when(ruleService.findRuleById(7L)).thenReturn(rule(program(12L, false, false), true));
    assertNull(resolver.spaceOf("3"));
  }

  /**
   * A missing announcement, an announcement of no quest and an announcement
   * of a missing quest belong to no space.
   */
  @Test
  public void missingAnnouncementOrQuestBelongsToNoSpace() {
    when(announcementService.getAnnouncementById(3L)).thenReturn(null);
    assertNull(resolver.spaceOf("3"));

    when(announcementService.getAnnouncementById(3L)).thenReturn(announcement(null));
    assertNull(resolver.spaceOf("3"));

    when(announcementService.getAnnouncementById(3L)).thenReturn(announcement(7L));
    when(ruleService.findRuleById(7L)).thenReturn(null);
    assertNull(resolver.spaceOf("3"));
  }

  /**
   * An id that isn't a number is never looked up. Mutant: the numeric check
   * removed.
   */
  @Test
  public void nonNumericIdIsNotLookedUp() {
    assertNull(resolver.spaceOf("abc"));
    assertNull(resolver.spaceOf("3 OR 1=1"));
    assertNull(resolver.spaceOf("-3"));
    assertNull(resolver.spaceOf(""));
    assertNull(resolver.spaceOf(null));

    verify(announcementService, never()).getAnnouncementById(anyLong());
  }

  /**
   * A lookup that throws, in either service, reads as no space. Mutant: the
   * catch removed.
   */
  @Test
  public void failingLookupBelongsToNoSpace() {
    when(announcementService.getAnnouncementById(3L)).thenThrow(new IllegalStateException("boom"));
    assertNull(resolver.spaceOf("3"));

    when(announcementService.getAnnouncementById(4L)).thenReturn(announcement(7L));
    when(ruleService.findRuleById(7L)).thenThrow(new IllegalStateException("boom"));
    assertNull(resolver.spaceOf("4"));
  }

  /**
   * Test fixture builder.
   */
  private static Announcement announcement(Long challengeId) {
    Announcement announcement = new Announcement();
    announcement.setChallengeId(challengeId);
    return announcement;
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
