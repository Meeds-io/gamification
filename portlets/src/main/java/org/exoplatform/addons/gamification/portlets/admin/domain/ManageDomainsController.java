/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.portlets.admin.domain;

import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.addons.gamification.portlets.common.BaseController;
import org.exoplatform.addons.gamification.service.configuration.DomainService;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainDTO;
import org.exoplatform.addons.gamification.service.dto.configuration.DomainFilter;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityFilterType;
import org.exoplatform.addons.gamification.service.dto.configuration.constant.EntityStatusType;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import juzu.*;
import juzu.request.SecurityContext;
import juzu.template.Template;

public class ManageDomainsController extends BaseController {
  protected static Log log = ExoLogger.getLogger(ManageDomainsController.class);

  @Inject
  @Path("index.gtmpl")
  Template             indexTemplate;

  @Inject
  PortletPreferences   portletPreferences;

  @Inject
  DomainService        domainService;

  @View
  public Response.Content index() {

    return indexTemplate.with().ok();
  }

  @Resource(method = HttpMethod.GET)
  @Ajax
  public Response init(SecurityContext securityContext) throws JSONException {

    String currentUser = securityContext.getRemoteUser();

    if (currentUser == null) {

      return Response.status(401).body("You must login to create new domain");

    }

    DomainFilter domainFilter = new DomainFilter(EntityFilterType.ALL, EntityStatusType.ALL, "");
    // ----Load categories by context
    List<DomainDTO> domains = domainService.getAllDomains(domainFilter, 0, 0);

    JSONArray domainsJson = new JSONArray();
    // --- Build json response
    domains.forEach(elt -> {
      JSONObject json = new JSONObject(elt);
      domainsJson.put(json);

    });

    return Response.ok(domainsJson.toString())
                   .withMimeType("application/json; charset=UTF-8")
                   .withHeader("Cache-Control", "no-cache");
  }
}
