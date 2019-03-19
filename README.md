gamification-addon 
============
An addon to expose gamification features


Installation
============
Addon can be installed with addon manager : in eXo Platform folder :

    ./addon install exo-gamification 

Overview
============

The goals of development and production builds differ greatly. In development, we want strong source mapping and a localhost server with live reloading or hot module replacement. In production, our goals shift to a focus on minified bundles, lighter weight source maps, and optimized assets to improve load time.



We will write separate webpack configurations for each environment.

We will have 3 configurtions :
* production 
* development 
* common

With the "common" configuration in place, we won’t have to duplicate code within the environment-specific configurations.

In order to merge these configurations together, we’ll use a utility called webpack-merge


Steps
===============
Install webpack-merge

                    $ npm install --save-dev webpack-merge

Why Babel
===============
Needed to transpiler edge JavaScript into plain old ES5 JavaScript that can run in any browser (even the old ones).

babel-core: 
===============
contains the core API.

babel-preset-env: .
===============
automatically determines the Babel plugins and polyfills you need based on your targeted browser or runtime environments.

babel-preset-stage-3: any transforms in stage-x presets are changes to the language that haven’t been approved to be part of a release of Javascript (such as ES6/ES2015).

Install babel required modules
-------------------
npm install --save-dev babel-core babel-preset-env babel-preset-stage-3
===============

Tip: "npm i -D"
-------------------
is a shorter alias for:       
                                                   
                  npm install-save-dev

What is a Babel preset?
-------------------
In Babel, a preset is a set of plugins used to support particular language features.

To configure babel you have to create the following file .babelrc

Why webpack
===============

Installation webpack
-------------------
We will be using the following Webpack libraries:

webpack
-------------------

webpack-cli: 
===============
encapsulates all code related to command line interface handling.

webpack-dev-server:
-------------------
serves a webpack app and updates the browser on changes.

           npm install --save-dev webpack webpack-cli webpack-dev-server
