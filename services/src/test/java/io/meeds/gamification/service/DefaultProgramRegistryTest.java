/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
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
package io.meeds.gamification.service;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.social.translation.service.TranslationService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultProgramRegistryTest extends BaseExoTestCase {

  @Mock
  private ProgramService         programService;

  @Mock
  private RuleService            ruleService;

  @Mock
  private TranslationService     translationService;

  @Mock
  private LocaleConfigService    localeConfigService;

  @Mock
  private LocaleConfig           defaultLocaleConfig;

  @InjectMocks
  private DefaultProgramRegistry defaultProgramRegistry;

  @Test
  public void testStartWhenNoProgramsExist() {
    ProgramDTO programDTO = new ProgramDTO();
    programDTO.setId(1L);
    programDTO.setTitle("Sample Program");
    when(programService.countPrograms(any(ProgramFilter.class))).thenReturn(0);

    when(programService.createProgram(any(ProgramDTO.class))).thenReturn(programDTO);
    when(localeConfigService.getDefaultLocaleConfig()).thenReturn(defaultLocaleConfig);
    when(defaultLocaleConfig.getLocale()).thenReturn(Locale.ENGLISH);
    LocaleConfig otherLocaleConfig = mock(LocaleConfig.class);
    when(otherLocaleConfig.getLocale()).thenReturn(Locale.ENGLISH);
    when(localeConfigService.getLocalConfigs()).thenReturn(Arrays.asList(defaultLocaleConfig, otherLocaleConfig));

    RuleDTO ruleDTO = new RuleDTO();

    ruleDTO.setId(1L);
    ruleDTO.setTitle("Sample Rule");
    when(ruleService.createRule(any(RuleDTO.class))).thenReturn(ruleDTO);
    when(ruleService.findRuleByTitle(anyString())).thenReturn(ruleDTO);

    defaultProgramRegistry.start();

    verify(programService, times(1)).countPrograms(any(ProgramFilter.class));
    verify(programService, times(1)).createProgram(any(ProgramDTO.class));
    verify(ruleService, atLeastOnce()).createRule(any(RuleDTO.class));
  }

  @Test
  public void testStartWhenProgramsExist() {

    when(programService.countPrograms(any(ProgramFilter.class))).thenReturn(1);

    // When
    defaultProgramRegistry.start();

    // Then
    verify(programService, times(1)).countPrograms(any(ProgramFilter.class));
    verify(programService, never()).createProgram(any(ProgramDTO.class));
    verify(ruleService, never()).createRule(any(RuleDTO.class));
  }

}
