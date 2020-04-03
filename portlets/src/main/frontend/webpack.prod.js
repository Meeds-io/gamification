const path = require('path');
const merge = require('webpack-merge');
//const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');
const webpack = require('webpack');

// the display name of the war
const app = 'gamification-portlets';

module.exports = merge(common, {
    output: {
        path: path.resolve(__dirname, '../webapp/javascript/')
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        })
    ]
});