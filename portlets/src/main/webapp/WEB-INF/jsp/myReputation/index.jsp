<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
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
<%@ page import="org.exoplatform.services.security.ConversationState"%>
<%@ page import="org.exoplatform.social.core.manager.IdentityManager"%>
<%@ page import="org.exoplatform.commons.utils.CommonsUtils"%>
<%@ page import="org.exoplatform.kudos.rest.KudosREST"%>
<%@ page import="java.time.Instant" %>
<%@ page import="org.json.JSONObject" %>
<%
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    KudosREST kudosRest = CommonsUtils.getService(KudosREST.class);
    String currentUserId = identityManager.getOrCreateUserIdentity(ConversationState.getCurrent().getIdentity().getUserId()).getId();
    Instant now = Instant.now();

    Object sentKudos = kudosRest.getSentKudosByPeriod(Long.parseLong(currentUserId), now.getEpochSecond(), "WEEK", 0, true).getEntity();
    JSONObject jsonSentKudos = new JSONObject(sentKudos);

    Object receivedKudos = kudosRest.getReceivedKudosByPeriod(Long.parseLong(currentUserId), now.getEpochSecond(), "WEEK", 0, true).getEntity();
    JSONObject jsonReceivedKudos = new JSONObject(receivedKudos);
%>
<div class="VuetifyApp">
  <div id="myReputation">
    <script type="text/javascript">
              require(['PORTLET/gamification-portlets/myReputation'], app => app.init(<%=%>, <%=%>));
      </script>
  </div>
</div>
