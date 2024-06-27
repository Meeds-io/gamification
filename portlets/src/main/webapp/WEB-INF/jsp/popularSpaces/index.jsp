<%
/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
%>
<%@page import="org.exoplatform.services.resources.ResourceBundleService"%>
<%@page import="java.util.Locale"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="java.util.ResourceBundle"%>
<%
  ResourceBundle bundle;
  try {
  	bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification",
  			request.getLocale());
  } catch (Exception e) {
  	bundle = ExoContainerContext.getService(ResourceBundleService.class).getResourceBundle("locale.addon.Gamification",
  			Locale.ENGLISH);
  }
%>
<div class="VuetifyApp">
  <div data-app="true"
    class="v-application v-application--is-ltr theme--light"
    id="popularSpacesApplication">
    <div class="flex position-relative v-application--wrap">
      <div role="progressbar" aria-valuemin="0" aria-valuemax="100" class="v-progress-linear v-progress-linear--rounded theme--light app-cached-content ${cacheId}-cached-content" style="height: 1px; position: absolute; z-index: 1">
        <div class="v-progress-linear__indeterminate v-progress-linear__indeterminate--active">
          <div class="v-progress-linear__indeterminate short primary"></div>
        </div>
      </div>
      <div class="v-card v-card--flat v-sheet theme--light">
        <div class="v-card__title text-header pb-2">
          <%=bundle.getString("popularSpaces.title")%>
        </div>
      </div>
    </div>
  </div>
</div>