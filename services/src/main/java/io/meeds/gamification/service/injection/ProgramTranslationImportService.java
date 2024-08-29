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
package io.meeds.gamification.service.injection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.meeds.gamification.model.ProgramDescriptor;
import io.meeds.gamification.plugin.ProgramTranslationPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.ResourceBundleService;

import io.meeds.common.ContainerTransactional;
import io.meeds.social.translation.service.TranslationService;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;

@Component
public class ProgramTranslationImportService {

  private static final Log                  LOG                  = ExoLogger.getLogger(ProgramTranslationImportService.class);

  @Autowired
  private TranslationService                translationService;

  @Autowired
  private LocaleConfigService               localeConfigService;

  @Autowired
  private ResourceBundleService             resourceBundleService;

  private final Map<String, List<Runnable>> postImportProcessors = new ConcurrentHashMap<>();

  private final Map<Locale, ResourceBundle> bundles              = new ConcurrentHashMap<>();

  private final ExecutorService             executorService      = Executors.newFixedThreadPool(1);

  @PreDestroy
  protected void destroy() {
    executorService.shutdownNow();
  }

  public void saveProgramDescriptionTranslationLabels(long programId, List<ProgramDescriptor.Description> descriptions) {
    postImportProcessors.computeIfAbsent(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE, k -> new ArrayList<>())
                        .add(() -> saveProgramDescriptionTranslationLabelsForAllLanguages(programId, descriptions));
  }

  public void saveTranslationLabels(String objectType,
                                    long objectId,
                                    String fieldName,
                                    Map<String, String> labels,
                                    String appendEmoji) {
    // Make Heavy processing made at the end or import process
    postImportProcessors.computeIfAbsent(objectType, k -> new ArrayList<>())
                        .add(() -> saveTranslationLabelsForAllLanguages(objectType,
                                                                        objectId,
                                                                        fieldName,
                                                                        labels,
                                                                        appendEmoji,
                                                                        getI18NLabel(labels.get("en"), Locale.ENGLISH)));
  }

  public void postImport(String objectType) {
    postImportProcessors.computeIfAbsent(objectType, k -> new ArrayList<>()).forEach(executorService::execute);
    postImportProcessors.remove(objectType);
    bundles.clear();
  }

  public String getI18NLabel(String label, Locale locale) {
    return getI18NLabel(label, locale, null);
  }

  public String getProgramDescriptionLabel(List<ProgramDescriptor.Description> descriptions, Locale locale) {
    StringBuilder stringBuilder = new StringBuilder();
    if (CollectionUtils.isNotEmpty(descriptions)) {
      for (ProgramDescriptor.Description description : descriptions) {
        String i18nKey = description.getInstruction().get("en");
        stringBuilder.append("<div>")
                     .append(description.getEmoji())
                     .append(" ")
                     .append(getI18NLabel(i18nKey, locale))
                     .append("</div>\n");
      }
      stringBuilder.append("<div><br>")
                   .append(getI18NLabel("gamification.defaultProgram.label.seeDocumentation",
                                        locale).replace("$DOCUMENTATION", "https://docs.meeds.io/meeds-guides"))
                   .append("</div>\n");
    }
    return stringBuilder.toString();
  }

  @SneakyThrows
  @ContainerTransactional
  private void saveProgramDescriptionTranslationLabelsForAllLanguages(long objectId,
                                                                      List<ProgramDescriptor.Description> descriptions) {
    Map<Locale, String> translations = new HashMap<>();
    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(config -> !StringUtils.equals(config.getLocale().toLanguageTag(), "ma"))
                       .forEach(config -> translations.put(config.getLocale(),
                                                           getProgramDescriptionLabel(descriptions, config.getLocale())));
    translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                             objectId,
                                             ProgramTranslationPlugin.PROGRAM_DESCRIPTION_FIELD_NAME,
                                             translations);
  }

  @SneakyThrows
  @ContainerTransactional
  private void saveTranslationLabelsForAllLanguages(String objectType,
                                                    long objectId,
                                                    String fieldName,
                                                    Map<String, String> labels,
                                                    String appendEmoji,
                                                    String defaultLabel) {
    String i18nKey = labels.get("en");
    Map<Locale, String> translations = new HashMap<>();
    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(config -> !StringUtils.equals(config.getLocale().toLanguageTag(), "ma"))
                       .forEach(config -> translations.put(config.getLocale(),
                                                           appendEmoji != null ? getI18NLabel(i18nKey,
                                                                                              config.getLocale(),
                                                                                              defaultLabel)
                                                               + " " + appendEmoji
                                                                               : getI18NLabel(i18nKey,
                                                                                              config.getLocale(),
                                                                                              defaultLabel)));
    translationService.saveTranslationLabels(objectType, objectId, fieldName, translations);
  }

  private String getI18NLabel(String label, Locale locale, String defaultLabel) {
    try {
      ResourceBundle resourceBundle = getResourceBundle(locale);
      if (resourceBundle != null && resourceBundle.containsKey(label)) {
        return resourceBundle.getString(label);
      }
    } catch (Exception e) {
      LOG.debug("Resource Bundle not found with locale {}", locale, e);
    }
    return defaultLabel == null ? label : defaultLabel;
  }

  private ResourceBundle getResourceBundle(Locale locale) {
    return bundles.computeIfAbsent(locale,
                                   l -> resourceBundleService.getResourceBundle("locale.addon.Gamification",
                                                                                l,
                                                                                PortalContainer.getInstance()
                                                                                               .getPortalClassLoader()));
  }
}
