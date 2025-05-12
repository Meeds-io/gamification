<%@page import="org.exoplatform.services.security.ConversationState"%>
<%@ page import="io.meeds.gamification.utils.Utils"%>
<%@ page import="org.exoplatform.portal.config.model.Page"%>
<%@ page import="org.exoplatform.portal.application.PortalRequestContext"%>
<%@ page import="org.exoplatform.portal.config.UserACL"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="java.util.List" %>
<%@ page import="org.exoplatform.social.core.space.SpaceUtils" %>
<%@ page import="org.exoplatform.social.core.space.model.Space" %>
<%@ page import="org.exoplatform.social.core.space.spi.SpaceService" %>
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
    boolean isAdministrator = Utils.isRewardingManager(username);
    Space currentSpace = SpaceUtils.getSpaceByContext();
    boolean isSpaceManager;
    if (currentSpace != null) {
        isSpaceManager = ExoContainerContext.getService(SpaceService.class).canManageSpace(currentSpace, username);
    } else {
        List<String> memberSpaceIds = ExoContainerContext.getService(SpaceService.class).getMemberSpacesIds(username, 0, -1);
        isSpaceManager = memberSpaceIds.stream()
               .map(id -> ExoContainerContext.getService(SpaceService.class).getSpaceById(id))
                .anyMatch(space -> ExoContainerContext.getService(SpaceService.class).canManageSpace(space, username));
    }
%>
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/programsOverview'], app => app.init(
        <%=portletStorageId%>,
        <%=limit%>,
        '<%=sortBy%>',
        <%=canEdit%>,
        '<%=pageRef%>',
        <%=isAdministrator%>,
        <%=isSpaceManager%>
      ));
    </script>
<% } else { %>
    <script type="text/javascript">
      require(['SHARED/vue'], () => Vue.prototype.$updateApplicationVisibility(false, document.querySelector('#programsOverview')));
    </script>
<% } %>
  </div>
</div>
