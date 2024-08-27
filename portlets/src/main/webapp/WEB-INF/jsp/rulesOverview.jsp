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
<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%
 if (Utils.canAccessAnonymousResources()) {
   String portletStorageId = ((String) request.getAttribute("portletStorageId"));
   String sortBy = request.getAttribute("rulesSortBy") == null ? "score" : ((String[]) request.getAttribute("rulesSortBy"))[0];
   String lockedRulesLimit = request.getAttribute("lockedRulesLimit") == null ? "2" : ((String[]) request.getAttribute("lockedRulesLimit"))[0];
   String endingRulesLimit = request.getAttribute("endingRulesLimit") == null ? "2" : ((String[]) request.getAttribute("endingRulesLimit"))[0];
   String availableRulesLimit = request.getAttribute("availableRulesLimit") == null ? "4" : ((String[]) request.getAttribute("availableRulesLimit"))[0];
   String upcomingRulesLimit = request.getAttribute("upcomingRulesLimit") == null ? "2" : ((String[]) request.getAttribute("upcomingRulesLimit"))[0];
   Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
   boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage);
   String pageRef = currentPage.getPageKey().format();
%>
<div class="VuetifyApp">
  <div id="rulesOverview">
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/challengesOverview'], app => app.init(
        <%=portletStorageId%>,
        <%=lockedRulesLimit%>,
        <%=endingRulesLimit%>,
        <%=availableRulesLimit%>,
        <%=upcomingRulesLimit%>,
        '<%=sortBy%>',
        <%=canEdit%>,
        '<%=pageRef%>'
      ));
    </script>
  </div>
</div>
<% } %>
