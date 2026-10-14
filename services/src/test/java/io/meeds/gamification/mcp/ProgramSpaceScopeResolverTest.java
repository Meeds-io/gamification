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
import io.meeds.gamification.service.ProgramService;

/**
 * Pins which space a campaign belongs to for the space scope decision.
 */
public class ProgramSpaceScopeResolverTest {

  private ProgramService            programService;

  private ProgramSpaceScopeResolver resolver;

  /**
   * Creates the resolver over a mocked service.
   */
  @Before
  public void setUp() {
    programService = mock(ProgramService.class);
    resolver = new ProgramSpaceScopeResolver(programService);
  }

  /**
   * A space campaign (a space, not open) belongs to that space, and the
   * resolver answers to the name the tool definitions give. Mutants: the
   * space id not read; the name changed.
   */
  @Test
  public void spaceCampaignBelongsToItsSpace() {
    when(programService.getProgramById(5L)).thenReturn(program(12L, false, false));

    assertEquals("campaign", resolver.getName());
    assertEquals(Long.valueOf(12L), resolver.spaceOf("5"));
  }

  /**
   * An open campaign gives no space even when it carries one: its audience is
   * everyone. Mutant: the open check removed.
   * Unreachable through storage today (ProgramMapper keeps no space on an open
   * program); pins the guard should that change.
   */
  @Test
  public void openCampaignBelongsToNoSpace() {
    when(programService.getProgramById(5L)).thenReturn(program(12L, true, false));

    assertNull(resolver.spaceOf("5"));
  }

  /**
   * A campaign of no space belongs to none. Mutant: the {@code spaceId > 0}
   * check removed.
   */
  @Test
  public void campaignOfNoSpaceBelongsToNone() {
    when(programService.getProgramById(5L)).thenReturn(program(0L, false, false));

    assertNull(resolver.spaceOf("5"));
  }

  /**
   * A deleted campaign belongs to no space. Mutant: the deleted check
   * removed.
   */
  @Test
  public void deletedCampaignBelongsToNoSpace() {
    when(programService.getProgramById(5L)).thenReturn(program(12L, false, true));

    assertNull(resolver.spaceOf("5"));
  }

  /**
   * A missing campaign belongs to no space.
   */
  @Test
  public void missingCampaignBelongsToNoSpace() {
    when(programService.getProgramById(5L)).thenReturn(null);

    assertNull(resolver.spaceOf("5"));
  }

  /**
   * An id that isn't a number is never looked up. Mutant: the numeric check
   * removed.
   */
  @Test
  public void nonNumericIdIsNotLookedUp() {
    assertNull(resolver.spaceOf("abc"));
    assertNull(resolver.spaceOf("5; drop"));
    assertNull(resolver.spaceOf("-5"));
    assertNull(resolver.spaceOf(""));
    assertNull(resolver.spaceOf(null));
    assertNull(resolver.spaceOf("99999999999999999999"));

    verify(programService, never()).getProgramById(anyLong());
  }

  /**
   * A lookup that throws reads as no space. Mutant: the catch removed.
   */
  @Test
  public void failingLookupBelongsToNoSpace() {
    when(programService.getProgramById(5L)).thenThrow(new IllegalStateException("boom"));

    assertNull(resolver.spaceOf("5"));
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
