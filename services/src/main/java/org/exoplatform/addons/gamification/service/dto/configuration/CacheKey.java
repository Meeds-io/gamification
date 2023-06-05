/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

package org.exoplatform.addons.gamification.service.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheKey implements Serializable {

  private static final long serialVersionUID = 8953827201312560226L;

  private RuleFilter        ruleFilter;

  private ProgramFilter      domainFilter;

  private int               offset;

  private int               limit;

  private Long              id;

  private String            title;

  private long              domainId;

  private Integer           context;

  public CacheKey(Integer context, Long id) {
    this.id = id;
    this.context = context;
  }

  public CacheKey(Integer context, String title) {
    this.title = title;
    this.context = context;
  }

  public CacheKey(Integer context, RuleFilter ruleFilter, int offset, int limit) {
    this.context = context;
    this.ruleFilter = ruleFilter;
    this.offset = offset;
    this.limit = limit;
  }

  public CacheKey(Integer context, ProgramFilter domainFilter, int offset, int limit) {
    this.context = context;
    this.domainFilter = domainFilter;
    this.offset = offset;
    this.limit = limit;
  }

  public CacheKey(Integer context, ProgramFilter domainFilter) {
    this.context = context;
    this.domainFilter = domainFilter;
  }

}
