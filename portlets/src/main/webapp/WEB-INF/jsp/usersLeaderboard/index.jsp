<%@page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@page import="java.util.Locale"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="java.util.ResourceBundle"%>
<%
  ResourceBundle bundle;
  try {
    bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification", request.getLocale());
  } catch (Exception e) {
    bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification", Locale.ENGLISH);
  }
%>
<div class="VuetifyApp">
  <div data-app="true"
    class="v-application v-application--is-ltr theme--light"
    id="usersLeaderboard">
    <div class="flex position-relative v-application--wrap">
      <div role="progressbar" aria-valuemin="0" aria-valuemax="100" class="v-progress-linear v-progress-linear--rounded theme--light app-cached-content ${cacheId}-cached-content" style="height: 1px; position: absolute; z-index: 1">
        <div class="v-progress-linear__indeterminate v-progress-linear__indeterminate--active">
          <div class="v-progress-linear__indeterminate short primary"></div>
        </div>
      </div>
      <div class="pa-3 v-card v-card--flat v-sheet theme--light">
        <div class="UserGamificationHeader text-color d-flex">
          <div class="align-start d-flex">
            <div class="d-inline-block">
              <%=bundle.getString("exoplatform.gamification.userLeaderboard.title")%>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>