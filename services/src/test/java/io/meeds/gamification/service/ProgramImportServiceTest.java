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
import io.meeds.gamification.service.injection.ProgramImportService;
import io.meeds.gamification.service.injection.ProgramTranslationImportService;
import io.meeds.social.translation.service.TranslationService;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProgramImportServiceTest extends BaseExoTestCase {

  @Mock
  private ProgramService                  programService;

  @Mock
  private RuleService                     ruleService;

  @Mock
  private TranslationService              translationService;

  @Mock
  private LocaleConfigService             localeConfigService;

  @Mock
  private FileService                     fileService;

  @Mock
  private SettingService                  settingService;

  @Mock
  private LocaleConfig                    defaultLocaleConfig;

  @Mock
  private ConfigurationManager            configurationManager;

  @Mock
  private UserACL                         userACL;

  @Mock
  private ProgramTranslationImportService programTranslationImportService;

  @InjectMocks
  private ProgramImportService            importService;

  @Before
  public void setup() {
    Map<String, String> settingValues = new HashMap<>();
    doAnswer(invocation -> {
      Context context = invocation.getArgument(0, Context.class);
      Scope scope = invocation.getArgument(1, Scope.class);
      String key = invocation.getArgument(2, String.class);
      String settingKey = context.getId() + context.getName() + scope.getId() + scope.getName() + key;
      settingValues.put(settingKey, "1");
      return null;
    }).when(settingService).set(any(Context.class), any(Scope.class), anyString(), any(SettingValue.class));

    when(settingService.get(any(Context.class), any(Scope.class), anyString())).thenAnswer(invocation -> {
      Context context = invocation.getArgument(0, Context.class);
      Scope scope = invocation.getArgument(1, Scope.class);
      String key = invocation.getArgument(2, String.class);
      String settingKey = context.getId() + context.getName() + scope.getId() + scope.getName() + key;
      String value = settingValues.get(settingKey);
      return value == null ? null : SettingValue.create(value);
    });
  }

  @Test
  public void init() {
    try {
      importService.init();
    } catch (Exception e) {
      fail("Shouldn't stop the container initialization if import default programs fails");
    }

    try {
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
      when(ruleService.findRuleById(anyLong())).thenReturn(ruleDTO);
      importService.importPrograms();

    } catch (Exception e) {
      fail("Shouldn't stop the container initialization if import default programs fails");
    }
  }

}
