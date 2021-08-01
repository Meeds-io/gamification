<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2020 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<%@ page import="org.exoplatform.social.webui.Utils"%>
<%@ page import="org.exoplatform.social.core.identity.model.Identity" %>
<%@ page import="org.exoplatform.social.core.identity.model.Profile" %>
<%
  String profileOwnerId = Utils.getOwnerIdentityId();
  Identity ownerIdentity = Utils.getOwnerIdentity(true);
  Profile profile = ownerIdentity.getProfile();
  boolean isExternal = profile.getProperty(Profile.EXTERNAL) != null && ((String) profile.getProperty(Profile.EXTERNAL)).equals("true");
%>
<% if (!isExternal) { %>
  <div class="VuetifyApp">
    <div data-app="true"
      class="v-application white v-application--is-ltr theme--light profileAboutMeOther"
      id="badgesOverview">
      <v-cacheable-dom-app cache-id="badgesOverview_<%=profileOwnerId%>"></v-cacheable-dom-app>
      <script type="text/javascript" defer="defer">
        eXo.env.portal.addOnLoadCallback(() => {
          require(['PORTLET/gamification-portlets/BadgesOverview'], app => app.init());
        });
      </script>
    </div>
  </div>
<% } %>