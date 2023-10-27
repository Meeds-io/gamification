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
<%@ page import="io.meeds.gamification.service.ProgramService"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="io.meeds.gamification.utils.Utils" %>

<%
  if (Utils.canAccessAnonymousResources()) {
    boolean isAdministrator = request.getRemoteUser() != null && Utils.isRewardingManager(request.getRemoteUser());
    boolean isProgramManager = request.getRemoteUser() != null && (isAdministrator || ExoContainerContext.getService(ProgramService.class).countOwnedPrograms(request.getRemoteUser()) > 0);
%>
<div class="VuetifyApp singlePageApplication">
  <div id="EngagementCenterActions">
    <script type="text/javascript">
      require(['PORTLET/gamification-portlets/EngagementCenterActions'], app => app.init(<%=isAdministrator%>, <%=isProgramManager%>));
    </script>
  </div>
</div>
<% } %>

