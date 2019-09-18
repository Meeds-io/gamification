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

    // ----Load categories by context
    List<DomainDTO> domains = domainService.getAllDomains();

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
