/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
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

export function init() {
  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'meedsSetting',
    name: 'meeds',
    image: '/gamification-portlets/images/connector/meeds.svg',
    title: 'Meeds',
    description: 'gamification.admin.meeds.label.description',
    defaultConnector: true,
  });

  // Upcoming Connectors
  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'discordSetting',
    name: 'discord',
    icon: 'fab fa-discord',
    iconColorClass: 'indigo--text',
    title: 'Discord',
    description: 'gamification.admin.discord.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'evmSetting',
    name: 'evm',
    image: '/gamification-portlets/images/connector/EVM.png',
    title: 'EVM Blockhain',
    description: 'gamification.admin.evm.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'linkedInSetting',
    name: 'linkedIn',
    icon: 'fab fa-linkedin',
    iconColorClass: 'light-blue--text text--darken-4',
    title: 'LinkedIn',
    description: 'gamification.admin.linkedIn.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'notionSetting',
    name: 'notion',
    image: '/gamification-portlets/images/connector/notion.svg',
    title: 'Notion',
    description: 'gamification.admin.notion.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'slackSetting',
    name: 'slack',
    icon: 'fab fa-slack',
    iconColorClass: 'purple--text text--darken-4',
    title: 'Slack',
    description: 'gamification.admin.slack.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'twitterSetting',
    name: 'twitter',
    icon: 'fab fa-twitter',
    iconColorClass: 'blue--text text--lighten-1',
    title: 'Twitter',
    description: 'gamification.admin.twitter.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'snapshotSetting',
    name: 'snapshot',
    image: '/gamification-portlets/images/connector/snapshot.png',
    title: 'Snapshot',
    description: 'gamification.admin.snapshot.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'teamsSetting',
    name: 'teams',
    image: '/gamification-portlets/images/connector/teams.png',
    title: 'Teams',
    description: 'gamification.admin.teams.label.description',
    comingSoon: true,
  });

  extensionRegistry.registerComponent('gamification-admin-connector', 'admin-connector-item', {
    id: 'telegramSetting',
    name: 'telegram',
    icon: 'fab fa-telegram',
    iconColorClass: 'blue--text text---darken-1',
    title: 'Telegram',
    description: 'gamification.admin.telegram.label.description',
    comingSoon: true,
  });
}