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
const path = require('path');
const ESLintPlugin = require('eslint-webpack-plugin');
const { VueLoaderPlugin } = require('vue-loader')

module.exports = {
  context: path.resolve(__dirname, '.'),
  entry: {
    engagementCenterPrograms: './src/main/webapp/vue-app/engagement-center-programs/main.js',
    engagementCenterActions: './src/main/webapp/vue-app/engagement-center-actions/main.js',
    engagementCenterAchievements: './src/main/webapp/vue-app/engagement-center-achievements/main.js',
    activityStreamExtension: './src/main/webapp/vue-app/activity-stream-extension/main.js',
    favoriteDrawerExtension: './src/main/webapp/vue-app/favorite-drawer-extension/main.js',
    notificationExtension: './src/main/webapp/vue-app/notification-extension/main.js',
    ruleComponents: './src/main/webapp/vue-app/rules/main.js',
    ruleExtensions: './src/main/webapp/vue-app/rules-extensions/main.js',
    ruleSearch: './src/main/webapp/vue-app/rules-search/main.js',
    gamificationCommon: './src/main/webapp/vue-app/commons/main.js',
    gamificationAnalytics: './src/main/webapp/vue-app/analytics/main.js',
    badge: './src/main/webapp/vue-app/badge/badge.js',
    profileStats: './src/main/webapp/vue-app/profileStats/main.js',
    myContributions: './src/main/webapp/vue-app/myContributions/main.js',
    popularSpaces: './src/main/webapp/vue-app/popularSpaces/main.js',
    usersLeaderboard: './src/main/webapp/vue-app/usersLeaderboard/main.js',
    badgesOverview: './src/main/webapp/vue-app/badgesOverview/main.js',
    myReputation: './src/main/webapp/vue-app/myReputation/main.js',
    myRewards: './src/main/webapp/vue-app/myRewards/main.js',
    topChallengers: './src/main/webapp/vue-app/topChallengers/main.js',
    rulesOverview: './src/main/webapp/vue-app/rulesOverview/main.js',
    rulesOverviewWidget: './src/main/webapp/vue-app/rulesOverviewWidget/main.js',
    programsOverview: './src/main/webapp/vue-app/programsOverview/main.js',
    connectorUserProfile: './src/main/webapp/vue-app/connector-user-profile/main.js',
    connectorUserSettings: './src/main/webapp/vue-app/connector-user-settings/main.js',
    connectorAdminSettings: './src/main/webapp/vue-app/connector-admin-settings/main.js',
  },
  plugins: [
    new ESLintPlugin({
      files: [
        './src/main/webapp/vue-app/*.js',
        './src/main/webapp/vue-app/*.vue',
        './src/main/webapp/vue-app/**/*.js',
        './src/main/webapp/vue-app/**/*.vue',
      ],
    }),
    new VueLoaderPlugin()
  ],
  output: {
    filename: 'js/[name].bundle.js',
    libraryTarget: 'amd'
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          'vue-style-loader',
          'css-loader'
        ],
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [
          'babel-loader',
        ]
      },
      {
        test: /\.vue$/,
        use: [
          'vue-loader',
        ]
      }
    ]
  },
};