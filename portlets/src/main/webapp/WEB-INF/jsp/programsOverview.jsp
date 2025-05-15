<%@page import="org.exoplatform.services.security.ConversationState"%>
<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="org.exoplatform.social.core.space.SpaceUtils" %>
<%@ page import="org.exoplatform.social.core.space.model.Space" %>
<%@ page import="io.meeds.gamification.service.ProgramService" %>
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

    String username = ConversationState.getCurrent().getIdentity().getUserId();
    ProgramService programService = ExoContainerContext.getService(ProgramService.class);
    boolean isAdministrator = Utils.isRewardingManager(username);

    boolean isProgramManager = isAdministrator || programService.countOwnedPrograms(username) > 0;

    Space currentSpace = SpaceUtils.getSpaceByContext();
    boolean canAddProgram = programService.canAddProgram(username, currentSpace != null ? currentSpace.getSpaceId() : 0);
%>
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/programsOverview'], app => app.init(
        <%=portletStorageId%>,
        <%=limit%>,
        '<%=sortBy%>',
        <%=canEdit%>,
        '<%=pageRef%>',
        <%=isAdministrator%>,
        <%=isProgramManager%>,
        <%=canAddProgram%>
      ));
    </script>
<% } else { %>
    <script type="text/javascript">
      require(['SHARED/vue'], () => Vue.prototype.$updateApplicationVisibility(false, document.querySelector('#programsOverview')));
    </script>
<% } %>
  </div>
</div>
