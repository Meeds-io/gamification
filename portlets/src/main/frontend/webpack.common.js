const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
/**
const DashboardPlugin = require("webpack-dashboard/plugin");
*/
module.exports = {
    context: path.resolve(__dirname, 'src/apps'),
    entry: {
        rule: "./rule.js",
        badge: "./badge.js",
        domain: "./domain.js",
        reputation: "./reputation.js",
        leaderboard: "./leaderboard.js",
        GamificationInformations: "./GamificationInformations.js",
        earnpoints: "./earnpoints.js",
        spaceleaderboard: "./spaceleaderboard.js",
        profileStats: "./profileStats/main.js",
        popularSpaces: "./popularSpaces/main.js",
        usersLeaderboard: "./usersLeaderboard/main.js"
    },
    output: {
        filename: '[name]/[name].bundle.js',
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
                // 'eslint-loader',
              ]
            },
            {
              test: /\.vue$/,
              use: [
                'vue-loader',
                // 'eslint-loader',
              ]
            }
        ]
    },
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        },
        extensions: ['*', '.js', '.vue', '.json']
    },
};