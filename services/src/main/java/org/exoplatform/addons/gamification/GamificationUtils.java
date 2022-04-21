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
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

public class GamificationUtils {

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
     */
    public static Space extractSpaceNameFromUrl(String url) {

        if (StringUtils.isEmpty(url)) {
            return null;
        }

        // Get Space by name
        StringBuilder sb = new StringBuilder();
        // Build space name
        String spaceName = (url.split(":spaces:")[1]).split("/")[0];
        sb.append("/spaces/").append(spaceName);

        // Find Space by GroupId
        return CommonsUtils.getService(SpaceService.class).getSpaceByGroupId(sb.toString());

    }
}
