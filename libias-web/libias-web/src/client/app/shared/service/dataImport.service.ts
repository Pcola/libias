import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { SessionStorageService } from 'ng2-webstorage';
import { Headers } from '@angular/http';

import { JobStatusResponse } from '../model/import/index';
import { HttpService } from './http.service';

import { ACCEPT_JSON, ACCEPT_XML, APPLICATION_PDF, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';
import { WSTestRequest } from '../model/import/wsTest-request.model';

export const JOB_STATUS_NONE = 'NONE';
export const JOB_STATUS_FINISHED = 'FINISHED';
export const JOB_STATUS_RUNNING_MARIS2LIBIAS = 'RUNNINGMARIS2LIBIAS';
export const JOB_STATUS_RUNNING_LIBIAS2COGNITEC = 'RUNNINGLIBIAS2COGNITEC';
export const JOB_STATUS_RUNNING_COGNITEC2LIBIAS = 'RUNNINGCOGNITEC2LIBIAS';
export const JOB_STATUS_RUNNING_DBENROLLMENT = 'RUNNINGDBENROLLMENT';
export const JOB_STATUS_RUNNING_FILTER_BEFORE_DATA_FETCH = 'RUNNINGFILTERBEFOREDATAFETCH';
export const JOB_STATUS_RUNNING_FILTER_AFTER_DATA_FETCH = 'RUNNINGFILTERAFTERDATAFETCH';
export const JOB_STATUS_RUNNING_RESET_FILTER_AFTER_FETCH = 'RUNNINGRESETFILTERAFTERFETCH';
export const JOB_STATUS_RUNNING_UPDATE_INCIDENT_APPLICANT_DATA = 'RUNNINGUPDATEINCIDENTAPPLICANTDATA';
export const JOB_STATUS_RUNNING_FETCH_NEW_INCIDENT_APPLICANT_DATA = 'RUNNINGFETCHNEWINCIDENTAPPLICANTDATA';

@Injectable()
export class DataImportService {

  private SERVICE_URL: string = '/dataImport';

  constructor(
    private httpService: HttpService,
    private storage: SessionStorageService
  ) { }

  startDataImportFull(): Observable<JobStatusResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = '{}';
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/start', headers);
  }

  startDataImportSkipMaris(): Observable<JobStatusResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = '{}';
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/start/skipMaris', headers);
  }

  getJobStatus(): Observable<JobStatusResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let request = null;
    return this.httpService.httpGetCall(request, LIBIAS_REST_URL + this.SERVICE_URL + '/jobStatus', headers);
  }

  getBild(request: WSTestRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/bild', headers);
  }

  getAkte(request: WSTestRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/akte', headers);
  }

  getBildPer(request: WSTestRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/bildPer', headers);
  }

  getAktePer(request: WSTestRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/aktePer', headers);
  }

  getUpdatedApplicants(request: WSTestRequest): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/getUpdatedApplicants', headers);
  }

  getToken(): Observable<void> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/getToken', headers);
  }
}
