package org.exoplatform.addons.gamification.portlets.admin.domain;

import java.io.IOException;

import javax.portlet.*;

public class ManageDomainsPortlet extends GenericPortlet {
  @RenderMode(name = "view")
  public void view(RenderRequest request, RenderResponse response) throws IOException, PortletException {
    PortletRequestDispatcher prDispatcher = getPortletContext().getRequestDispatcher("/jsp/domain/index.jsp");
    prDispatcher.include(request, response);
  }
}
