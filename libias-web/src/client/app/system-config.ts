//declare var System: SystemJSLoader.System;

System.config(JSON.parse('<%= SYSTEM_CONFIG_DEV %>'));

System.config({
  paths: {
    'npm:': 'node_modules/'
  },
  map: {
    'primeng': 'npm:primeng',
    'ng2-webstorage': 'npm:ng2-webstorage',
    'moment': 'npm:moment',
    'lodash': 'npm:lodash',
    'crypto-js': 'npm:crypto-js',
    'ng2-translate': 'npm:ng2-translate/bundles/ng2-translate.umd.js',
    'ng2-spin-kit': 'npm:ng2-spin-kit',
    'ng2-trim-directive': 'npm:ng2-trim-directive'
  },
  packages: {
    'primeng': { defaultExtension: 'js' },
    'lodash': { main: 'lodash.min.js', defaultExtension: 'js' },
    'crypto-js': { main: 'index.js', defaultExtension: 'js' },
    'moment': { main: 'moment.js', defaultExtension: 'js' },
    'ng2-webstorage': { main: 'bundles/core.umd.js', defaultExtension: 'js' },
    'ng2-spin-kit': { main: 'main.js', defaultExtension: 'js' },
    'ng2-trim-directive': { main: 'dist/input-trim.directive.js', defaultExtension: 'js' }
  }
});
