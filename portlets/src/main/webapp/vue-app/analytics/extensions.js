/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2023 Meeds Association contact@meeds.io
 *
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
extensionRegistry.registerExtension('AnalyticsSamples', 'SampleItem', {
  type: 'program',
  options: {
    // Rank of executing 'match' method
    rank: 50,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-program-sample-item-attribute'],
    match: fieldName => fieldName === 'programId',
  },
});

extensionRegistry.registerExtension('AnalyticsSamples', 'SampleItem', {
  type: 'programOwners',
  options: {
    // Rank of executing 'match' method
    rank: 60,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-profile-list-sample-item-attribute'],
    match: fieldName => fieldName === 'programOwners',
  },
});

extensionRegistry.registerExtension('AnalyticsSamples', 'SampleItem', {
  type: 'rule',
  options: {
    // Rank of executing 'match' method
    rank: 60,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-rule-sample-item-attribute'],
    match: fieldName => fieldName === 'ruleId',
  },
});

extensionRegistry.registerExtension('AnalyticsSamples', 'SampleItem', {
  type: 'spaceId',
  options: {
    // Rank of executing 'match' method
    rank: 10,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-profile-sample-item-attribute'],
    match: fieldName => (fieldName === 'spaceId'),
    options: {
      isSpace: true,
    },
  },
});

extensionRegistry.registerExtension('AnalyticsTable', 'CellValue', {
  type: 'program',
  options: {
    // Rank of executing 'match' method
    rank: 50,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-table-cell-program-value'],
    // Method complete signature : match: (fieldName, aggregationType, fieldDataType, item) => { ... }
    match: (fieldName, aggregationType) => fieldName === 'programId' && aggregationType === 'TERMS',
  },
});

extensionRegistry.registerExtension('AnalyticsTable', 'CellValue', {
  type: 'rule',
  options: {
    // Rank of executing 'match' method
    rank: 60,
    // Used Vue component to display cell value
    vueComponent: Vue.options.components['analytics-table-cell-rule-value'],
    // Method complete signature : match: (fieldName, aggregationType, fieldDataType, item) => { ... }
    match: (fieldName, aggregationType) => fieldName === 'ruleId' && aggregationType === 'TERMS',
  },
});
