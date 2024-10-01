<%@page import="org.exoplatform.services.security.ConversationState"%>
<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<div class="VuetifyApp">
  <div id="programsOverview">
<%
  if (Utils.canAccessAnonymousResources()) {
    String portletStorageId = ((String) request.getAttribute("portletStorageId"));
    String limit = request.getAttribute("limit") == null ? "4" : ((String[]) request.getAttribute("limit"))[0];
    String sortBy = request.getAttribute("programsSortBy") == null ? "" : ((String[]) request.getAttribute("programsSortBy"))[0];
    Page currentPage = PortalRequestContext.getCurrentInstance().getPage();
    boolean canEdit = ExoContainerContext.getService(UserACL.class).hasEditPermission(currentPage, ConversationState.getCurrent().getIdentity());
    String pageRef = currentPage.getPageKey().format();
%>
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/programsOverview'], app => app.init(
        <%=portletStorageId%>,
        <%=limit%>,
        '<%=sortBy%>',
        <%=canEdit%>,
        '<%=pageRef%>'
      ));
    </script>
<% } else { %>
    <script type="text/javascript">
      require(['SHARED/vue'], () => Vue.prototype.$updateApplicationVisibility(false, document.querySelector('#programsOverview')));
    </script>
<% } %>
  </div>
</div>
