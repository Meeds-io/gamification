/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.meeds.gamification.test;

import static org.exoplatform.commons.notification.template.TemplateUtils.getExcerptSubject;

import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.channel.AbstractChannel;
import org.exoplatform.commons.api.notification.channel.template.AbstractTemplateBuilder;
import org.exoplatform.commons.api.notification.model.ChannelKey;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.UserSetting;
import org.exoplatform.commons.api.notification.plugin.BaseNotificationPlugin;
import org.exoplatform.commons.api.notification.service.setting.PluginContainer;
import org.exoplatform.commons.api.notification.service.setting.PluginSettingService;
import org.exoplatform.commons.api.notification.service.setting.UserSettingService;
import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.notification.channel.MailChannel;
import org.exoplatform.commons.notification.channel.WebChannel;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.settings.jpa.CacheUserSettingServiceImpl;
import org.exoplatform.social.notification.mock.MockNotificationService;

import io.meeds.gamification.service.AnnouncementService;
import io.meeds.gamification.service.ProgramService;
import io.meeds.gamification.service.RuleService;

public abstract class AbstractPluginTest extends AbstractServiceTest { // NOSONAR

  public static final String        ADMIN_USER    = "root1";

  public static final String        SIMPLE_USER   = "root5";

  protected Locale                  initialDefaultLocale;

  protected UserSettingService      userSettingService;

  protected PluginContainer         pluginService;

  protected MockNotificationService notificationService;

  protected PluginSettingService    pluginSettingService;

  protected ExoFeatureService       exoFeatureService;

  public abstract BaseNotificationPlugin getPlugin();

  @Override
  public void setUp() throws Exception {
    super.setUp();

    userSettingService = getContainer().getComponentInstanceOfType(UserSettingService.class);
    pluginService = getContainer().getComponentInstanceOfType(PluginContainer.class);
    notificationService = getContainer().getComponentInstanceOfType(MockNotificationService.class);
    pluginSettingService = getContainer().getComponentInstanceOfType(PluginSettingService.class);
    exoFeatureService = getContainer().getComponentInstanceOfType(ExoFeatureService.class);
    programService = getContainer().getComponentInstanceOfType(ProgramService.class);
    ruleService = getContainer().getComponentInstanceOfType(RuleService.class);
    announcementService = getContainer().getComponentInstanceOfType(AnnouncementService.class);

    // set default locale to en (used for notification wording) to have
    // deterministic tests
    initialDefaultLocale = Locale.getDefault();
    Locale.setDefault(new Locale("en", "US"));

    turnON(getPlugin());
    notificationService.clearAll();
    CacheService cacheService = ExoContainerContext.getService(CacheService.class);
    cacheService.getCacheInstance(CacheUserSettingServiceImpl.CACHE_NAME).clearCache();

    OrganizationService organizationService = ExoContainerContext.getService(OrganizationService.class);
    UserHandler userHandler = organizationService.getUserHandler();
    if (userHandler.findUserByName(ADMIN_USER) == null) {
      User user = userHandler.createUserInstance(ADMIN_USER);
      user.setFirstName(ADMIN_USER);
      user.setLastName(ADMIN_USER);
      user.setEmail(ADMIN_USER + "@meeds.io");
      userHandler.createUser(user, false);
    }
    if (userHandler.findUserByName(SIMPLE_USER) == null) {
      User user = userHandler.createUserInstance(SIMPLE_USER);
      user.setFirstName(SIMPLE_USER);
      user.setLastName(SIMPLE_USER);
      user.setEmail(SIMPLE_USER + "@meeds.io");
      userHandler.createUser(user, false);
    }
  }

  @Override
  public void tearDown() {
    turnOFF(getPlugin());
    if (initialDefaultLocale != null) {
      Locale.setDefault(initialDefaultLocale);
    }

    super.tearDown();
  }

  /**
   * It will be invoked after make Activity, Relationship and New User also.
   * Makes the notification message and retrieve from MockNotificationService
   * 
   * @return
   */
  protected NotificationInfo getNotificationInfo(String username) {
    List<NotificationInfo> list = notificationService.storeDigest(username);
    assertFalse(list.isEmpty());
    return list.get(0);
  }

  protected List<NotificationInfo> getNotificationInfos(String username) {
    return notificationService.storeDigest(username);
  }

  /**
   * Validates the Message's subject
   * 
   * @param message
   * @param validatedString
   */
  protected void assertSubject(MessageInfo message, String validatedString) {
    assertEquals(getExcerptSubject(validatedString), message.getSubject());
  }

  /**
   * Validates the Message's body
   * 
   * @param message
   * @param includedString
   */
  protected void assertBody(MessageInfo message, String includedString) {
    assertTrue("body = '" + message.getBody() + "' \r\n doesn't contain\r\n " + includedString,
               message.getBody().indexOf(includedString) > 0);
  }

  /**
   * Validate the digest email
   * 
   * @param writer
   * @param includedString
   */
  protected void assertDigest(Writer writer, String includedString) {
    assertEquals(includedString, writer.toString().replaceAll("\\<.*?>", ""));
  }

  /**
   * Asserts the number of notification what made by the plugins.
   * 
   * @param number
   */
  protected void assertMadeMailDigestNotifications(int number) {
    UserSetting setting = userSettingService.get(rootIdentity.getRemoteId());
    if (setting.isInDaily(getPlugin().getKey().getId())) {
      assertEquals(number, notificationService.sizeOfStoredDigest());
    }
  }

  /**
   * Asserts the number of notification what made by the plugins.
   * 
   * @param number
   */
  protected List<NotificationInfo> assertMadeMailDigestNotifications(String username, int number) {
    UserSetting setting = userSettingService.get(username);
    List<NotificationInfo> got = notificationService.storeDigest(username);
    if (setting.isActive(MailChannel.ID, getPlugin().getKey().getId())) {
      got = notificationService.storeInstantly(username);
      assertEquals(number, got.size());
    }
    return got;
  }

  /**
   * Asserts the number of web's notifications what made by the plugins.
   * 
   * @param number
   */
  protected void assertMadeWebNotifications(int number) {
    UserSetting setting = userSettingService.get(rootIdentity.getRemoteId());
    if (setting.isActive(WebChannel.ID, getPlugin().getKey().getId())) {
      assertEquals(number, notificationService.sizeOfWebNotifs());
    }
  }

  /**
   * Asserts the number of web's notifications what made by the plugins.
   * 
   * @param number
   */
  protected List<NotificationInfo> assertMadeWebNotifications(String username, int number) {
    UserSetting setting = userSettingService.get(username);
    assertTrue(setting.isActive(WebChannel.ID, getPlugin().getKey().getId()));
    List<NotificationInfo> got = notificationService.storeWebNotifs(username);
    assertEquals(number, got.size());
    return got;
  }

  /**
   * Turn on the plug in
   * 
   * @param plugin
   */
  protected void turnON(BaseNotificationPlugin plugin) {
    pluginSettingService.saveActivePlugin(MailChannel.ID, plugin.getId(), true);
  }

  /**
   * Turn off the plugin
   * 
   * @param plugin
   */
  protected void turnOFF(BaseNotificationPlugin plugin) {
    pluginSettingService.saveActivePlugin(MailChannel.ID, plugin.getId(), false);
  }

  protected void turnFeatureOn() {
    exoFeatureService.saveActiveFeature("notification", true);
  }

  protected void turnFeatureOff() {
    exoFeatureService.saveActiveFeature("notification", false);
  }

  protected void setInstantlySettings(String userId, List<String> settings) {
    UserSetting userSetting = userSettingService.get(userId);

    if (userSetting == null) {
      userSetting = UserSetting.getInstance();
      userSetting.setUserId(userId);
    }
    userSetting.setChannelActive(MailChannel.ID);
    //
    userSetting.setChannelPlugins(MailChannel.ID, settings);
    userSettingService.save(userSetting);
  }

  /**
   * Make Daily setting
   * 
   * @param userId
   * @param settings
   */
  protected void setDailySetting(String userId, List<String> settings) {
    UserSetting userSetting = userSettingService.get(userId);

    if (userSetting == null) {
      userSetting = UserSetting.getInstance();
      userSetting.setUserId(userId);
    }
    userSetting.setChannelActive(MailChannel.ID);

    userSetting.setDailyPlugins(settings);
    userSettingService.save(userSetting);
  }

  protected void setWeeklySetting(String userId, List<String> settings) {
    UserSetting userSetting = userSettingService.get(userId);

    if (userSetting == null) {
      userSetting = UserSetting.getInstance();
      userSetting.setUserId(userId);
    }
    userSetting.setChannelActive(MailChannel.ID);

    userSetting.setWeeklyPlugins(settings);
    userSettingService.save(userSetting);
  }

  protected AbstractTemplateBuilder getTemplateBuilder(NotificationContext ctx) {
    AbstractChannel channel = ctx.getChannelManager().getChannel(ChannelKey.key(MailChannel.ID));
    assertNotNull(channel);
    return channel.getTemplateBuilder(ctx.getNotificationInfo().getKey());
  }

  protected MessageInfo buildMessageInfo(NotificationContext ctx) {
    AbstractTemplateBuilder templateBuilder = getTemplateBuilder();
    if (templateBuilder == null) {
      templateBuilder = getTemplateBuilder(ctx);
    }
    MessageInfo massage = templateBuilder.buildMessage(ctx);
    assertNotNull(massage);
    return massage;
  }

  protected void buildDigest(NotificationContext ctx, Writer writer) {
    AbstractTemplateBuilder templateBuilder = getTemplateBuilder();
    if (templateBuilder == null) {
      templateBuilder = getTemplateBuilder(ctx);
    }
    templateBuilder.buildDigest(ctx, writer);
  }

  public abstract AbstractTemplateBuilder getTemplateBuilder();
}
