/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
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
package io.meeds.gamification.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

import io.meeds.gamification.constant.DateFilterType;
import io.meeds.gamification.constant.EntityStatusType;
import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.filter.RuleFilter;
import io.meeds.gamification.service.RuleService;
import io.meeds.social.cms.model.ContentLinkExtension;
import io.meeds.social.cms.model.ContentLinkSearchResult;
import io.meeds.social.cms.service.ContentLinkPluginService;
import io.meeds.social.translation.service.TranslationService;

import lombok.SneakyThrows;

@RunWith(MockitoJUnitRunner.class)
public class RuleContentLinkPluginTest {

  private static final String      RULE_TITLE = "ruleTitle";

  private static final Long        RULE_ID    = 55l;

  private static final String      TEST_USER  = "testUser";

  private static final String      QUERY      = "query";

  @Mock
  private ContentLinkPluginService contentLinkPluginService;

  @Mock
  private RuleService              ruleService;

  @Mock
  private UserACL                  userAcl;

  @Mock
  private IdentityManager          identityManager;

  @Mock
  private TranslationService       translationService;

  @Mock
  private Identity                 identity;

  @Mock
  private RuleDTO                  rule;

  @InjectMocks
  private RuleContentLinkPlugin    plugin;

  @Test
  public void getExtension() {
    ContentLinkExtension extension = plugin.getExtension();
    assertNotNull(extension);
    assertEquals("rule", extension.getObjectType());
    assertEquals("action", extension.getCommand());
    assertEquals("fa fa-trophy", extension.getIcon());
    assertEquals("contentLink.action", extension.getTitleKey());
  }

  @Test
  public void searchWhenEmpty() {
    when(userAcl.isAnonymousUser((Identity) null)).thenReturn(true);

    List<ContentLinkSearchResult> results = plugin.search(QUERY, null, null, 0, 10);
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  @SneakyThrows
  public void searchWithQuery() {
    RuleFilter filter = new RuleFilter();
    filter.setTerm(QUERY);
    filter.setIdentityId(2l);
    filter.setLocale(Locale.ENGLISH);
    filter.setDateFilterType(DateFilterType.ALL);
    filter.setProgramStatus(EntityStatusType.ENABLED);
    filter.setStatus(EntityStatusType.ENABLED);

    when(identity.getUserId()).thenReturn(TEST_USER);
    org.exoplatform.social.core.identity.model.Identity userIdentity =
                                                                     mock(org.exoplatform.social.core.identity.model.Identity.class);
    when(identityManager.getOrCreateUserIdentity(TEST_USER)).thenReturn(userIdentity);
    when(userIdentity.getId()).thenReturn("2");
    when(rule.getId()).thenReturn(RULE_ID);
    when(rule.getTitle()).thenReturn(RULE_TITLE);

    List<ContentLinkSearchResult> results = plugin.search(QUERY, identity, Locale.ENGLISH, 2, 10);
    assertNotNull(results);
    assertTrue(results.isEmpty());

    when(ruleService.getRules(filter, 2, 10)).thenReturn(Collections.singletonList(rule));
    results = plugin.search(QUERY, identity, Locale.ENGLISH, 2, 10);
    assertNotNull(results);
    assertEquals(1, results.size());
    ContentLinkSearchResult contentLinkSearchResult = results.get(0);
    assertEquals(plugin.getExtension().getObjectType(), contentLinkSearchResult.getObjectType());
    assertEquals(String.valueOf(rule.getId()), contentLinkSearchResult.getObjectId());
    assertEquals(plugin.getExtension().getIcon(), contentLinkSearchResult.getIcon());
    assertEquals(rule.getTitle(), contentLinkSearchResult.getTitle());
  }

  @Test
  public void getContentTitleWhenNotFound() {
    assertNull(plugin.getContentTitle(String.valueOf(RULE_ID), null));
    assertNull(plugin.getContentTitle(String.valueOf(RULE_ID), Locale.ENGLISH));
  }

  @Test
  public void getContentTitleWhenNoLang() {
    when(ruleService.findRuleById(RULE_ID)).thenReturn(rule);
    when(rule.getTitle()).thenReturn(RULE_TITLE);
    assertEquals(RULE_TITLE, plugin.getContentTitle(String.valueOf(RULE_ID), null));
    assertEquals(RULE_TITLE, plugin.getContentTitle(String.valueOf(RULE_ID), Locale.ENGLISH));
  }

  @Test
  @SneakyThrows
  public void getContentTitle() {
    when(ruleService.findRuleById(RULE_ID)).thenReturn(rule);
    when(rule.getTitle()).thenReturn(RULE_TITLE);
    assertEquals(RULE_TITLE, plugin.getContentTitle(String.valueOf(RULE_ID), Locale.ENGLISH));
  }

}
