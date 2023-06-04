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
package io.meeds.gamification.scheduled;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

/**
 * Disables automatically Programs without active rules. This should be executed
 * each midnight to check on programs with outdated rules.
 */
@DisallowConcurrentExecution
public class ProgramAutoDisableJob implements Job {

  private static final Log LOG = ExoLogger.getLogger(ProgramAutoDisableJob.class);

  private ProgramService   programService;

  private RuleService      ruleService;

  public ProgramAutoDisableJob() {
    this.programService = ExoContainerContext.getService(ProgramService.class);
    this.ruleService = ExoContainerContext.getService(RuleService.class);
  }

  @Override
  @ExoTransactional
  public void execute(JobExecutionContext context) throws JobExecutionException {
    List<Long> programIds = programService.getProgramIds(new ProgramFilter(true), 0, -1);
    programIds.forEach(this::checkAndChangeProgramStatus);
  }

  public void checkAndChangeProgramStatus(long programId) {
    try {
      ProgramDTO program = programService.getProgramById(programId);
      if (program != null && program.isEnabled() && ruleService.countActiveRules(programId) == 0) {
        program.setEnabled(false);
        programService.updateProgram(program);
        LOG.info("Program {} switched automatically to disabled since no active rules", programId);
      }
    } catch (Exception e) {
      LOG.warn("Error while automatically switching program {} status", programId, e);
    }
  }

}
