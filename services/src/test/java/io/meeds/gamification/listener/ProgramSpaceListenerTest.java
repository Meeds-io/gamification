/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.listener;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.service.ProgramService;

@RunWith(MockitoJUnitRunner.class)
public class ProgramSpaceListenerTest {

  private static final long SPACE_ID   = 2;

  private static final long PROGRAM_ID = 5l;

  @Mock
  ProgramService            programService;

  @Mock
  Space                     space;

  @Mock
  ProgramDTO                program;

  @Mock
  SpaceLifeCycleEvent       event;

  @Test
  public void testSpaceRemoved() throws ObjectNotFoundException {
    ProgramSpaceListener programSpaceListener = new ProgramSpaceListener(programService);
    when(event.getSpace()).thenReturn(space);
    when(space.getId()).thenReturn(String.valueOf(SPACE_ID));
    when(programService.getProgramIds(argThat(filter -> filter.getSpacesIds()
                                                              .equals(Collections.singletonList(SPACE_ID))
                                                        && filter.isExcludeOpen()),
                                      anyInt(),
                                      anyInt())).thenReturn(Collections.singletonList(PROGRAM_ID));

    when(programService.getProgramById(PROGRAM_ID)).thenReturn(program);
    when(program.getSpaceId()).thenReturn(SPACE_ID);
    programSpaceListener.removePrograms(event);
    verify(program, times(1)).setEnabled(false);
    verify(programService, times(1)).updateProgram(program);
  }

}
