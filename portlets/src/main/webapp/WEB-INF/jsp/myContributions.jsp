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
  String portletStorageId = ((String) request.getAttribute("portletStorageId"));
  String myContributionsPeriod = request.getAttribute("myContributionsPeriod") == null ? "week" : ((String[]) request.getAttribute("myContributionsPeriod"))[0];
  String myContributionsProgramLimit = request.getAttribute("myContributionsProgramLimit") == null ? "5" : ((String[]) request.getAttribute("myContributionsProgramLimit"))[0];
  String myContributionsDisplayLegend = request.getAttribute("myContributionsDisplayLegend") == null ? "true" : ((String[]) request.getAttribute("myContributionsDisplayLegend"))[0];
  Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
  boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage);
  String pageRef = currentPage.getPageKey().format();
%>
<div class="VuetifyApp">
  <div id="myContributions">
    <script type="text/javascript">
      require(['PORTLET/gamification-portlets/myContributions'], app => app.init(
        '<%=portletStorageId%>',
        '<%=myContributionsPeriod%>',
        <%=myContributionsProgramLimit%>,
        <%=myContributionsDisplayLegend%>,
        <%=canEdit%>,
        '<%=pageRef%>'
      ));
    </script>
  </div>
</div>
