
const path = require('path');

module.exports = {
    context: path.resolve(__dirname, 'src/apps'),
    entry: {
        rule: "./rule.js",
        badge: "./badge.js",

        //common: "common.js"
    },
    output: {
        /**
                path: path.resolve(__dirname, 'dist'),
                filename: 'bundle.js'
        */

        path: path.resolve(__dirname, '../webapp/javascript/'),
        filename: '[name]/[name].bundle.js'

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
                use: {
                    loader: 'babel-loader'
                }
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        },
        extensions: ['*', '.js', '.vue', '.json']
    },
}