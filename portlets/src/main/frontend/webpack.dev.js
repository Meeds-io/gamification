const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const exoServerPath = "D:\\home\\\\exo\\\\Documents\\\\Myworkspace\\\\server\\\\plf\\\\platform-5.3.0-M15";


module.exports = merge(common, {

    context: path.resolve(__dirname, 'src/apps'),

    entry: {

        rule: "./rule.js",
        badge: "./badge.js",
        reputation: "./reputation.js",
        leaderboard: "./leaderboard.js",
        GamificationInformations: "./GamificationInformations.js",
        earnpoints: "./earnpoints.js",
        spaceleaderboard: "./spaceleaderboard.js",
    },
    output: {
        path: path.resolve(__dirname, exoServerPath + '/webapps/gamification-portlets/javascript/'),

        //path: path.resolve(__dirname, '../webapp/javascript/'),
        filename: '[name]/[name].bundle.js'

    },

    devtool: 'inline-source-map',
});