import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { ReportIdResponse } from '../model/report/reportId-response.model';
import { SearchReportRequest } from '../model/report/search-report-request';
import { SearchBulkReportRequest } from '../model/report/search-bulk-report-request';

import { HttpService } from './http.service';

import { ACCEPT_JSON, APPLICATION_PDF, APPLICATION_XLSX, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class ReportService {

  private SERVICE_URL: string = '/report';

  constructor(
    private httpService: HttpService
  ) { }

  downloadReport(caseId: number): Observable<Blob> {
    let headers = new Headers({ 'Accept': APPLICATION_PDF });
    let serviceUrl = LIBIAS_REST_URL + this.SERVICE_URL + '/create' + '/' + caseId;
    return this.httpService.httpGetBlob(null, serviceUrl, headers, APPLICATION_PDF);
  }

  createComparerExport(comparerReportRequest: SearchReportRequest): Observable<any> {
    let headers = new Headers({ 'Accept': APPLICATION_PDF, 'Content-Type': CONTENT_TYPE_JSON});
    let serviceUrl = LIBIAS_REST_URL + this.SERVICE_URL + '/comparer/create';
    let body = JSON.stringify(comparerReportRequest);
    return this.httpService.httpPostBlob(body, serviceUrl, headers, APPLICATION_PDF);
  }

  createSearchExport(searchReportRequest: SearchReportRequest): Observable<any> {
    let headers = new Headers({ 'Accept': APPLICATION_PDF, 'Content-Type': CONTENT_TYPE_JSON});
    let serviceUrl = LIBIAS_REST_URL + this.SERVICE_URL + '/search/create';
    let body = JSON.stringify(searchReportRequest);
    return this.httpService.httpPostBlob(body, serviceUrl, headers, APPLICATION_PDF);
  }

  createSearchBulkExport(searchBulkReportRequest: SearchBulkReportRequest): Observable<any> {
    let headers = new Headers({ 'Accept': APPLICATION_XLSX, 'Content-Type': CONTENT_TYPE_JSON});
    let serviceUrl = LIBIAS_REST_URL + this.SERVICE_URL + '/search/createBulk';
    let body = JSON.stringify(searchBulkReportRequest);
    return this.httpService.httpPostBlob(body, serviceUrl, headers, APPLICATION_XLSX);
  }

  getReportId(caseId: number): Observable<ReportIdResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    let serviceUrl = LIBIAS_REST_URL + this.SERVICE_URL + '/reportId/' + caseId;
    return this.httpService.httpGetCall(null, serviceUrl, headers);
  }
}
