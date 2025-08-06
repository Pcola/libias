import { Component, OnInit } from '@angular/core';
import { LoginService, Utils, VersionService } from '../shared/service/index';
import { Message } from 'primeng/primeng';

import { GROWL_LIFE, GROWL_SEVERITY_ERROR } from '../shared/constants';

@Component({
  moduleId: module.id,
  templateUrl: 'about.component.html',
})
export class AboutComponent implements OnInit {

  serverVersion: string = '';
  clientVersion: string = '';

  msgs: Message[] = [];
  growlLife = GROWL_LIFE;

  constructor(
    private utils: Utils,
    private loginService: LoginService,
    private versionService: VersionService
  ) { }

  ngOnInit() {
    if (!this.loginService.isAuthenticated()) {
      this.loginService.logout(true);
    }

    this.getClientVersion();
    this.getServerVersion();

  }

  private getServerVersion() {
    this.versionService.getServerVersion().subscribe(
      resp => {
        this.serverVersion = resp.version;
      },
      err => {
        console.error('Cannot get server version.' + err);
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetVersion');
      }
    );
  }

  private getClientVersion() {
    this.versionService.getClientVersion().subscribe(
      resp => {
        this.clientVersion = resp.version;
      },
      err => {
        console.error('Cannot get client version.' + err);
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetVersion');
      }
    );
  }

}
