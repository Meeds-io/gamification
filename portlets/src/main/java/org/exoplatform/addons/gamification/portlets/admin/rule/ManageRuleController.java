package org.exoplatform.addons.gamification.portlets.admin.rule;

import juzu.*;
import juzu.request.SecurityContext;
import juzu.template.Template;
import org.exoplatform.addons.gamification.portlets.common.BaseController;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.gamification.entities.domain.configuration.RuleEntity;
import org.exoplatform.gamification.service.configuration.RuleService;
import org.exoplatform.gamification.service.dto.configuration.RuleDTO;
import org.exoplatform.gamification.storage.dao.RuleDAO;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import java.util.List;

public class ManageRuleController extends BaseController {
    protected static Log log = ExoLogger.getLogger(ManageRuleController.class);

    @Inject
    @Path("index.gtmpl")
    Template indexTemplate;

    @Inject
    PortletPreferences portletPreferences;

    @Inject
    RuleService ruleService;

    @View
    public Response.Content index() {

        return indexTemplate.with().ok();
    }

    @Resource(method = HttpMethod.GET)
    @Ajax
    public Response init(SecurityContext securityContext) throws JSONException {

        String currentUser = securityContext.getRemoteUser();

        if (currentUser == null) {

            return Response.status(401).body("You must login to create new category");

        }

        //----Load categories by context
        List<RuleDTO> rules = ruleService.getAllRules();

        JSONArray rulesJson = new JSONArray();
        //--- Build json response
        rules.forEach(elt -> {
            JSONObject json = new JSONObject(elt);
            rulesJson.put(json);

        });

        return Response.ok(rulesJson.toString()).withMimeType("application/json; charset=UTF-8").withHeader("Cache-Control", "no-cache");
    }
}
