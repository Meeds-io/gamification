/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
package org.exoplatform.addons.gamification;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.jgroups.util.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GamificationUtils {

    // BLACK LIST GROUP
    public static String BLACK_LISTED_USERS_GROUP = "/leaderboard-blacklist-users";
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_USER_KEY = "GAMIFICATION_DATAMODEL_MIGRATION_USER";
    //scope of gamification migration status
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY = "GAMIFICATION_DATAMODEL_MIGRATION_DONE_KEY";
    //status of gamification migration (true if migration completed successfully)
    public static final String GAMIFICATION_DATAMODEL_MIGRATION_DONE = "GAMIFICATION_DATAMODEL_MIGRATION_DONE";

    /**
     * Compute the list of user whithin the black list (user we shouldn't display them on leaderboard screen)
     * @param userName : the user social id to check if is already black listed
     * @return : return true is the user is black lisetd false else
     */
    public static boolean isBlackListed(String userName) {

        List<String> blackListedUsers = new ArrayList<String>();

        try {
            ListAccess<User> usersBlackList = CommonsUtils.getService(OrganizationService.class).getUserHandler().findUsersByGroupId(BLACK_LISTED_USERS_GROUP);
            if (null != usersBlackList && usersBlackList.getSize() > 0) {
                User[] managerUser = usersBlackList.load(0, usersBlackList.getSize());
                for (User user : managerUser) {
                    try {
                        blackListedUsers.add(user.getUserName());
                    } catch (Exception e) {

                    }
                }
                return blackListedUsers.stream().anyMatch(userName::equals);
            }
        } catch (Exception e) {

        }

        return false;

    }

    /**
     * Build current username from url
     * @param url : http url
     * @param separator : seperator to use to build profile's owner
     * @return username
     */
    public static String extractProfileOwnerFromUrl(String url, String separator) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        if (StringUtils.isEmpty(separator)) {
            return "";
        }
        int pos = url.lastIndexOf(separator);
        if (pos == -1 || pos == (url.length() - separator.length())) {
            return "";
        }
        return url.substring(pos + separator.length());
    }

    /**
     * Build space name from url
     * @param url : http url
     * @return username
     * @throws Exception
     */
    public static Space extractSpaceNameFromUrl(String url) throws Exception {

        if (StringUtils.isEmpty(url)) {
            return null;
        }

        // Get Space by name
        StringBuffer sb = new StringBuffer();
        // Build space name
        String spaceName = (url.split(":spaces:")[1]).split("/")[0];
        sb.append("/spaces/").append(spaceName);

        // Find Space by GroupId
        Space space = CommonsUtils.getService(SpaceService.class).getSpaceByGroupId(sb.toString());

        return space;

    }
}
