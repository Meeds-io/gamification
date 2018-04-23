package org.exoplatform.addons.gamification.portlets.admin.rule;

import javax.portlet.*;
import java.io.IOException;

public class ManageRulesPortlet extends GenericPortlet {
    @RenderMode(name = "view")
    public void Hello(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/rule/index.jsp");
        prDispatcher.include(request, response);
    }
}
