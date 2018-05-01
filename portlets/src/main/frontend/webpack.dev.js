const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {

    context: path.resolve(__dirname, 'src/apps'),

    entry: {

        common: "./common.js"
    },

    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    },

    devtool: 'inline-source-map',

    devServer: {
        contentBase: './dist'
    }
});