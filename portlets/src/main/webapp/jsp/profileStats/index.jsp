<%@page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@page import="java.util.Locale"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.exoplatform.social.webui.Utils"%>
<%@page import="org.exoplatform.social.core.identity.model.Identity" %>
<%@page import="org.exoplatform.social.core.identity.model.Profile" %>
<%
  String profileOwnerId = Utils.getOwnerIdentityId();
  Identity ownerIdentity = Utils.getOwnerIdentity(true);
  Profile profile = ownerIdentity.getProfile();
  boolean isExternal = profile.getProperty(Profile.EXTERNAL) != null && ((String) profile.getProperty(Profile.EXTERNAL)).equals("true");
%>

<% if (!isExternal) {
  ResourceBundle bundle;
  try {
    bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification", request.getLocale());
  } catch (Exception e) {
    bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification", Locale.ENGLISH);
  }
  %>
  <div class="VuetifyApp">
    <div data-app="true"
      class="v-application ms-md-2 v-application--is-ltr theme--light"
      id="profile-stats-portlet" flat="">
      <div class="flex position-relative v-application--wrap">
        <div role="progressbar" aria-valuemin="0" aria-valuemax="100" class="v-progress-linear v-progress-linear--rounded theme--light app-cached-content ${cacheId}-cached-content" style="height: 1px; position: absolute; z-index: 1">
          <div class="v-progress-linear__indeterminate v-progress-linear__indeterminate--active">
            <div class="v-progress-linear__indeterminate short primary"></div>
          </div>
        </div>
        <div class="container pa-0">
          <div class="layout white profileCard row wrap mx-0">
            <div
              class="flex d-flex xs12 sm12 profileFlippedCard profileStats">
              <div class="layout row wrap mx-0">
                <div class="flex d-flex justify-center pt-4 xs12">
                  <div class="v-card v-card--flat v-sheet theme--light">
                    <div tabindex="-1" class="v-list-item theme--light">
                      <a href="/portal/dw/profile">
                        <div
                          class="v-avatar v-list-item__avatar"
                          style="height: 40px; min-width: 40px; width: 40px;">
                          <img src="<%=profile.getAvatarUrl()%>" class="ma-auto object-fit-cover">
                        </div>
                      </a>
                      <div class="v-list-item__content">
                        <div
                          class="v-list-item__title text-uppercase subtitle-1 profile-card-header">
                          <span><%=bundle.getString("homepage.profileStatus.header")%> <%=profile.getProperty("firstName")%></span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="flex d-flex xs12">
                  <div class="layout row mx-0">
                    <div class="flex xs6 d-flex justify-center align-center">
                      <div class="v-card v-card--flat v-sheet theme--light">
                        <a class="white--text">
                          <span class="v-badge badge-color theme--light">
                            <div
                              class="headline text-color font-weight-bold pa-1">
                              <span>-</span>
                            </div>
                          </span>
                        </a>
                        <div class="v-card__text pa-1 subtitle-1 text-color">
                          <span><%=bundle.getString("homepage.profileStatus.spaces")%></span>
                        </div>
                      </div>
                    </div>
                    <div class="flex d-flex xs6 justify-center align-center">
                      <div class="v-card v-card--flat v-sheet theme--light rounded-0">
                        <a class="white--text">
                          <span class="v-badge badge-color theme--light">
                            <div class="headline text-color font-weight-bold pa-1">
                              <span>-</span>
                            </div>
                          </span>
                        </a>
                        <div class="v-card__text pa-1 subtitle-1 text-color">
                          <span><%=bundle.getString("homepage.profileStatus.connections")%></span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="flex d-flex xs12 align-center">
                  <div class="layout row mx-0">
                    <div class="flex xs6 d-flex justify-center align-center">
                      <div class="v-card v-card--flat v-sheet theme--light">
                        <a>
                          <div class="v-card__text headline text-color font-weight-bold pa-1">
                            <span>-</span>
                          </div>
                          <div class="v-card__text pa-1 subtitle-1 text-color">
                            <span><%=bundle.getString("homepage.profileStatus.weeklyPoints")%></span>
                          </div>
                        </a>
                      </div>
                    </div>
                    <div class="flex d-flex xs6 justify-center align-center">
                      <div class="v-card v-card--flat v-sheet theme--light">
                        <a>
                          <div class="v-card__text headline text-color font-weight-bold pa-1">
                            <span>-</span>
                          </div>
                          <div class="v-card__text pa-1 subtitle-1 text-color">
                            <span><%=bundle.getString("homepage.profileStatus.weeklyRank")%></span>
                          </div>
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script type="text/javascript">
      require(['SHARED/profileStatsBundle'], app => app.init());
    </script>
  </div>
<% } %>