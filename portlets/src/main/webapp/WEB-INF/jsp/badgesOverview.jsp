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
<%@ page import="org.exoplatform.social.webui.Utils"%>
<%@ page import="org.exoplatform.social.core.identity.model.Identity" %>
<%@ page import="org.exoplatform.social.core.identity.model.Profile" %>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="org.exoplatform.services.security.ConversationState"%>
<%
  String profileOwnerId = Utils.getOwnerIdentityId();
  Identity ownerIdentity = Utils.getOwnerIdentity(true);
  Profile profile = ownerIdentity.getProfile();
  String portletStorageId = ((String) request.getAttribute("portletStorageId"));
  String showName = request.getAttribute("showName") == null ? "false" : ((String[]) request.getAttribute("showName"))[0];
  String sortBy = request.getAttribute("badgesSortBy") == null ? "" : ((String[]) request.getAttribute("badgesSortBy"))[0];
  Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
  boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage, ConversationState.getCurrent().getIdentity());
  String pageRef = currentPage.getPageKey().format();
%>
<div class="VuetifyApp">
  <div data-app="true"
    class="v-application white v-application--is-ltr theme--light profileAboutMeOther"
    id="badgesOverview">
    <script type="text/javascript">
      eXo.env.portal.addOnLoadCallback(() => {
        window.require(['PORTLET/gamification-portlets/BadgesOverview'], app => app.init(
          <%=portletStorageId%>,
          <%=showName%>,
          '<%=sortBy%>',
          <%=canEdit%>,
          '<%=pageRef%>'
        ));
      });
    </script>
  </div>
</div>
