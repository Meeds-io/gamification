<%
/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.exoplatform.social.webui.Utils"%>
<%@page import="org.exoplatform.social.core.identity.model.Identity" %>
<%@page import="org.exoplatform.social.core.identity.model.Profile" %>
<%
  Identity ownerIdentity = Utils.getOwnerIdentity(true);
  Profile profile = ownerIdentity.getProfile();
  boolean isExternal = profile.getProperty(Profile.EXTERNAL) != null && ((String) profile.getProperty(Profile.EXTERNAL)).equals("true");

  if (!isExternal) { %>
  <div class="VuetifyApp">
    <div id="profile-stats-portlet"></div>
    <script type="text/javascript">
      window.require(['SHARED/profileStatsBundle'], app => app.init());
    </script>
  </div>
<% } %>