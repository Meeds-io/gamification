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
package io.meeds.gamification.plugin;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import io.meeds.gamification.model.ProgramDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;
import io.meeds.social.translation.model.TranslationField;
import io.meeds.social.translation.service.TranslationService;

public class ProgramTranslationPluginTest extends AbstractServiceTest {
  private final Identity     adminAclIdentity =
                                              new Identity("root1",
                                                           Arrays.asList(new MembershipEntry(Utils.ADMINS_GROUP)));

  private TranslationService translationService;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityRegistry.register(adminAclIdentity);
    translationService = ExoContainerContext.getService(TranslationService.class);
  }

  public void testManageTranslations() throws IllegalAccessException, ObjectNotFoundException {
    ProgramDTO program = newProgram();
    assertNotNull(program);

    long programId = program.getId();
    assertThrows(IllegalAccessException.class,
                 () -> translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                programId,
                                                                "title",
                                                                Collections.singletonMap(Locale.ENGLISH, "label"),
                                                                "root2"));
    assertThrows(ObjectNotFoundException.class, // NOSONAR
                 () -> translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                5555l,
                                                                "title",
                                                                Collections.singletonMap(Locale.ENGLISH, "label"),
                                                                adminAclIdentity.getUserId()));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                0,
                                                                "title",
                                                                Collections.singletonMap(Locale.ENGLISH, "label"),
                                                                "root2"));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                programId,
                                                                null,
                                                                Collections.singletonMap(Locale.ENGLISH, "label"),
                                                                "root2"));
    assertThrows(IllegalAccessException.class, // NOSONAR
                 () -> translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                programId,
                                                                "title",
                                                                Collections.singletonMap(Locale.ENGLISH, "label"),
                                                                null));

    translationService.saveTranslationLabels(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                             programId,
                                             "title",
                                             Collections.singletonMap(Locale.ENGLISH, "label"),
                                             adminAclIdentity.getUserId());
    TranslationField translationField = translationService.getTranslationField(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE,
                                                                               programId,
                                                                               "title",
                                                                               adminAclIdentity.getUserId());
    assertNotNull(translationField);
    assertEquals(programId, translationField.getObjectId());
    assertEquals(ProgramTranslationPlugin.PROGRAM_OBJECT_TYPE, translationField.getObjectType());
    assertEquals(translationField.getLabels().get(Locale.ENGLISH), "label");
  }

}
