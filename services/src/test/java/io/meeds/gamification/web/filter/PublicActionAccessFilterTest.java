
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.service.LayoutService;

import io.meeds.portal.security.constant.UserRegistrationType;
import io.meeds.portal.security.service.SecuritySettingService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicActionAccessFilterTest {

  private static MockedStatic<ExoContainerContext> containerContent;

  @BeforeClass
  public static void initClassContext() {
    containerContent = mockStatic(ExoContainerContext.class);
  }

  @AfterClass
  public static void endClassContext() {
    containerContent.close();
  }

  @Mock
  LayoutService          layoutService;

  @Mock
  SecuritySettingService securitySettingService;

  @Mock
  PortalConfig           portalConfig;

  @Mock
  HttpServletRequest     request;

  @Mock
  HttpServletResponse    response;

  @Mock
  FilterChain            filterChain;

  @Before
  public void setup() {
    containerContent.when(() -> ExoContainerContext.getService(LayoutService.class)).thenReturn(layoutService);
    containerContent.when(() -> ExoContainerContext.getService(SecuritySettingService.class)).thenReturn(securitySettingService);
  }

  @Test
  public void testDoFilterAnonymousUser() throws IOException, ServletException {
    when(request.getRequestURI()).thenReturn("/portal/meeds/overview/contributions/actions/1234");
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(1)).doFilter(request, response);
    
    when(securitySettingService.getRegistrationType()).thenReturn(UserRegistrationType.OPEN);
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(layoutService, times(1)).getPortalConfig(any(SiteKey.class));
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(2)).doFilter(request, response);
    
    when(layoutService.getPortalConfig(SiteKey.portal("public"))).thenReturn(portalConfig);
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(3)).doFilter(request, response);
    
    when(portalConfig.getAccessPermissions()).thenReturn(new String[] {"/platform/users"});
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(4)).doFilter(request, response);
    
    when(portalConfig.getAccessPermissions()).thenReturn(new String[] {UserACL.EVERYONE});
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(response, times(1)).sendRedirect("/portal/public/overview/actions/1234");
    verify(filterChain, times(4)).doFilter(request, response);
  }

  @Test
  public void testDoFilterAuthenticatedUser() throws IOException, ServletException {
    when(request.getRemoteUser()).thenReturn("user");
    when(request.getRequestURI()).thenReturn("/portal/meeds/overview/contributions/actions/1234");
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(1)).doFilter(request, response);

    when(securitySettingService.getRegistrationType()).thenReturn(UserRegistrationType.OPEN);
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(layoutService, never()).getPortalConfig(any(SiteKey.class));
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(2)).doFilter(request, response);

    when(layoutService.getPortalConfig(SiteKey.portal("public"))).thenReturn(portalConfig);
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(layoutService, never()).getPortalConfig(any(SiteKey.class));
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(3)).doFilter(request, response);

    when(portalConfig.getAccessPermissions()).thenReturn(new String[] {"/platform/users"});
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(layoutService, never()).getPortalConfig(any(SiteKey.class));
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(4)).doFilter(request, response);

    when(portalConfig.getAccessPermissions()).thenReturn(new String[] {UserACL.EVERYONE});
    new PublicActionAccessFilter().doFilter(request, response, filterChain);
    verify(layoutService, never()).getPortalConfig(any(SiteKey.class));
    verify(response, never()).sendRedirect(anyString());
    verify(filterChain, times(5)).doFilter(request, response);
  }

}
