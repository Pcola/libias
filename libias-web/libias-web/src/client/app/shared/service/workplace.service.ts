import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { HttpService } from './http.service';
import { WorkplaceResponse } from '../model/workplace/index';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class WorkplaceService {

  private SERVICE_URL: string = '/workplace';

  constructor(
    private httpService: HttpService
  ) { }

  getWorkplaceItems(): Observable<WorkplaceResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/all', headers);
  }
}
