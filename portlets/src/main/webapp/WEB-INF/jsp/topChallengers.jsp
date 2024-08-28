<%
/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
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
<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<% if (Utils.canAccessAnonymousResources()) {
  String portletStorageId = ((String) request.getAttribute("portletStorageId"));
  String topChallengersPeriod = request.getAttribute("topChallengersPeriod") == null ? "week" : ((String[]) request.getAttribute("topChallengersPeriod"))[0];
  String topChallengersCurrentPosition = request.getAttribute("topChallengersCurrentPosition") == null ? "false" : ((String[]) request.getAttribute("topChallengersCurrentPosition"))[0];
  Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
  boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage);
  String pageRef = currentPage.getPageKey().format();
%>
  <div class="VuetifyApp">
    <div id="topChallengers">
      <script type="text/javascript">
        window.require(['PORTLET/gamification-portlets/topChallengers'], app => app.init(
          '<%=portletStorageId%>',
          '<%=topChallengersPeriod%>',
          <%=topChallengersCurrentPosition%>,
          <%=canEdit%>,
          '<%=pageRef%>'
        ));
      </script>
    </div>
  </div>
<% } %>
