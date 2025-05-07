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
<%@ page import="org.exoplatform.services.security.ConversationState" %>
<%@ page import="java.util.List" %>
<%@ page import="org.exoplatform.social.core.space.spi.SpaceService" %>
<%@ page import="org.apache.commons.collections4.CollectionUtils" %>

<%
String username = ConversationState.getCurrent().getIdentity().getUserId();
boolean isAdministrator = Utils.isRewardingManager(username);
List<String> managedSpaceIds = ExoContainerContext.getService(SpaceService.class).getManagerSpacesIds(username, 0, -1);
boolean isProgramManager = isAdministrator || ExoContainerContext.getService(ProgramService.class).countOwnedPrograms(ConversationState.getCurrent().getIdentity().getUserId()) > 0;
%>

<div class="VuetifyApp">
  <div id="EngagementCenterPrograms">
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/EngagementCenterPrograms'], app => app.init(<%=isAdministrator%>, <%=isProgramManager%>, <%=CollectionUtils.isNotEmpty(managedSpaceIds)%>));
    </script>
  </div>
</div>
