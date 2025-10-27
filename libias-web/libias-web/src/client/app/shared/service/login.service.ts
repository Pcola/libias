import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpService } from './http.service';
import { UserInfoResponse, UserRole } from '../model/user/index';
import { Account } from '../account/account';
import { AccountEventsService } from '../account/account.events.service';
import { SecurityToken } from '../security/securityToken';
import * as Constants from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';
import { Headers, Http, RequestOptions, Response } from '@angular/http';

@Injectable()
export class LoginService {

  public loggingOff = false;

  constructor(
    private http: Http,
    private accountEventService: AccountEventsService,
    private httpService: HttpService,
    private router: Router) {
  }

  authenticate(username: string, password: string, headers: Headers): Observable<Account> {
    headers.append('Content-Type', 'application/json');

    return this.http.post(LIBIAS_REST_URL + '/login', JSON.stringify({ username: username, password: password }), { headers: headers })
      .map((res: Response) => {
        let securityToken: SecurityToken = new SecurityToken(
          {
            publicSecret: res.headers.get(Constants.HEADER_X_SECRET),
            securityLevel: res.headers.get(Constants.HEADER_WWW_AUTHENTICATE)
          }
        );

        sessionStorage.setItem(Constants.CSRF_CLAIM_HEADER, res.headers.get(Constants.CSRF_CLAIM_HEADER));
        let userInfo: UserInfoResponse = res.json();
        let authorities: string[] = [];
        userInfo.userRoleCollection.forEach((role: UserRole) => {
          authorities.push(role.role);
        });
        if (userInfo.active === 1) {
          authorities.push('LOCAL_USER_MGMT');
        }

        let account: Account = new Account({ login: userInfo.username, authorities: authorities });
        sessionStorage.setItem(Constants.STORAGE_ACCOUNT_TOKEN, JSON.stringify(account));
        sessionStorage.setItem(Constants.STORAGE_SECURITY_TOKEN, JSON.stringify(securityToken));
        this.sendLoginSuccess(account);
        return account;
      }).catch(this.handleError);
  }

  sendLoginSuccess(account?: Account): void {
    if (!account) {
      account = new Account(JSON.parse(sessionStorage.getItem(Constants.STORAGE_ACCOUNT_TOKEN)));
    }
    this.accountEventService.loginSuccess(account);
  }

  isAuthenticated(): boolean {
    return !!sessionStorage.getItem(Constants.STORAGE_ACCOUNT_TOKEN);
  }

  removeAccount(): void {
    sessionStorage.removeItem(Constants.STORAGE_ACCOUNT_TOKEN);
    sessionStorage.removeItem(Constants.STORAGE_SECURITY_TOKEN);
    sessionStorage.removeItem(Constants.CSRF_CLAIM_HEADER);

    // if(sessionStorage.getItem('HTTP_GRP') !== null) {
    //   sessionStorage.removeItem('HTTP_GRP');
    // }
    // if(sessionStorage.getItem('HTTP_CN') !== null) {
    //   sessionStorage.removeItem('HTTP_CN');
    // }
  }

  logout(callServer: boolean = true): void {
    if (callServer) {
      this.loggingOff = true;
      this.callLogOut().subscribe((link : string) => {
        this.loggingOff = false;
        document.location.href = link;
      });
    } else {
      this.removeAccount();
      this.router.navigate(['/login']);
    }
  }

  isAuthorized(roles: Array<string>): boolean {
    let authorized: boolean = false;
    if (this.isAuthenticated() && roles) {
      let account: Account = new Account(JSON.parse(sessionStorage.getItem(Constants.STORAGE_ACCOUNT_TOKEN)));
      if (account && account.authorities) {
        roles.forEach((role: string) => {
          if (account.authorities.indexOf(role) !== -1) {
            authorized = true;
          }
        });
      }
    }
    return authorized;
  }

  private callLogOut(): any {
    let serviceUrl = LIBIAS_REST_URL + '/logout';
    let options = this.httpService.createSecurityHeader(serviceUrl, 'GET', new RequestOptions({headers: new Headers(),withCredentials:true}), null);

    return this.http.get(serviceUrl, options).map((resp: Response) => {
      this.accountEventService.logout(new Account(JSON.parse(sessionStorage.getItem(Constants.STORAGE_ACCOUNT_TOKEN))));
      this.removeAccount();

      return resp.headers.get(Constants.HEADER_LOGOUT_LINK);
    });
  }

  private handleError(error: any) {
    console.error('An error occurred: ' + error);
    return Observable.throw(error || 'Server error');
  }
}
