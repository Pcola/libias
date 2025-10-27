import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL, LIBIAS_URL } from '../config/env.config';
import { VersionResponse } from '../model/version/index';

@Injectable()
export class VersionService {

  private SERVICE_URL: string = '/version';

  constructor(
    private httpService: HttpService
  ) { }

  getServerVersion(): Observable<VersionResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/server', headers);
  }

  getClientVersion(): Observable<VersionResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_URL + '/release.json', headers);
  }

}
