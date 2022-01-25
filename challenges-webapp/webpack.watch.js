const path = require('path');
const merge = require('webpack-merge');

const webpackProductionConfig = require('./webpack.prod.js');

module.exports = merge(webpackProductionConfig, {
  output: {
    path: '/home/exo/work/server meeds 2022/meeds-community-1.3.x-SNAPSHOT/webapps/challenges/',
    filename: 'js/[name].bundle.js'
  }
});
