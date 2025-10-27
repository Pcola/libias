import { Component } from '@angular/core';
import { Config } from './shared/config/env.config';
import './operators';

import { TranslateService } from 'ng2-translate';

const SUPPORTED_LANGS = ['en', 'de'];
const DEFAULT_LANG = 'de';

/**
 * This class represents the main application component.
 */
@Component({
  moduleId: module.id,
  selector: 'bamface-app',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.css'],
})
export class AppComponent {
  constructor(translate: TranslateService) {
    //console.log('Environment config', Config);

    // this language will be used as a fallback when a translation isn't found in the current language
    translate.setDefaultLang(DEFAULT_LANG);
    translate.addLangs(SUPPORTED_LANGS);
    let supportedLang = SUPPORTED_LANGS.indexOf(translate.getBrowserLang());
    if (supportedLang < 0) {
      translate.use(DEFAULT_LANG);
    } else {
      translate.use(translate.getBrowserLang());
    }
  }
}
