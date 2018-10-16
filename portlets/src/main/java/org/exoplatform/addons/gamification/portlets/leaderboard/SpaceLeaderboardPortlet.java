package org.exoplatform.addons.gamification.portlets.leaderboard;

import javax.portlet.*;
import java.io.IOException;

public class SpaceLeaderboardPortlet extends GenericPortlet {
    @RenderMode(name = "view")
    public void view(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/leaderboard/space/index.jsp");
        prDispatcher.include(request, response);
    }
}
