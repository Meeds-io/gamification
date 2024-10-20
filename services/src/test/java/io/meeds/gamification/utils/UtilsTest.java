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

package io.meeds.gamification.utils;
import static io.meeds.gamification.mock.SpaceServiceMock.SPACE_GROUP_ID;
import static io.meeds.gamification.mock.SpaceServiceMock.SPACE_PRETTY_NAME;
import static io.meeds.gamification.utils.Utils.DEFAULT_COVER_REMOTE_ID;
import static io.meeds.gamification.utils.Utils.ATTACHMENT_COVER_TYPE;
import static io.meeds.gamification.utils.Utils.isAttachmentTokenValid;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

import io.meeds.gamification.model.RuleDTO;
import io.meeds.gamification.model.UserInfoContext;
import io.meeds.gamification.rest.builder.ProgramBuilder;
import io.meeds.gamification.test.AbstractServiceTest;

@SuppressWarnings("deprecation")
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
  public void testGetSpaceFromObjectId() {
    String objectId = "/portal/g/:spaces:" + SPACE_PRETTY_NAME;
    String spaceDisplayName = Utils.getSpaceFromObjectID("test");
    assertNull(spaceDisplayName);
    spaceDisplayName = Utils.getSpaceFromObjectID("");
    assertNull(spaceDisplayName);
    spaceDisplayName = Utils.getSpaceFromObjectID(objectId);
    assertNotNull(spaceDisplayName);
    assertEquals("test space", spaceDisplayName);
  }

  @Test
  public void testCreateUser() {
    RuleDTO rule = newRuleDTO();
    Identity identity = identityManager.getOrCreateUserIdentity("root1");

    UserInfoContext userContext = ProgramBuilder.toUserContext(programService, rule.getProgram(), identity.getRemoteId());
    assertNotNull(userContext);
    assertEquals("root1", userContext.getRemoteId());
    assertFalse(userContext.isAllowedToRealize());
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
  public void testGetSpaceById() {

    Space space = new Space();
    space.setId("1");
    space.setPrettyName(SPACE_PRETTY_NAME);
    space.setDisplayName("test space");
    space.setGroupId(SPACE_GROUP_ID);

    Space savedSpace = Utils.getSpaceById("");
    assertNull(savedSpace);
    savedSpace = Utils.getSpaceById("2");
    assertNull(savedSpace);
    savedSpace = Utils.getSpaceById("1");
    assertNotNull(savedSpace);
    assertEquals(space.getId(), savedSpace.getId());
  }

  @Test
  public void testBuildAttachmentUrl() {
    Long lastModifiedDate = System.currentTimeMillis();
    String attachementURl = Utils.buildAttachmentUrl("0", lastModifiedDate, ATTACHMENT_COVER_TYPE, DEFAULT_COVER_REMOTE_ID, false);
    assertNull(attachementURl);
    attachementURl = Utils.buildAttachmentUrl("1", 0l, ATTACHMENT_COVER_TYPE, DEFAULT_COVER_REMOTE_ID, true);
    assertNotNull(attachementURl);
    assertTrue(attachementURl.contains(DEFAULT_COVER_REMOTE_ID));
    attachementURl = Utils.buildAttachmentUrl("1", lastModifiedDate, ATTACHMENT_COVER_TYPE, DEFAULT_COVER_REMOTE_ID, false);
    assertNotNull(attachementURl);
    assertFalse(attachementURl.contains(DEFAULT_COVER_REMOTE_ID));
  }

  @Test
  public void testIsAttachmentTokenValid() {
    long lastModifiedTime = System.currentTimeMillis();
    String domainId = "1";
    String token = Utils.generateAttachmentToken(domainId, ATTACHMENT_COVER_TYPE, lastModifiedTime);

    assertTrue(isAttachmentTokenValid(token, domainId, ATTACHMENT_COVER_TYPE, lastModifiedTime));
    assertFalse(isAttachmentTokenValid("", domainId, ATTACHMENT_COVER_TYPE, lastModifiedTime));
  }
}
