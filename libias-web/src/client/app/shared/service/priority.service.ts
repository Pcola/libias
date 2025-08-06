import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { HttpService } from './http.service';
import { Priority } from '../model/priority/index';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class PriorityService {

  private SERVICE_URL: string = '/priority';

  constructor(
    private httpService: HttpService
  ) { }

  getPriorityItems(): Observable<Priority[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/all', headers);
  }
}
