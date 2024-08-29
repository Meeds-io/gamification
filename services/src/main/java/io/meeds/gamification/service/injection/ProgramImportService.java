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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.constant.EntityType;
import io.meeds.gamification.model.*;
import io.meeds.gamification.model.filter.ProgramFilter;
import io.meeds.gamification.plugin.ProgramTranslationPlugin;
import io.meeds.gamification.plugin.RuleTranslationPlugin;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;
import io.meeds.gamification.utils.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.portal.config.UserACL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import io.meeds.common.ContainerTransactional;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;

import static io.meeds.gamification.utils.Utils.getUserAclIdentity;

@Component
public class ProgramImportService {

  private static final String             DEFAULT_RULE_IMPORT          = "DEFAULT_RULE_IMPORT";

  private static final Scope              DEFAULT_RULE_IMPORT_SCOPE    = Scope.APPLICATION.id(DEFAULT_RULE_IMPORT);

  private static final Scope              DEFAULT_PROGRAM_IMPORT_SCOPE = Scope.APPLICATION.id("DEFAULT_PROGRAM_IMPORT_SCOPE");

  private static final Context            DEFAULT_PROGRAM_CONTEXT      = Context.GLOBAL.id("DEFAULT_PROGRAM");

  private static final Log                LOG                          = ExoLogger.getLogger(ProgramImportService.class);

  @Autowired
  private ProgramTranslationImportService programTranslationImportService;

  @Autowired
  private FileService                     fileService;

  @Autowired
  private ProgramService                  programService;

  @Autowired
  private RuleService                     ruleService;

  @Autowired
  private SettingService                  settingService;

  @Autowired
  private ConfigurationManager            configurationManager;

  @Autowired
  private UserACL                         userAcl;

  @Value("${meeds.programs.import.override:false}")
  private boolean                         forceReimport;

  private final List<RuleDescriptor>      rulesWithPrerequisite        = new ArrayList<>();

  @PostConstruct
  public void init() {
    CompletableFuture.runAsync(this::importPrograms);
  }

  @ContainerTransactional
  public void importPrograms() {
    LOG.info("Importing Default Programs");
    ConversationState.setCurrent(new ConversationState(getUserAclIdentity(userAcl.getSuperUser())));
    try {
      ProgramFilter filter = new ProgramFilter();
      filter.setIncludeDeleted(true);
      filter.setStatus(EntityStatusType.ALL);
      int programsCount = programService.countPrograms(filter);
      if (forceReimport || programsCount == 0) {
        Collections.list(getClassLoader().getResources("default-programs.json"))
                   .stream()
                   .map(this::parseProgramDescriptors)
                   .flatMap(List::stream)
                   .forEach(this::importProgramDescriptor);
        Collections.list(getClass().getClassLoader().getResources("default-rules.json"))
                   .stream()
                   .map(this::parseRuleDescriptors)
                   .flatMap(List::stream)
                   .forEach(this::importRuleDescriptor);

        if (CollectionUtils.isNotEmpty(rulesWithPrerequisite)) {
          rulesWithPrerequisite.forEach(this::updateRuleWithPrerequisite);
        }
        LOG.info("Importing Default Programs finished successfully.");

        programTranslationImportService.postImport(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE);
        programTranslationImportService.postImport(RuleTranslationPlugin.RULE_OBJECT_TYPE);
      }
    } catch (Exception e) {
      LOG.warn("An error occurred while importing default programs", e);
    } finally {
      ConversationState.setCurrent(null);
    }
  }

  protected List<RuleDescriptor> parseRuleDescriptors(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      RuleDescriptorList list = Utils.fromJsonString(content, RuleDescriptorList.class);
      return list != null ? list.getDescriptors() : Collections.emptyList();
    } catch (IOException e) {
      LOG.warn("An unknown error happened while parsing default rules from url {}", url, e);
      return Collections.emptyList();
    }
  }

  protected List<ProgramDescriptor> parseProgramDescriptors(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      ProgramDescriptorList list = Utils.fromJsonString(content, ProgramDescriptorList.class);
      return list != null ? list.getDescriptors() : Collections.emptyList();
    } catch (IOException e) {
      LOG.warn("An unknown error happened while parsing default programs from url {}", url, e);
      return Collections.emptyList();
    }
  }

  protected void importProgramDescriptor(ProgramDescriptor descriptor) {
    String descriptorId = descriptor.getNameId();
    long existingId = getProgramSettingValue(descriptorId);
    if (forceReimport || existingId == 0) {
      importProgram(descriptor, existingId);
    } else {
      LOG.debug("Ignore re-importing default program {}", descriptorId);
    }
  }

  protected void importRuleDescriptor(RuleDescriptor descriptor) {
    String descriptorId = descriptor.getNameId();
    long existingId = getSettingValue(descriptorId);
    if (existingId == 0) {
      importRule(descriptor, existingId);
    } else {
      LOG.debug("Ignore re-importing Rule {}", descriptorId);
    }
  }

  protected void importProgram(ProgramDescriptor d, long oldId) {
    String descriptorId = d.getNameId();
    try {
      ProgramDTO programDTO = saveProgram(d, oldId);
      if (programDTO != null && (forceReimport || oldId == 0 || programDTO.getId() != oldId)) {
        saveProgramNames(d, programDTO);
        saveProgramDescriptions(d, programDTO);
        // Mark as imported
        setProgramSettingValue(descriptorId, programDTO.getId());
      }
    } catch (Exception e) {
      LOG.warn("An error occurred while importing default program {}", descriptorId, e);
    }
  }

  protected void importRule(RuleDescriptor d, long oldId) {
    String descriptorId = d.getNameId();
    try {
      RuleDTO rule = saveRule(d, oldId);
      if (rule == null) {
        return;
      }
      if (forceReimport || oldId == 0 || rule.getId() != oldId) {
        saveNames(d, rule);
        saveDescriptions(d, rule);
        // Mark as imported
        setSettingValue(descriptorId, rule.getId());
      }
    } catch (Exception e) {
      LOG.warn("An error occurred while importing default rule {}", descriptorId, e);
    }
  }

  protected void saveNames(RuleDescriptor d, RuleDTO ruleDTO) {
    programTranslationImportService.saveTranslationLabels(RuleTranslationPlugin.RULE_OBJECT_TYPE,
                                                          ruleDTO.getId(),
                                                          RuleTranslationPlugin.RULE_TITLE_FIELD_NAME,
                                                          d.getNames(),
                                                          d.getAppendEmoji());
  }

  protected void saveDescriptions(RuleDescriptor d, RuleDTO ruleDTO) {
    programTranslationImportService.saveTranslationLabels(RuleTranslationPlugin.RULE_OBJECT_TYPE,
                                                          ruleDTO.getId(),
                                                          RuleTranslationPlugin.RULE_DESCRIPTION_FIELD_NAME,
                                                          d.getDescriptions(),
                                                          null);
  }

  protected void saveProgramNames(ProgramDescriptor d, ProgramDTO programDTO) {
    programTranslationImportService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                          programDTO.getId(),
                                                          ProgramTranslationPlugin.PROGRAM_TITLE_FIELD_NAME,
                                                          d.getNames(),
                                                          d.getAppendEmoji());
  }

  protected void saveProgramDescriptions(ProgramDescriptor d, ProgramDTO programDTO) {
    programTranslationImportService.saveProgramDescriptionTranslationLabels(programDTO.getId(), d.getDescriptions());
  }

  @SneakyThrows
  protected ProgramDTO saveProgram(ProgramDescriptor d, long oldId) {
    ProgramDTO program = null;
    if (oldId > 0) {
      program = programService.getProgramById(oldId);
    }
    boolean isNew = program == null;
    if (isNew) {
      program = new ProgramDTO();
    }
    program.setTitle(d.getAppendEmoji() != null ? programTranslationImportService.getI18NLabel(d.getNames().get("en"),
                                                                                               Locale.ENGLISH)
        + " " + d.getAppendEmoji() : programTranslationImportService.getI18NLabel(d.getNames().get("en"), Locale.ENGLISH));
    program.setDescription(programTranslationImportService.getProgramDescriptionLabel(d.getDescriptions(), Locale.ENGLISH));
    program.setEnabled(d.isEnabled());
    if (d.getCoverUrl() != null) {
      program.setCoverFileId(storeProgramCover(d.getCoverUrl()));
    }
    if (isNew) {
      return programService.createProgram(program);
    } else {
      return programService.updateProgram(program);
    }
  }

  @SneakyThrows
  protected RuleDTO saveRule(RuleDescriptor d, long oldId) {
    RuleDTO rule = null;
    if (oldId > 0) {
      rule = ruleService.findRuleById(oldId);
    }
    boolean isNew = rule == null;
    if (isNew) {
      rule = new RuleDTO();
    }
    rule.setTitle(d.getAppendEmoji() != null ? programTranslationImportService.getI18NLabel(d.getNames().get("en"),
                                                                                            Locale.ENGLISH)
        + " " + d.getAppendEmoji() : programTranslationImportService.getI18NLabel(d.getNames().get("en"), Locale.ENGLISH));
    rule.setDescription(programTranslationImportService.getI18NLabel(d.getDescriptions().get("en"), Locale.ENGLISH));
    rule.setType(EntityType.AUTOMATIC);
    rule.setScore(d.getScore());
    rule.setEnabled(true);
    if (d.getRecurrence() != null) {
      rule.setRecurrence(d.getRecurrence());
    }
    EventDTO eventDTO = new EventDTO();
    eventDTO.setType("meeds");
    eventDTO.setTrigger(d.getEvent());
    eventDTO.setTitle(d.getEvent());
    rule.setEvent(eventDTO);
    if (getProgramSettingValue(d.getProgramNameId()) > 0) {
      ProgramDTO program = programService.getProgramById(getProgramSettingValue(d.getProgramNameId()));
      rule.setProgram(program);
    }
    rule.setDefaultRealizationStatus(d.getDefaultRealizationStatus());
    if (d.getPrerequisiteRules() != null) {
      rulesWithPrerequisite.add(d);
    }
    if (isNew) {
      return ruleService.createRule(rule);
    } else {
      return ruleService.updateRule(rule);
    }
  }

  protected void updateRuleWithPrerequisite(RuleDescriptor rule) {
    long ruleId = getSettingValue(rule.getNameId());
    RuleDTO ruleDTO = ruleService.findRuleById(ruleId);
    if (ruleDTO != null) {
      Set<Long> prerequisiteRuleIds = new HashSet<>();
      rule.getPrerequisiteRules().forEach(item -> {
        long prerequisiteRuleId = getSettingValue(item);
        if (prerequisiteRuleId > 0) {
          prerequisiteRuleIds.add(prerequisiteRuleId);
        }
      });
      try {
        ruleDTO.setPrerequisiteRuleIds(prerequisiteRuleIds);
        ruleService.updateRule(ruleDTO);
      } catch (ObjectNotFoundException e) {
        LOG.warn("Error while automatically updating the rule. Rule = {} ", rule, e);
      }
    }
  }

  protected void setProgramSettingValue(String name, long value) {
    settingService.set(DEFAULT_PROGRAM_CONTEXT, DEFAULT_PROGRAM_IMPORT_SCOPE, name, SettingValue.create(String.valueOf(value)));
  }

  protected long getProgramSettingValue(String name) {
    try {
      SettingValue<?> settingValue = settingService.get(DEFAULT_PROGRAM_CONTEXT, DEFAULT_PROGRAM_IMPORT_SCOPE, name);
      return settingValue == null || settingValue.getValue() == null ? 0L : Long.parseLong(settingValue.getValue().toString());
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  protected void setSettingValue(String name, long value) {
    settingService.set(DEFAULT_PROGRAM_CONTEXT, DEFAULT_RULE_IMPORT_SCOPE, name, SettingValue.create(String.valueOf(value)));
  }

  protected long getSettingValue(String name) {
    try {
      SettingValue<?> settingValue = settingService.get(DEFAULT_PROGRAM_CONTEXT, DEFAULT_RULE_IMPORT_SCOPE, name);
      return settingValue == null || settingValue.getValue() == null ? 0L : Long.parseLong(settingValue.getValue().toString());
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  private ClassLoader getClassLoader() {
    return getClass().getClassLoader();
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
      return fileItem != null ? fileItem.getFileInfo().getId() : 0;
    } catch (Exception e) {
      LOG.warn("Error while writing program {} cover file", coverUrl, e);
      return 0;
    }
  }

}
