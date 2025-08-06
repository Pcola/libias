import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { LoginService } from '../service/index';
import { Account } from '../account/account';
import * as Constants from '../constants';

@Component({
  moduleId: module.id,
  selector: 'toolbar',
  templateUrl: 'toolbar.component.html',
})
export class ToolbarComponent implements OnInit {

  constructor(
    private loginService: LoginService,
    private location: Location,
  ) {
  }

  ngOnInit() {
    // this.fetchNotifications();
  }

  fetchNotifications() {
    if (this.isAuthenticated()) {
      console.log('Fetching notifications (deactivated)');
      // this.utils.fetchNotifications();
    } else {
      console.log('Not fetching notifications');
    }
  }

  logout() {
    this.loginService.logout(true);
  }

  isAuthenticated() {
    return this.location.path().indexOf('/login') < 0 && this.loginService.isAuthenticated();
  }

  get username() {
    let acc: Account = JSON.parse(sessionStorage.getItem(Constants.STORAGE_ACCOUNT_TOKEN));
    return acc.login;
  }

  get hasAdminRole() {
    return this.loginService.isAuthenticated() && this.loginService.isAuthorized(['ADMIN']);
  }

  hasRole(role: string[]) {
    return this.loginService.isAuthenticated() && this.loginService.isAuthorized(role);
  }

}
