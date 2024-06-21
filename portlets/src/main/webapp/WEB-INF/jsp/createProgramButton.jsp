<%@ page import="io.meeds.gamification.service.ProgramService"%>
<%@ page import="org.exoplatform.container.ExoContainerContext"%>
<%@ page import="org.exoplatform.services.security.ConversationState" %>
<%
  ProgramService programService = ExoContainerContext.getService(ProgramService.class);
  boolean canManagePrograms =
    request.getRemoteUser() != null
    && programService.countPublicPrograms() == 0
    && (programService.canAddProgram(ConversationState.getCurrent().getIdentity())
        || programService.countOwnedPrograms(request.getRemoteUser()) > 0);
%>
<div class="VuetifyApp">
  <div id="createProgramButton">
    <script type="text/javascript">
      window.require(['PORTLET/gamification-portlets/CreateProgramButton'], app => app.init(<%=canManagePrograms%>));
    </script>
  </div>
</div>
