/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'meeds',
  name: 'meeds',
  image: '/gamification-portlets/images/connector/meeds.svg',
  title: 'Meeds',
  description: 'gamification.admin.meeds.label.description',
  defaultConnector: true,
  rank: 10
});

// Upcoming Connectors
extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'discord',
  name: 'discord',
  icon: 'fab fa-discord',
  iconColorClass: 'indigo--text',
  title: 'Discord',
  description: 'gamification.admin.discord.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'linkedIn',
  name: 'linkedIn',
  icon: 'fab fa-linkedin',
  iconColorClass: 'light-blue--text text--darken-4',
  title: 'LinkedIn',
  description: 'gamification.admin.linkedIn.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'notion',
  name: 'notion',
  image: '/gamification-portlets/images/connector/notion.svg',
  title: 'Notion',
  description: 'gamification.admin.notion.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'slack',
  name: 'slack',
  icon: 'fab fa-slack',
  iconColorClass: 'purple--text text--darken-4',
  title: 'Slack',
  description: 'gamification.admin.slack.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'snapshot',
  name: 'snapshot',
  image: '/gamification-portlets/images/connector/snapshot.png',
  title: 'Snapshot',
  description: 'gamification.admin.snapshot.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'teams',
  name: 'teams',
  image: '/gamification-portlets/images/connector/teams.png',
  title: 'Teams',
  description: 'gamification.admin.teams.label.description',
  comingSoon: true,
});

extensionRegistry.registerExtension('engagementCenterConnectors', 'connector-extensions', {
  id: 'telegram',
  name: 'telegram',
  icon: 'fab fa-telegram',
  iconColorClass: 'blue--text text---darken-1',
  title: 'Telegram',
  description: 'gamification.admin.telegram.label.description',
  comingSoon: true,
});
