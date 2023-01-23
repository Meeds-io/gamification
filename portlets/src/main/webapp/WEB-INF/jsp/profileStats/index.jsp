<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.exoplatform.social.webui.Utils"%>
<%@page import="org.exoplatform.social.core.identity.model.Identity" %>
<%@page import="org.exoplatform.social.core.identity.model.Profile" %>
<%
  Identity ownerIdentity = Utils.getOwnerIdentity(true);
  Profile profile = ownerIdentity.getProfile();
  boolean isExternal = profile.getProperty(Profile.EXTERNAL) != null && ((String) profile.getProperty(Profile.EXTERNAL)).equals("true");

  if (!isExternal) { %>
  <div class="VuetifyApp">
    <div id="profile-stats-portlet"></div>
    <script type="text/javascript">
      require(['SHARED/profileStatsBundle'], app => app.init());
    </script>
  </div>
<% } %>