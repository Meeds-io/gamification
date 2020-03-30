const path = require('path');
const merge = require('webpack-merge');
//const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');
const webpack = require('webpack');

module.exports = merge(common, {
    context: path.resolve(__dirname, 'src/apps'),
    entry: {

        rule: "./rule.js",
        badge: "./badge.js",
        domain:"./domain.js",
        reputation: "./reputation.js",
        leaderboard: "./leaderboard.js",
        GamificationInformations: "./GamificationInformations.js",
        earnpoints: "./earnpoints.js",
        spaceleaderboard: "./spaceleaderboard.js",
        profileStats: "./profileStats/main.js",

    },
    output: {

        path: path.resolve(__dirname, '../webapp/javascript/'),
        filename: '[name]/[name].bundle.js',
        libraryTarget: 'amd'

    },
    plugins: [

        //new UglifyJSPlugin({
        //    sourceMap: true
        //}),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        })
    ]
});