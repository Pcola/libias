import { Component, OnInit } from '@angular/core';
import { LoginService, UserService, Utils } from '../shared/service/index';
import { ChangePasswordRequest } from '../shared/model/user/index';
import { Message } from 'primeng/primeng';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR } from '../shared/constants';

@Component({
  moduleId: module.id,
  templateUrl: 'changePassword.component.html',
})
export class ChangePasswordComponent implements OnInit {
  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  passwordRepeat: string;
  busy: boolean = false;
  request: ChangePasswordRequest;

  constructor(
    private userService: UserService,
    private loginService: LoginService,
    private utils: Utils) {
  }

  ngOnInit() {
    this.request = new ChangePasswordRequest();
  }

  changePassword() {
    if (this.passwordRepeat !== this.request.password) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.PasswordsDontMatch');
      return;
    }
    this.userService.changePassword(this.request).subscribe(
      resp => {
        console.log('Password has been updated successfully.');
        this.loginService.logout(true);
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ChangePassword');
        console.log('Error updating user password. ' + err);
      }
    );

  }

}
