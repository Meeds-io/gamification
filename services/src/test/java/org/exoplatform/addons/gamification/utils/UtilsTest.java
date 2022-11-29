/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.exoplatform.addons.gamification.utils;

import static org.exoplatform.addons.gamification.constant.GamificationConstant.GAMIFICATION_DEFAULT_DATA_PREFIX;
import static org.exoplatform.addons.gamification.utils.Utils.DEFAULT_IMAGE_REMOTE_ID;
import static org.exoplatform.addons.gamification.utils.Utils.TYPE;
import static org.exoplatform.addons.gamification.utils.Utils.getGamificationService;
import static org.exoplatform.addons.gamification.utils.Utils.isAttachmentTokenValid;
import static org.exoplatform.addons.gamification.utils.Utils.toUserInfo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.*;

import org.exoplatform.addons.gamification.service.RuleService;
import org.junit.Test;

import org.exoplatform.addons.gamification.entity.RuleEntity;
import org.exoplatform.addons.gamification.model.DomainDTO;
import org.exoplatform.addons.gamification.model.RuleDTO;
import org.exoplatform.addons.gamification.model.UserInfo;
import org.exoplatform.addons.gamification.service.GamificationService;
import org.exoplatform.addons.gamification.test.AbstractServiceTest;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

public class UtilsTest extends AbstractServiceTest {

  @Test
  public void testToRFC3339Date() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Date date = new Date(System.currentTimeMillis());
    date.setHours(15);
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    String rfc3339Date = Utils.toRFC3339Date(date);
    assertNotNull(rfc3339Date);
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testParseRFC3339Date() {
    String rfc3339Date = "2022-04-05T13:45:51.701Z";
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertNull(Utils.parseRFC3339Date(""));

    Date date = Utils.parseRFC3339Date(rfc3339Date);
    assertNotNull(date);
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testToSimpleDateFormat() {
    Date date = new Date(System.currentTimeMillis());
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    String rfc3339Date = Utils.toSimpleDateFormat(date);
    assertNotNull(rfc3339Date);
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertEquals("00:00:00", rfc3339Date.split("T")[1]);
    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testParseSimpleDate() {
    TimeZone.setDefault(TimeZone.getTimeZone("US/Hawaii"));

    String simpleDate = "2022-04-05T00:00:00";
    String fullDate = simpleDate.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];
    Date date = Utils.parseSimpleDate(simpleDate);
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    assertEquals("00:00:00", simpleDate.split("T")[1]);
    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testEscapeIllegalCharacterInMessage() {
    String message = "testing escaping illegal characters such as , and ; and \n";
    assertTrue(message.contains(","));
    assertTrue(message.contains(";"));
    assertTrue(message.contains("\n"));

    assertNull(Utils.escapeIllegalCharacterInMessage(null));

    message = Utils.escapeIllegalCharacterInMessage(message);
    assertNotNull(message);
    assertFalse(message.contains(","));
    assertFalse(message.contains(";"));
    assertFalse(message.contains("\n"));
  }

  @Test
  public void testGetCurrentUser() {
    ConversationState.setCurrent(null);
    String currentUser = Utils.getCurrentUser();
    assertNull(currentUser);
    ConversationState.setCurrent(new ConversationState(new org.exoplatform.services.security.Identity("root")));
    currentUser = Utils.getCurrentUser();
    assertNotNull(currentUser);
    assertEquals("root", currentUser);
  }

  @Test
  public void testGetRuleService() {
    RuleService ruleService = Utils.getRuleService();
    assertNotNull(ruleService);
  }

  @Test
  public void testGetGamificationService() {
    GamificationService gamificationService = getGamificationService();
    assertNotNull(gamificationService);
  }

  @Test
  public void testGetUserGlobalScore() {
    Long userScore = Utils.getUserGlobalScore("");
    assertEquals(0, (long) userScore);
    newGamificationActionsHistory();
    userScore = Utils.getUserGlobalScore(TEST_USER_SENDER);
    assertNotEquals(0, (long) userScore);
  }

  @Test
  public void testGetSpaceFromObjectId() {
    String ObjectId = "/portal/g/:spaces:test_space";
    String spaceDisplayName = Utils.getSpaceFromObjectID("test");
    assertNull(spaceDisplayName);
    spaceDisplayName = Utils.getSpaceFromObjectID("");
    assertNull(spaceDisplayName);
    spaceDisplayName = Utils.getSpaceFromObjectID(ObjectId);
    assertNotNull(spaceDisplayName);
    assertEquals("test space", spaceDisplayName);
  }

  @Test
  public void testCountAnnouncementsByChallenge() throws ObjectNotFoundException {
    RuleEntity ruleEntity = newRule("challenge1", "domain", 1l);
    newGamificationActionsHistoryWithRuleId("annoucement 1", ruleEntity.getId());
    newGamificationActionsHistoryWithRuleId("annoucement 2", ruleEntity.getId());
    assertEquals((Long) 2l, announcementService.countAllAnnouncementsByChallenge(ruleEntity.getId()));
    assertThrows(ObjectNotFoundException.class, () -> announcementService.countAllAnnouncementsByChallenge(528l));
  }

  @Test
  public void testCreateUser() {
    String[] spaceMembers = { "root" };
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    space.setManagers(spaceMembers);
    space.setMembers(spaceMembers);
    space.setRedactors(new String[0]);

    UserInfo userInfo = Utils.toUserInfo(rootIdentity, space, Collections.singletonList(1l));
    assertNotNull(userInfo);
    assertEquals("root", userInfo.getRemoteId());
    assertTrue(userInfo.isCanAnnounce());

    assertNull(toUserInfo("", new ArrayList<>(Collections.singleton(1L))));
  }

  @Test
  public void testGetDomainByTitle() {
    DomainDTO domain = newDomainDTO();
    DomainDTO savedDomain = Utils.getDomainByTitle(null);
    assertNull(savedDomain);
    savedDomain = Utils.getDomainByTitle("");
    assertNull(savedDomain);
    savedDomain = Utils.getDomainByTitle(domain.getTitle());
    assertNotNull(savedDomain);
  }
  @Test
  public void testGetEnabledDomainByTitle() {
    DomainDTO domain = newDomainDTO();
    DomainDTO savedDomain = Utils.getEnabledDomainByTitle(null);
    assertNull(savedDomain);
    savedDomain = Utils.getEnabledDomainByTitle("");
    assertNull(savedDomain);
    savedDomain = Utils.getEnabledDomainByTitle(domain.getTitle());
    assertNotNull(savedDomain);
  }

  @Test
  public void testGetRuleById() {
    RuleDTO rule = newRuleDTO();
    RuleDTO savedRule = Utils.getRuleById(0);
    assertNull(savedRule);
    savedRule = Utils.getRuleById(rule.getId());
    assertNotNull(savedRule);
    assertEquals(rule.getTitle(), savedRule.getTitle());
  }

  @Test
  public void testGetRuleByTitle() {

      RuleEntity rule = newRule(GAMIFICATION_DEFAULT_DATA_PREFIX + "test", "domain");
      RuleDTO savedRule = Utils.getRuleByTitle("");
      assertNull(savedRule);
      savedRule = Utils.getRuleByTitle("test_domain");
      assertNotNull(savedRule);
      assertEquals(rule.getTitle(), savedRule.getTitle());
  }

  @Test
  public void testGetIdentityByTypeAndId() {
    Identity identity = Utils.getIdentityByTypeAndId(OrganizationIdentityProvider.NAME, "root1");
    assertNotNull(identity);
  }
  @Test
  public void testGetUserRemoteId() {
    String remoteId = Utils.getUserRemoteId("15893");
    assertNull(remoteId);
    remoteId = Utils.getUserRemoteId("1");
    assertNotNull(remoteId);
  }

  @Test
  public void testCanAnnounce() {
    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");
    String username = "root";
    assertThrows(IllegalArgumentException.class, () -> Utils.canAnnounce("158", username));
    boolean canAnnounce = Utils.canAnnounce("1", "");
    assertFalse(canAnnounce);
    canAnnounce = Utils.canAnnounce("1", "root");
    assertTrue(canAnnounce);
  }

  @Test
  public void testGetSpaceById() {

    Space space = new Space();
    space.setId("1");
    space.setPrettyName("test_space");
    space.setDisplayName("test space");
    space.setGroupId("/spaces/test_space");

    Space savedSpace = Utils.getSpaceById("");
    assertNull(savedSpace);
    savedSpace = Utils.getSpaceById("2");
    assertNull(savedSpace);
    savedSpace = Utils.getSpaceById("1");
    assertNotNull(savedSpace);
    assertEquals(space.getId(), savedSpace.getId());
  }

  @Test
  public void testGetManagersByIds() {
    List<UserInfo> usersInfo = Utils.getManagersByIds(Collections.emptyList());
    assertEquals(0, usersInfo.size());
    usersInfo = Utils.getManagersByIds(Collections.singletonList(1l));
    assertEquals(1, usersInfo.size());
  }

  @Test
  public void testBuildActivityParams() {
    ExoSocialActivityImpl activity = new ExoSocialActivityImpl();
    Map<String, String> activityParams = new HashMap<>();
    activityParams.put("id", "1");
    activityParams.put("titre", "titre");
    activityParams.put("description", null);
    activity.setTemplateParams(activityParams);
    Map<String, String> extraParams = new HashMap<>();
    extraParams.put("toAdd", "true");

    Utils.buildActivityParams(activity, extraParams);
    Map<String, String> buildedParams = activity.getTemplateParams();
    assertNotNull(buildedParams);
    assertEquals(3, buildedParams.size());
    assertFalse(buildedParams.containsKey("description"));
  }

  @Test
  public void testBuildAttachmentUrl() {
    Long lastModifiedDate = System.currentTimeMillis();
    String attachementURl = Utils.buildAttachmentUrl("0", lastModifiedDate, TYPE, false);
    assertNull(attachementURl);
    attachementURl = Utils.buildAttachmentUrl("1", 0l, TYPE, true);
    assertNotNull(attachementURl);
    assertTrue(attachementURl.contains(DEFAULT_IMAGE_REMOTE_ID));
    attachementURl = Utils.buildAttachmentUrl("1", lastModifiedDate, TYPE, false);
    assertNotNull(attachementURl);
    assertFalse(attachementURl.contains(DEFAULT_IMAGE_REMOTE_ID));
  }

  @Test
  public void testIsAttachmentTokenValid() {
    long lastModifiedTime = System.currentTimeMillis();
    String domainId ="1";
    String token = Utils.generateAttachmentToken(domainId, TYPE, lastModifiedTime);

    assertTrue(isAttachmentTokenValid(token, domainId, TYPE, lastModifiedTime));
    assertFalse(isAttachmentTokenValid("", domainId, TYPE, lastModifiedTime));
  }
}
