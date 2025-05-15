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
<%@ page import="org.exoplatform.social.core.space.SpaceUtils" %>
<%@ page import="org.exoplatform.social.core.space.model.Space" %>

<%
  String username = ConversationState.getCurrent().getIdentity().getUserId();
  ProgramService programService = ExoContainerContext.getService(ProgramService.class);
  boolean isAdministrator = Utils.isRewardingManager(username);
  boolean isProgramManager = isAdministrator || programService.countOwnedPrograms(username) > 0;

  Space currentSpace = SpaceUtils.getSpaceByContext();
  boolean canAddProgram = programService.canAddProgram(username, currentSpace != null ? currentSpace.getSpaceId() : 0);
%>

<div class="VuetifyApp">
  <div id="EngagementCenterPrograms">
    <script type="text/javascript">
      window.require(['SHARED/engagementCenterProgramsBundle'], app => app.init(<%=isAdministrator%>, <%=isProgramManager%>, <%=canAddProgram%>));
    </script>
  </div>
</div>
