<%@page import="io.meeds.gamification.utils.Utils"%>
<% if (Utils.canAccessAnonymousResources()) { %>
  <div class="VuetifyApp">
    <div id="programsOverview">
      <script type="text/javascript">
                require(['PORTLET/gamification-portlets/programsOverview'], app => app.init());
        </script>
    </div>
  </div>
<% } %>
  