<%@page import="io.meeds.gamification.utils.Utils"%>
<% if (Utils.canAccessAnonymousResources()) { %>
  <div class="VuetifyApp">
    <div id="topChallengers">
      <script type="text/javascript">
                require(['PORTLET/gamification-portlets/topChallengers'], app => app.init());
        </script>
    </div>
  </div>
<% } %>
