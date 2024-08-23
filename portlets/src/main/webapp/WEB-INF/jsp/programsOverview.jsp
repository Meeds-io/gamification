<%@page import="io.meeds.gamification.utils.Utils"%>
<%@page import="org.exoplatform.web.PortalHttpServletResponseWrapper"%>
<%@page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%
  if (Utils.canAccessAnonymousResources()) {
%>
  <div class="VuetifyApp">
    <div id="programsOverview">
      <script type="text/javascript">
        window.require(['PORTLET/gamification-portlets/programsOverview'], app => app.init());
      </script>
    </div>
  </div>
<% } %>