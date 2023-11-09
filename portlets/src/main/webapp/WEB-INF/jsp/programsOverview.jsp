<%@page import="io.meeds.gamification.utils.Utils"%>
<%@page import="org.exoplatform.web.PortalHttpServletResponseWrapper"%>
<%@page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%
  if (Utils.canAccessAnonymousResources()) {
    PortalRequestContext rcontext = (PortalRequestContext) PortalRequestContext.getCurrentInstance();
    PortalHttpServletResponseWrapper responseWrapper = (PortalHttpServletResponseWrapper) rcontext.getResponse();
    responseWrapper.addHeader("Link", "</portal/rest/gamification/programs?returnSize=true&limit=4&status=ENABLED&sortBy=modifiedDate&sortDescending=true&lang=" + request.getLocale().toLanguageTag() + "&expand=countActiveRules%2Cadministrators>; rel=preload; as=fetch; crossorigin=use-credentials", false);
%>
  <div class="VuetifyApp">
    <div id="programsOverview">
      <script type="text/javascript">
                require(['PORTLET/gamification-portlets/programsOverview'], app => app.init());
        </script>
    </div>
  </div>
<% } %>