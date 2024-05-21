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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.quartz.JobExecutionException;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.gamification.entity.ProgramEntity;
import io.meeds.gamification.entity.RuleEntity;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class ProgramAutoDisableJobTest extends AbstractServiceTest {

  public void testDisablePrograms() throws JobExecutionException, ObjectNotFoundException {
    ProgramEntity domain1 = newDomain();
    RuleEntity rule1 = newRule("rule1", domain1.getId());
    rule1.setEndDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
    rule1 = ruleDAO.update(rule1);
    assertEquals(0, ruleService.countActiveRules(domain1.getId()));

    ProgramEntity domain2 = newDomain();
    RuleEntity rule2 = newRule("rule2", domain2.getId());
    assertEquals(1, ruleService.countActiveRules(domain2.getId()));

    ProgramAutoDisableJob programAutoDisableJob = new ProgramAutoDisableJob();
    programAutoDisableJob.execute(null);
    assertEquals(0, ruleService.countActiveRules(domain1.getId()));
    assertEquals(1, ruleService.countActiveRules(domain2.getId()));
    domain1 = programDAO.find(domain1.getId());
    domain2 = programDAO.find(domain2.getId());
    assertFalse(domain1.isEnabled());
    assertTrue(domain2.isEnabled());

    RuleDTO ruleDto2 = ruleService.findRuleById(rule2.getId());
    ruleDto2.setEndDate(Utils.toSimpleDateFormat(Date.from(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))));
    ruleService.updateRule(ruleDto2);

    programAutoDisableJob.execute(null);

    assertEquals(0, ruleService.countActiveRules(domain1.getId()));
    assertEquals(0, ruleService.countActiveRules(domain2.getId()));
    domain1 = programDAO.find(domain1.getId());
    domain2 = programDAO.find(domain2.getId());
    assertFalse(domain1.isEnabled());
    assertFalse(domain2.isEnabled());
  }

}
