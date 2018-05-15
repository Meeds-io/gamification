package org.exoplatform.addons.gamification.portlets.leaderboard;

import javax.portlet.*;
import java.io.IOException;

public class LeaderboardPortlet extends GenericPortlet {

    @RenderMode(name = "view")
    public void Hello(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/leaderboard/index.jsp");
        prDispatcher.include(request, response);
    }
}
