/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package io.meeds.gamification.listener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.service.ProgramService;

public class ProgramSpaceListener extends SpaceListenerPlugin {

  private static final Log LOG = ExoLogger.getLogger(ProgramSpaceListener.class);

  protected ProgramService programService;

  public ProgramSpaceListener(ProgramService programService) {
    this.programService = programService;
  }

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent event) {
    CompletableFuture.runAsync(() -> removePrograms(event));
  }

  @Override
  public void spaceRegistrationEdited(SpaceLifeCycleEvent event) {
    CompletableFuture.runAsync(() -> updateProgramsVisibility(event));
  }

  @Override
  public void spaceAccessEdited(SpaceLifeCycleEvent event) {
    CompletableFuture.runAsync(() -> updateProgramsVisibility(event));
  }

  @ExoTransactional
  public void updateProgramsVisibility(SpaceLifeCycleEvent event) {
    String spaceId = event.getSpace().getId();
    List<Long> programIds = getSpaceProgramIds(spaceId);
    programIds.forEach(programId -> {
      try {
        ProgramDTO program = programService.getProgramById(programId);
        // Force update visibility computing by updating the program
        programService.updateProgram(program);
      } catch (Exception e) {
        LOG.warn("Error updating program with id {} while its space registration with id {} had changed",
                 programId,
                 spaceId,
                 e);
      }
    });
  }

  @ExoTransactional
  public void removePrograms(SpaceLifeCycleEvent event) {
    String spaceId = event.getSpace().getId();
    List<Long> programIds = getSpaceProgramIds(spaceId);
    programIds.forEach(programId -> {
      try {
        ProgramDTO program = programService.getProgramById(programId);
        program.setEnabled(false);
        programService.updateProgram(program);
      } catch (Exception e) {
        LOG.warn("Error disabling program with id {} while its space with id {} has been deleted",
                 programId,
                 spaceId,
                 e);
      }
    });
  }

  private List<Long> getSpaceProgramIds(String spaceId) {
    ProgramFilter spaceProgramsFilter = new ProgramFilter(true);
    spaceProgramsFilter.setSpacesIds(Collections.singletonList(Long.parseLong(spaceId)));
    return programService.getProgramIds(spaceProgramsFilter, 0, -1);
  }

}
