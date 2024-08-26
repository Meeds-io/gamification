<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%
  if (Utils.canAccessAnonymousResources()) {
    String portletStorageId = ((String) request.getAttribute("portletStorageId"));
    String limit = request.getAttribute("limit") == null ? "4" : ((String[]) request.getAttribute("limit"))[0];
    String sortBy = request.getAttribute("sortBy") == null ? "" : ((String[]) request.getAttribute("sortBy"))[0];
    Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
    boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage);
    String pageRef = currentPage.getPageKey().format();
%>
  <div class="VuetifyApp">
    <div id="programsOverview">
      <script type="text/javascript">
        window.require(['PORTLET/gamification-portlets/programsOverview'], app => app.init(
          '<%=portletStorageId%>',
          <%=limit%>,
          '<%=sortBy%>',
          <%=canEdit%>,
          '<%=pageRef%>'
        ));
      </script>
    </div>
  </div>
<% } %>