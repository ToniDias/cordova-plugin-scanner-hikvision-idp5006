
var exec = require('cordova/exec');

var PLUGIN_NAME = 'ScanPlugin';

var ScanPlugin = {
  register: function(success, error) {
    exec(success, error, PLUGIN_NAME, "register", []);
  },
  openScanner: function() {
    exec(null, null, PLUGIN_NAME, "openScanner", []);
  },
  closeScanner: function() {
    exec(null, null, PLUGIN_NAME, "closeScanner", []);
  },
  startDecode: function(success, error) {
    exec(success, error, "ScanPlugin", "startDecode", []);
  },
  stopDecode: function() {
    exec(null, null, "ScanPlugin", "stopDecode", []);
  },
  start_continue: function(success, error) {
    exec(success, error, "ScanPlugin", "start_continue", []);
  },
  stop_continue: function() {
    exec(null, null, "ScanPlugin", "stop_continue", []);
  }
};

module.exports = ScanPlugin;
