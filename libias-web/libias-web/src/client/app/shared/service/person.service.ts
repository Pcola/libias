import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { PersonsRequest, PersonResponse } from '../model/person/index';
import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class PersonService {

  private SERVICE_URL: string = '/person';

  constructor(
    private httpService: HttpService
  ) { }

  getPersons(request: PersonsRequest): Observable<PersonResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    let res = this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/persons', headers);
    return res;
  }
}
