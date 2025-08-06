import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { UserInfoResponse, UserChangeRequest, ChangePasswordRequest } from '../model/user/index';
import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class UserService {

  private SERVICE_URL: string = '/user';

  constructor(
    private httpService: HttpService
  ) { }

  getUsers(): Observable<UserInfoResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/list', headers);
  }

  getLoggedUserInfo(): Observable<UserInfoResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/loggedUserInfo', headers);
  }

  createUser(request: UserChangeRequest): Observable<UserInfoResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPostCall(JSON.stringify(request), LIBIAS_REST_URL + this.SERVICE_URL + '/create', headers);
  }

  updateUser(request: UserChangeRequest): Observable<UserInfoResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPutCall(JSON.stringify(request), LIBIAS_REST_URL + this.SERVICE_URL + '/update', headers);
  }

  disconnect(request: UserChangeRequest): Observable<UserInfoResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPutCall(JSON.stringify(request), LIBIAS_REST_URL + this.SERVICE_URL + '/disconnect', headers);
  }

  changePassword(request: ChangePasswordRequest): Observable<UserInfoResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPutCall(JSON.stringify(request), LIBIAS_REST_URL + this.SERVICE_URL + '/changePassword', headers);
  }
}
