import { join } from 'path';

import { SeedConfig } from './seed.config';
import { ExtendPackages } from './seed.config.interfaces';

/**
 * This class extends the basic seed configuration, allowing for project specific overrides. A few examples can be found
 * below.
 */
export class ProjectConfig extends SeedConfig {

  PROJECT_TASKS_DIR = join(process.cwd(), this.TOOLS_DIR, 'tasks', 'project');

  FONTS_DEST = `${this.APP_DEST}/fonts`;
  FONTS_SRC = ['node_modules/font-awesome/fonts/**'];

  PRIME_NG_THEME = 'omega';
  CSS_IMAGE_DEST = `${this.CSS_DEST}/images`;
  CSS_IMAGE_SRC = [
    'node_modules/primeng/resources/themes/' + this.PRIME_NG_THEME + '/images/**'
  ];

  THEME_FONTS_DEST = `${this.APP_DEST}/css/fonts`;
  THEME_FONTS_SRC = [
    'node_modules/primeng/resources/themes/' + this.PRIME_NG_THEME + '/fonts/**',
  ];

  constructor() {
    super();
    this.APP_TITLE = 'LiBiAs';
    // this.GOOGLE_ANALYTICS_ID = 'Your site's ID';

    /* Enable typeless compiler runs (faster) between typed compiler runs. */
    // this.TYPED_COMPILE_INTERVAL = 5;

    // Add `NPM` third-party libraries to be injected/bundled.
    this.NPM_DEPENDENCIES = [
      ...this.NPM_DEPENDENCIES,
      // {src: 'jquery/dist/jquery.min.js', inject: 'libs'},
      { src: 'lodash/lodash.min.js', inject: 'libs' },
      { src: 'file-saver/FileSaver.min.js', inject: 'libs' },
      { src: 'base-64/base64.js', inject: 'libs' },
      { src: 'primeng/resources/primeng.css', inject: true },
      { src: 'primeng/resources/themes/' + this.PRIME_NG_THEME + '/theme.css', inject: true },
      { src: 'font-awesome/css/font-awesome.min.css', inject: true }
    ];

    // Add `local` third-party libraries to be injected/bundled.
    this.APP_ASSETS = [
      // { src: `${this.APP_SRC}/js/imgcompare.js`, inject: true, vendor: false },
      // { src: `${this.APP_SRC}/js/imgman.js`, inject: true, vendor: false },
      // { src: `${this.APP_SRC}/js/imgcmp.js`, inject: true, vendor: false },
      // { src: `${this.APP_SRC}/js/imginfo.js`, inject: true, vendor: false }
      // {src: `${this.APP_SRC}/your-path-to-lib/libs/jquery-ui.js`, inject: true, vendor: false}
      // {src: `${this.CSS_SRC}/path-to-lib/test-lib.css`, inject: true, vendor: false},
    ];

    this.ROLLUP_INCLUDE_DIR = [
      ...this.ROLLUP_INCLUDE_DIR,
      //'node_modules/moment/**'
    ];

    this.ROLLUP_NAMED_EXPORTS = [
      ...this.ROLLUP_NAMED_EXPORTS,
      //{'node_modules/immutable/dist/immutable.js': [ 'Map' ]},
    ];

    // Add packages (e.g. ng2-translate)
    let additionalPackages: ExtendPackages[] = [
      { name: 'ng2-translate',  path: 'node_modules/ng2-translate/bundles/index.js' },
      { name: 'ng2-webstorage', path: 'node_modules/ng2-webstorage/bundles/core.umd.js' },
      { name: 'ng2-trim-directive', path:'node_modules/ng2-trim-directive/dist/input-trim.directive.js' },
      { name: 'moment', path: 'node_modules/moment/moment.js' },
      { name: 'crypto-js', path: 'node_modules/crypto-js/index.js' },
      { name: 'lodash', path: 'node_modules/lodash/index.js' }
    ];

    this.addPackagesBundles(additionalPackages);

    /* Add proxy middleware */
    if (this.BUILD_TYPE === 'dev') {
      this.PROXY_MIDDLEWARE = [
        require('http-proxy-middleware')('/libias-rest', { ws: true, target: 'http://localhost:18080', changeOrigin: true })
      ];
    }

    /* Add to or override NPM module configurations: */
    // this.PLUGIN_CONFIGS['browser-sync'] = { ghostMode: false };
  }

}
