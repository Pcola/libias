import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';
import { HttpService } from './http.service';
import {
  CognitecImage,
  VerificationPortraitsRequest, VerificationPortraitsResponse,
  AnalyzePortraitRequest, AnalyzePortraitResponse,
  IdentificationBinningRequest, IdentificationBinningResponse
} from '../model/cognitec/index';


import { ACCEPT_JSON, ACCEPT_XML, APPLICATION_PDF, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class CognitecService {

  private SERVICE_URL = '/cognitec';

  constructor(
    private httpService: HttpService,
  ) { }

  getCountAll(): Observable<number> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/image/count', headers);
  }

  getImage(oid: number): Observable<CognitecImage> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/image/' + oid, headers);
  }

  verificationPortraits(request: VerificationPortraitsRequest): Observable<VerificationPortraitsResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/verificationPortraits', headers);
  }

  analyzePortrait(request: AnalyzePortraitRequest): Observable<AnalyzePortraitResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/analyzePortrait', headers);
  }

  identificationBinning(request: IdentificationBinningRequest): Observable<IdentificationBinningResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify(request);
    return this.httpService.httpPostCall(body, LIBIAS_REST_URL + this.SERVICE_URL + '/identBinning', headers);
  }

}
