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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class ProgramAutoDisableListenerTest extends AbstractServiceTest {

  public void testDisablePrograms() throws Exception {
    ProgramDTO program = newProgram();
    RuleDTO rule = newRuleDTO("rule1", program.getId());
    assertEquals(1, ruleService.countActiveRules(program.getId()));
    assertTrue(program.isEnabled());

    rule.setEndDate(Utils.toSimpleDateFormat(Date.from(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))));
    ruleService.updateRule(rule);

    assertEquals(0, ruleService.countActiveRules(program.getId()));
    program = programService.getProgramById(program.getId());
    assertFalse(program.isEnabled());
  }

}
