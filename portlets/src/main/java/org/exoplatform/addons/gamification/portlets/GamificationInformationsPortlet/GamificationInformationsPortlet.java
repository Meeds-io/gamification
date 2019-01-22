package org.exoplatform.addons.gamification.portlets.GamificationInformationsPortlet;

import javax.portlet.*;
import java.io.IOException;

public class GamificationInformationsPortlet extends GenericPortlet {

    @RenderMode(name = "view")
    public void view(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/GamificationInformations/index.jsp");
        prDispatcher.include(request, response);
    }
}
