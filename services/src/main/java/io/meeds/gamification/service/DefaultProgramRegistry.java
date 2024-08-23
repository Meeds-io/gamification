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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.meeds.common.ContainerTransactional;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.*;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.service.TranslationService;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.ResourceBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultProgramRegistry {

  private static final Log                  LOG                  = ExoLogger.getLogger(DefaultProgramRegistry.class);

  @Autowired
  private ProgramService                    programService;

  @Autowired
  private RuleService                       ruleService;

  @Autowired
  private TranslationService                translationService;

  @Autowired
  private LocaleConfigService               localeConfigService;

  @Autowired
  private ResourceBundleService             resourceBundleService;

  @Autowired
  private FileService                       fileService;

  private final List<DefaultRuleConfig>     ruleAvecPrerequisite = new ArrayList<>();

  private final Map<Locale, ResourceBundle> bundles              = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    start();
  }

  @ContainerTransactional
  public void start() {
    ProgramFilter filter = new ProgramFilter();
    filter.setIncludeDeleted(true);
    filter.setStatus(EntityStatusType.ALL);
    int programsCount = programService.countPrograms(filter);
    if (programsCount == 0) {
      loadDefaultPrograms();
    }
  }

  @SneakyThrows
  private void loadDefaultPrograms() {
    ObjectMapper mapper = new ObjectMapper();
    InputStream programInputStream = new ClassPathResource("default-programs.json").getInputStream();
    List<DefaultProgramConfig> programConfigs = mapper.readValue(programInputStream, new TypeReference<>() {
    });

    Enumeration<URL> templateFiles = PortalContainer.getInstance().getPortalClassLoader().getResources("default-rules.json");

    List<DefaultRuleConfig> defaultRuleConfigs = Collections.list(templateFiles)
                                                            .stream()
                                                            .map(this::parseRules)
                                                            .flatMap(List::stream)
                                                            .toList();

    for (DefaultProgramConfig config : programConfigs) {
      ProgramDTO program = new ProgramDTO();
      program.setTitle(getI18NLabel(config.getI18nTitleKey(), Locale.ENGLISH) + " " + config.getAppendEmoji());
      program.setDescription(getProgramDescriptionLabel(config, Locale.ENGLISH));
      program.setEnabled(config.isEnabled());
      if (config.getCoverUrl() != null) {
        program.setCoverFileId(storeProgramCover(config.getCoverUrl()));
      }
      program = programService.createProgram(program);
      saveProgramTranslations(program, config);

      if (!defaultRuleConfigs.isEmpty()) {
        ProgramDTO finalProgram = program;
        defaultRuleConfigs.stream().filter(rule -> rule.getProgramTitle().equals(config.getTitle())).forEach(rule -> {
          RuleDTO ruleDTO = createRule(rule, finalProgram);
          saveRuleTranslations(ruleDTO, rule);
        });
      }
    }
    Locale defaultLocale = localeConfigService.getDefaultLocaleConfig().getLocale();
    if (CollectionUtils.isNotEmpty(ruleAvecPrerequisite)) {
      ruleAvecPrerequisite.forEach(rule -> updateRuleAvecPrerequisite(rule, defaultLocale));
    }
  }

  private void updateRuleAvecPrerequisite(DefaultRuleConfig rule, Locale defaultLocale) {
    RuleDTO ruleDTO = ruleService.findRuleByTitle(getI18NLabel(getI18nKey("title", rule.getEvent()), defaultLocale));
    Set<Long> prerequisiteRuleIds = new HashSet<>();
    rule.getPrerequisiteRules().forEach(item -> {
      RuleDTO prerequisiteRule = ruleService.findRuleByTitle(getI18NLabel(getI18nKey("title", item), defaultLocale));
      if (prerequisiteRule != null) {
        prerequisiteRuleIds.add(prerequisiteRule.getId());
      }
    });
    try {
      ruleDTO.setPrerequisiteRuleIds(prerequisiteRuleIds);
      ruleService.updateRule(ruleDTO);
    } catch (ObjectNotFoundException e) {
      LOG.warn("Error while automatically updating the rule. Rule = {} ", rule, e);
    }
  }

  private RuleDTO createRule(DefaultRuleConfig rule, ProgramDTO finalProgram) {
    RuleDTO ruleDTO = new RuleDTO();
    String i18nTitleKey = getI18nKey("title", rule.getEvent());
    String i18nDescriptionKey = getI18nKey("description", rule.getEvent());
    ruleDTO.setTitle(getI18NLabel(i18nTitleKey, Locale.ENGLISH));
    ruleDTO.setDescription(getI18NLabel(i18nDescriptionKey, Locale.ENGLISH));
    ruleDTO.setType(EntityType.AUTOMATIC);
    ruleDTO.setScore(rule.getScore());
    ruleDTO.setEnabled(true);
    if (rule.getRecurrence() != null) {
      ruleDTO.setRecurrence(rule.getRecurrence());
    }
    if (rule.getPrerequisiteRules() != null) {
      ruleAvecPrerequisite.add(rule);
    }
    EventDTO eventDTO = new EventDTO();
    eventDTO.setType("meeds");
    eventDTO.setTrigger(rule.getEvent());
    eventDTO.setTitle(rule.getEvent());
    ruleDTO.setEvent(eventDTO);
    ruleDTO.setProgram(finalProgram);
    ruleDTO.setDefaultRealizationStatus(rule.getDefaultRealizationStatus());
    return ruleService.createRule(ruleDTO);
  }

  private static String getI18nKey(String type, String name) {
    return "gamification.defaultProgram.rule." + name + "." + type;
  }

  @SneakyThrows
  private void saveProgramTranslations(ProgramDTO program, DefaultProgramConfig config) {
    String i18nTitleKey = config.getI18nTitleKey();
    Map<Locale, String> titleTranslations = new HashMap<>();
    Map<Locale, String> descriptionTranslations = new HashMap<>();

    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(localeConfig -> !StringUtils.equals(localeConfig.getLocale().toLanguageTag(), "ma"))
                       .forEach(localeConfig -> {
                         Locale locale = localeConfig.getLocale();
                         if (config.getAppendEmoji() != null) {
                           titleTranslations.put(locale, getI18NLabel(i18nTitleKey, locale) + " " + config.getAppendEmoji());
                         } else {
                           titleTranslations.put(locale, getI18NLabel(i18nTitleKey, locale));
                         }
                         descriptionTranslations.put(locale, getProgramDescriptionLabel(config, locale));
                       });
    saveTranslation("program", program.getId(), titleTranslations, descriptionTranslations);
  }

  @SneakyThrows
  private void saveRuleTranslations(RuleDTO ruleDTO, DefaultRuleConfig config) {
    String i18nTitleKey = getI18nKey("title", config.getEvent());
    String i18nDescriptionKey = getI18nKey("description", config.getEvent());
    Map<Locale, String> titleTranslations = new HashMap<>();
    Map<Locale, String> descriptionTranslations = new HashMap<>();

    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(localeConfig -> !StringUtils.equals(localeConfig.getLocale().toLanguageTag(), "ma"))
                       .forEach(localeConfig -> {
                         Locale locale = localeConfig.getLocale();
                         if (config.getAppendEmoji() != null) {
                           titleTranslations.put(locale, getI18NLabel(i18nTitleKey, locale) + " " + config.getAppendEmoji());
                         } else {
                           titleTranslations.put(locale, getI18NLabel(i18nTitleKey, locale));
                         }
                         descriptionTranslations.put(locale, getI18NLabel(i18nDescriptionKey, locale));
                       });

    saveTranslation("rule", ruleDTO.getId(), titleTranslations, descriptionTranslations);
  }

  private void saveTranslation(String objectType,
                               Long objectId,
                               Map<Locale, String> titleTranslations,
                               Map<Locale, String> descriptionTranslations) throws ObjectNotFoundException {
    translationService.saveTranslationLabels(objectType, objectId, "title", titleTranslations);
    translationService.saveTranslationLabels(objectType, objectId, "description", descriptionTranslations);
  }

  private String getI18NLabel(String label, Locale locale) {
    try {
      ResourceBundle resourceBundle = getResourceBundle(locale);
      if (resourceBundle != null && resourceBundle.containsKey(label)) {
        return resourceBundle.getString(label);
      }
    } catch (Exception e) {
      LOG.debug("Resource Bundle not found with locale {}", locale, e);
    }
    return label;
  }

  private ResourceBundle getResourceBundle(Locale locale) {
    return bundles.computeIfAbsent(locale,
                                   l -> resourceBundleService.getResourceBundle("locale.addon.Gamification",
                                                                                l,
                                                                                PortalContainer.getInstance()
                                                                                               .getPortalClassLoader()));
  }

  protected List<DefaultRuleConfig> parseRules(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      DefaultRuleConfigList list = Utils.fromJsonString(content, DefaultRuleConfigList.class);
      return list != null ? list.getRules() : Collections.emptyList();
    } catch (IOException e) {
      LOG.warn("An unknown error happened while parsing rules from url {}", url, e);
      return Collections.emptyList();
    }
  }

  private long storeProgramCover(String coverUrl) {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(coverUrl)) {
      if (inputStream == null) {
        return 0;
      }
      FileItem fileItem = new FileItem(null,
                                       coverUrl,
                                       "image/png",
                                       "gamification",
                                       inputStream.available(),
                                       new Date(),
                                       "gamification",
                                       false,
                                       inputStream);

      fileItem = fileService.writeFile(fileItem);
      return fileItem.getFileInfo().getId();
    } catch (Exception e) {
      LOG.warn("Error while writing program {} cover file", coverUrl, e);
      return 0;
    }
  }

  private String getProgramDescriptionLabel(DefaultProgramConfig config, Locale locale) {
    String i18nDescriptionKey = config.getI18nDescriptionKey();
    StringBuilder description = new StringBuilder();
    if (CollectionUtils.isNotEmpty(config.getInstructions())) {
      for (DefaultProgramConfig.Instruction instruction : config.getInstructions()) {
        description.append("<div>")
                   .append(instruction.getEmoji())
                   .append(" ")
                   .append(getI18NLabel(i18nDescriptionKey + instruction.getKey(), locale))
                   .append("</div>\n");
      }
      description.append("<div><br>")
                 .append(getI18NLabel("gamification.defaultProgram.label.seeDocumentation",
                                      locale).replace("$DOCUMENTATION", "https://docs.meeds.io/meeds-guides"))
                 .append("</div>\n");
    }
    return description.toString();
  }
}
