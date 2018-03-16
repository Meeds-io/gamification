package org.exoplatform.addons.gamification.portlets.leaderboard;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.addons.gamification.portlets.common.BaseController;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;

public class LeaderboardController extends BaseController {
    protected static Log log = ExoLogger.getLogger(LeaderboardController.class);

    @Inject
    @Path("index.gtmpl")
    Template indexTemplate;

    @Inject
    PortletPreferences portletPreferences;

    @View
    public Response.Content index() {
        return indexTemplate.with().ok();
    }
}
