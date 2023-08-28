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

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.social.attachment.AttachmentService;
import org.exoplatform.social.attachment.model.FileAttachmentResourceList;
import org.exoplatform.social.attachment.model.ObjectAttachmentList;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.test.AbstractServiceTest;
import io.meeds.gamification.utils.Utils;

public class RuleAttachmentPluginTest extends AbstractServiceTest {

  private final Identity    adminAclIdentity =
                                             new Identity("root1",
                                                          Arrays.asList(new MembershipEntry(Utils.ADMINS_GROUP),
                                                                        new MembershipEntry(Utils.REWARDING_GROUP)));

  private final Identity    userAclIdentity  =
                                            new Identity("root2",
                                                         Arrays.asList(new MembershipEntry("/platform/externals")));

  private AttachmentService attachmentService;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityRegistry.register(adminAclIdentity);
    attachmentService = ExoContainerContext.getService(AttachmentService.class);
  }

  public void testRuleAttachmentPlugin() throws IllegalAccessException, ObjectNotFoundException {
    RuleDTO rule = newRuleDTO();
    assertNotNull(rule);
    long ruleId = rule.getId();

    long adminIdentityId = 1l;
    long userIdentityId = 2l;
    assertThrows(IllegalAccessException.class,
                 () -> attachmentService.saveAttachments(new FileAttachmentResourceList(Collections.emptyList(),
                                                                                        Collections.emptyList(),
                                                                                        userIdentityId,
                                                                                        RuleAttachmentPlugin.RULE_OBJECT_TYPE,
                                                                                        String.valueOf(ruleId),
                                                                                        null),
                                                         userAclIdentity));
    assertThrows(ObjectNotFoundException.class,
                 () -> attachmentService.saveAttachments(new FileAttachmentResourceList(Collections.emptyList(),
                                                                                        Collections.emptyList(),
                                                                                        userIdentityId,
                                                                                        RuleAttachmentPlugin.RULE_OBJECT_TYPE,
                                                                                        String.valueOf(5555l),
                                                                                        null),
                                                         adminAclIdentity));
    assertThrows(IllegalArgumentException.class, // NOSONAR
                 () -> attachmentService.saveAttachments(new FileAttachmentResourceList(Collections.emptyList(),
                                                                                        Collections.emptyList(),
                                                                                        0,
                                                                                        RuleAttachmentPlugin.RULE_OBJECT_TYPE,
                                                                                        String.valueOf(ruleId),
                                                                                        null),
                                                         adminAclIdentity));

    attachmentService.saveAttachments(new FileAttachmentResourceList(Collections.emptyList(),
                                                                     Collections.emptyList(),
                                                                     adminIdentityId,
                                                                     RuleAttachmentPlugin.RULE_OBJECT_TYPE,
                                                                     String.valueOf(ruleId),
                                                                     null),
                                      adminAclIdentity);
    ObjectAttachmentList objectAttachmentList = attachmentService.getAttachments(RuleAttachmentPlugin.RULE_OBJECT_TYPE,
                                                                                 String.valueOf(ruleId),
                                                                                 adminAclIdentity);
    assertNotNull(objectAttachmentList);
    assertNotNull(objectAttachmentList.getAttachments());
    assertEquals(0, objectAttachmentList.getAttachments().size());
    assertEquals(RuleAttachmentPlugin.RULE_OBJECT_TYPE, objectAttachmentList.getObjectType());
    assertEquals(String.valueOf(ruleId), objectAttachmentList.getObjectId());
  }

}
