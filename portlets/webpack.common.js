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
    gamificationCommon: './src/main/webapp/vue-app/commons/main.js',
    badge: './src/main/webapp/vue-app/badge/badge.js',
    profileStats: './src/main/webapp/vue-app/profileStats/main.js',
    myContributions: './src/main/webapp/vue-app/myContributions/main.js',
    popularSpaces: './src/main/webapp/vue-app/popularSpaces/main.js',
    usersLeaderboard: './src/main/webapp/vue-app/usersLeaderboard/main.js',
    badgesOverview: './src/main/webapp/vue-app/badgesOverview/main.js',
    realizationsComponents: './src/main/webapp/vue-app/realizations/initComponents.js',
    ruleComponents: './src/main/webapp/vue-app/rules/initComponents.js',
    realizations: './src/main/webapp/vue-app/realizations/main.js',
    engagementCenter: './src/main/webapp/vue-app/engagement-center/main.js',
    engagementCenterActivityStreamExtension: './src/main/webapp/vue-app/engagement-center-activity-stream-extension/main.js',
    myReputation: './src/main/webapp/vue-app/myReputation/main.js',
    myRewards: './src/main/webapp/vue-app/myRewards/main.js',
    topChallengers: './src/main/webapp/vue-app/topChallengers/main.js',
    challengesOverview: './src/main/webapp/vue-app/challengesOverview/main.js',
    programsOverview: './src/main/webapp/vue-app/programsOverview/main.js',
    gamificationAnalytics: './src/main/webapp/vue-app/analytics/main.js',
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