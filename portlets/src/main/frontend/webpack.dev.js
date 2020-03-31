const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

// add the server path to your server location path
const exoServerPath = "/exo-server";

module.exports = merge(common, {
    output: {
        path: path.resolve(__dirname, exoServerPath + '/webapps/gamification-portlets/javascript/')
    },
    devtool: 'inline-source-map',
});