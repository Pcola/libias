import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';
import { HttpService } from './http.service';
import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';
import { SearchRequestSearchRequest } from '../model/search-request/search-request-search-request.model';
import { SearchRequestSearchResponse } from '../model/search-request/search-request-search-response.model';

@Injectable()
export class SearchRequestService {

  private SERVICE_URL = '/search-requests';

  constructor(
    private httpService: HttpService
  ) { }

  getById(requestId: number): Observable<any> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/' + requestId, headers);
  }

  search(request: SearchRequestSearchRequest): Observable<SearchRequestSearchResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON});
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/search', headers);
  }
}