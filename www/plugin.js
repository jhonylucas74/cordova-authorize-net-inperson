
var exec = require('cordova/exec');

var PLUGIN_NAME = 'AuthozizeNetPlugin';

var AuthozizeNetPlugin = {
  createEMVTransaction: function(options, done, error) {
    exec(done, error, PLUGIN_NAME, 'createEMVTransaction', options);
  },
  createNonEMVTransaction: function(options, done, error) {
    exec(done, error, PLUGIN_NAME, 'createNonEMVTransaction', options);
  }
};

module.exports = AuthozizeNetPlugin;
