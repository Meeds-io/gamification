/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.web.filter;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.web.filter.Filter;

import io.meeds.portal.security.constant.UserRegistrationType;
import io.meeds.portal.security.service.SecuritySettingService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PublicActionAccessFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request,
                       ServletResponse response,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    String requestURI = req.getRequestURI();

    SecuritySettingService securitySettingService = ExoContainerContext.getService(SecuritySettingService.class);
    if (req.getRemoteUser() == null
        && securitySettingService.getRegistrationType() == UserRegistrationType.OPEN
        && !StringUtils.contains(requestURI, "/portal/public")) {
      LayoutService layoutService = ExoContainerContext.getService(LayoutService.class);
      PortalConfig portalConfig = layoutService.getPortalConfig(SiteKey.portal("public"));
      if (portalConfig != null
          && portalConfig.getAccessPermissions() != null
          && StringUtils.equals(UserACL.EVERYONE, portalConfig.getAccessPermissions()[0])) {
        res.sendRedirect(requestURI.replaceFirst("/portal/(.*)/contributions/actions",
                                                 "/portal/public/overview/actions"));
        return;
      }
    }
    filterChain.doFilter(req, res);
  }

}
