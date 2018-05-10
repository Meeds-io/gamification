package org.exoplatform.addons.gamification.portlets.profile.reputation;

import javax.portlet.*;
import java.io.IOException;

public class UserReputationPortlet extends GenericPortlet {

    @RenderMode(name = "view")
    public void Hello(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/reputation/index.jsp");
        prDispatcher.include(request, response);
    }
}
