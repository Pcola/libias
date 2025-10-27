import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { SessionStorageService } from 'ng2-webstorage';
import { Headers } from '@angular/http';

import {
  IncidentRequest, IncidentResponse, IncidentStatusRequest,
  FinishCaseRequest, RelatedCasesResponse, IncidentCountResponse, IncidentFilterResponse
} from '../model/incident/index';

import { HttpService } from './http.service';

import { ACCEPT_JSON, ACCEPT_XML, APPLICATION_PDF, CONTENT_TYPE_JSON, ACCEPT_TEXT_PLAIN } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class IncidentService {

  private SERVICE_URL: string = '/incident';

  constructor(
    private httpService: HttpService,
    private storage: SessionStorageService
  ) { }

  getCountAll(): Observable<number> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/count', headers);
  }

  getIncidents(request: IncidentRequest): Observable<IncidentFilterResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/search', headers);
  }

  getIncident(caseId: number): Observable<IncidentResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/' + caseId, headers);
  }
  getAussenstellerCase(caseId: number): Observable<IncidentResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/aussenstellercase/' + caseId, headers);
  }

  getAussenstellerCases(filter: IncidentRequest): Observable<IncidentFilterResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(filter);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/aussenstellercases', headers);
  }

  getAussenstellerCasesCount(request: IncidentRequest): Observable<number> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/aussenstellercases/count', headers);
  }

  setIncidentStatus(request: IncidentStatusRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPutCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/update', headers);
  }

  finishCase(request: FinishCaseRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPutCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/finish', headers);
  }

  getReferenceTypes(): Observable<string[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/referencetype/all', headers);
  }

  getNationalities(): Observable<string[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/nationality/all', headers);
  }

  getIncidentsCount(request: IncidentRequest): Observable<number> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/search/count', headers);
  }

  getRelatedCases(caseId: number): Observable<RelatedCasesResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/relatedcases/' + caseId, headers);
  }

  getSiteRelatedCases(caseId: number): Observable<RelatedCasesResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/siterelatedcases/' + caseId, headers);
  }

  getIncidentsTypeCount(): Observable<IncidentCountResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/incidentstypecount', headers);
  }

  getNationalitiesSearcher(): Observable<string[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/nationality/searcher', headers);
  }

}
