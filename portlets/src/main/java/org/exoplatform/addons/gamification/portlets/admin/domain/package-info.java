/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

@Application(defaultController = ManageDomainsController.class)
@Portlet

@Stylesheets({
    @Stylesheet(value = "/org/exoplatform/addons/gamification/portlets/admin/domain/assets/global.css", location = AssetLocation.APPLICATION, id = "global") }

)
@Bindings({ @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
    @Binding(value = DomainService.class),

})

@Less(value = { "global.less" }, minify = true)
@Assets("*")
package org.exoplatform.addons.gamification.portlets.admin.domain;

import org.exoplatform.addons.gamification.service.configuration.DomainService;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.less.Less;
import juzu.plugin.portlet.Portlet;
