package org.exoplatform.addons.gamification.portlets.earnpoints;

import javax.portlet.*;
import java.io.IOException;

public class EarnPointsPortlet extends GenericPortlet {

    @RenderMode(name = "view")
    public void view(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/earnpoints/index.jsp");
        prDispatcher.include(request, response);
    }
}
