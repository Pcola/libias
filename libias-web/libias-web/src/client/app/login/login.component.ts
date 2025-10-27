import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService, Utils } from '../shared/service/index';
import { Account } from '../shared/account/account';
import { AccountEventsService } from '../shared/account/account.events.service';
import { Message } from 'primeng/primeng';
import { SessionStorageService } from 'ng2-webstorage';

import { GROWL_LIFE, GROWL_SEVERITY_ERROR, STORAGE_INCIDENT_PAGE, STORAGE_INCIDENT_PKZ, STORAGE_INCIDENT_REQUEST } from '../shared/constants';
import { Headers } from '@angular/http';

@Component({
  moduleId: module.id,
  providers: [AccountEventsService],
  templateUrl: 'login.component.html',
})
export class LoginComponent implements OnInit {
  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  username: string = '';
  password: string = '';
  //webgateSimulation: boolean;
  account: Account;
  busy: boolean = true;
  canDisplay: boolean = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private accountEventService: AccountEventsService,
    private storage: SessionStorageService,
    private utils: Utils) {
  }

  ngOnInit() {
    this.username = '';
    this.password = '';

    this.accountEventService.subscribe((account) => {
      this.busy = false;
      this.canDisplay = true;
      if (!account.authenticated) {
        this.password = '';
        if (account.error) {
          if (account.error.indexOf('BadCredentialsException') !== -1) {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.Login');
          } else {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', account.error, false);
          }
        }
      }
    });

    if(!this.loginService.loggingOff) {
      this.callServer();
    }
  }

  authenticate(event: any) {
    event.preventDefault();
    this.callServer();
  }

  /**
   * call server to authenticate based on credentials form user input
   * if credentials match, it will navigate to list of incidents
   */
  private callServer() {
    this.busy = true;
    let header = new Headers();

    // if(this.webgateSimulation) {
    //   this.username = '';
    //   this.password = '';
    //   header = this.getSimulationHeaders();
    //   sessionStorage.setItem('HTTP_GRP', header.get('HTTP_GRP'));
    //   sessionStorage.setItem('HTTP_CN', header.get('HTTP_CN'));
    // }

    this.loginService.authenticate(this.username, this.password, header).subscribe(account => {
      this.account = account;
      this.busy = false;
      this.clearStorage();
      //console.log('Successfully logged in.', account);
      this.utils.fetchNotifications();
      this.router.navigate(['/incident-list']);
    }, err => {
      this.busy = false;
      this.canDisplay = true;
      this.password = '';
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.Login');
    });
  }

  private clearStorage() {
    this.storage.clear(STORAGE_INCIDENT_PKZ);
    this.storage.clear(STORAGE_INCIDENT_REQUEST);
    this.storage.clear(STORAGE_INCIDENT_PAGE);
  }

  // just for simulation
  // private getSimulationHeaders(): Headers {
  //   let header = new Headers();
  //   header.set('HTTP_SN', 'HOST');
  //   header.set('HTTP_GIVENNAME', 'LOCAL');
  //   let idm_role: string = 'Administrator:Supervisor:Benutzer:Sucher:Vergleicher:AS_Berlin:AS_Augsburg:AS_Dortmund';
  //   header.set('HTTP_GRP', idm_role);
  //   header.set('HTTP_CN', 'skuska');
  //   return header;
  // }
}
