package org.exoplatform.addons.gamification.portlets.admin.badge;

import javax.portlet.*;
import java.io.IOException;

public class ManageBadgesPortlet extends GenericPortlet {

    @RenderMode(name = "view")
    public void view(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/badge/index.jsp");
        prDispatcher.include(request, response);
    }

}
