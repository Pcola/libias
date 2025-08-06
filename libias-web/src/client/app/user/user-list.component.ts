import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Message, SelectItem } from 'primeng/primeng';
import { TranslateService } from 'ng2-translate';
import { UserChangeRequest, UserInfoResponse } from '../shared/model/user/index';
import { LoginService, UserService, Utils, WorkplaceService } from '../shared/service/index';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR, ROLE_ADMIN } from '../shared/constants';

@Component({
  moduleId: module.id,
  templateUrl: 'user-list.component.html',
})
export class UserListComponent implements OnInit {
  userChange: UserChangeRequest;
  users: UserInfoResponse[];
  userRoleIds: string[];
  workplaceItems: SelectItem[];
  displayCreateUserDialog: boolean = false;
  displayUpdateUserDialog: boolean = false;
  passwordRepeat: string;
  hasAdminRole: boolean = false;
  msgs: Message[] = [];
  growlLife = GROWL_LIFE;
  submitted: boolean = false;
  selectedWorkplaceId: string;

  constructor(
    private router: Router,
    private userService: UserService,
    private workplaceService: WorkplaceService,
    private utils: Utils,
    private translate: TranslateService,
    private loginService: LoginService) {
  }

  ngOnInit() {

    this.clearModel();

    if (!this.loginService.isAuthenticated()) {
      this.loginService.logout(true);
    } else {
      this.hasAdminRole = this.loginService.isAuthorized([ROLE_ADMIN]);
      if (this.hasAdminRole) {
        this.getWorkplaceInfo();
        this.getUsers();
      } else {
        this.loginService.logout(true);
      }
    }
  }

  /**
   * dialog when creating new user
   */
  showCreateUserDialog() {
    this.userRoleIds = [];
    this.userChange = new UserChangeRequest();
    this.userChange.userRoleIds = [];
    this.userRoleIds.push('3');
    this.passwordRepeat = null;
    this.selectedWorkplaceId = this.workplaceItems[0].value;
    this.displayCreateUserDialog = true;
  }

  /**
   *
   * @param username - username of selected user
   *
   * dialog for changing roles, password, username, password, active flag and workplace of selected user
   */
  showUpdateUserDialog(username: string) {
    this.userRoleIds = [];
    this.passwordRepeat = null;

    for (let u of this.users) {
      if (u.username === username) {
        this.userChange = new UserChangeRequest();
        this.userChange.userRoleIds = [];
        this.userChange.username = username;
        this.userChange.active = ( u.active === 1 ? true : false );

        for (let r of u.userRoleCollection) {
          this.userRoleIds.push(String(r.roleId));
        }

        if (this.utils.isBlank(u.workplaceId)) {
          this.selectedWorkplaceId = this.workplaceItems[0].value;
        } else {
          this.selectedWorkplaceId = u.workplaceId;
        }

        this.displayUpdateUserDialog = true;
        break;
      }
    }
  }

  /**
   * call rest to create new user
   */
  createUser() {

    if (this.passwordRepeat !== this.userChange.password) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.PasswordsDontMatch');
      return;
    }

    if (this.userRoleIds.length === 0) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.SelectRole');
      return;
    }

    this.displayCreateUserDialog = false;
    this.userChange.workplaceId = this.selectedWorkplaceId;
    for (let r of this.userRoleIds) {
      this.userChange.userRoleIds.push(parseInt(r));
    }
    this.userService.createUser(this.userChange).subscribe(
      resp => {
        this.clearModel();
        this.getWorkplaceInfo();
        this.getUsers();
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CreateUser');
        console.log('Error creating new user. ' + err);
      }
    );

  }

  /**
   * call rest to update user
   */
  updateUser() {

    if (this.utils.isNotBlank(this.userChange.password) && this.passwordRepeat !== this.userChange.password) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.PasswordsDontMatch');
      return;
    }

    if (this.userRoleIds.length === 0) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.SelectRole');
      return;
    }

    this.displayUpdateUserDialog = false;
    this.userChange.workplaceId = this.selectedWorkplaceId;
    for (let r of this.userRoleIds) {
      this.userChange.userRoleIds.push(parseInt(r));
    }
    this.userService.updateUser(this.userChange).subscribe(
      resp => {
        this.clearModel();
        this.getWorkplaceInfo();
        this.getUsers();
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.UpdateUser');
        console.log('Error updating user. ' + err);
      }
    );

  }

  /**
   *
   * @param username - of user to disconnect
   *
   * disconnect specific user from app
   */
  disconnectUser(username: string) {

    let rq = new UserChangeRequest();
    rq.username = username;

    this.userService.disconnect(rq).subscribe(
      resp => {
        this.getWorkplaceInfo();
        this.getUsers();
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.DisconnectUser');
        console.log('Error disconnecting user. ' + err);
      }
    );

  }

  isInputInvalid(val: string): boolean {
    return this.submitted && this.utils.isBlank(val);
  }

  public getImageByStatus(status: any): string {
    if (status === 1) {
      return 'Actions-dialog-ok-apply-icon.png';
    }
    return 'Actions-edit-delete-icon.png';
  }

  /**
   * get all users in app
   */
  private getUsers() {
    this.userService.getUsers().subscribe(
      response => {
        this.users = response;
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetUsers');
        console.log('Error getting users. ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  private getWorkplaceInfo() {
    this.workplaceService.getWorkplaceItems().subscribe(
      response => {
        for (let wi of response) {
          this.workplaceItems.push({ label: wi.workplace, value: wi.id });
        }
      },
      err => {
        console.log('Error getting list of workplace: ' + err);
      }
    );
  }

  private clearModel() {
    this.users = [];
    this.userChange = new UserChangeRequest();
    this.userChange.userRoleIds = [];
    this.userRoleIds = [];
    this.workplaceItems = [];
    this.passwordRepeat = null;
    this.selectedWorkplaceId = null;
  }

}
