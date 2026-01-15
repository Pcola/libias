import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { TrafficLight } from '../model/traffic-light/traffic-light.model';
import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class TrafficLightService {

  private SERVICE_URL: string = '/api/traffic-lights';

  constructor(
    private httpService: HttpService
  ) { }

  getTrafficLights(): Observable<TrafficLight[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL, headers);
  }

  getTrafficLight(roleId: number): Observable<TrafficLight> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/' + roleId, headers);
  }

  createTrafficLight(trafficLight: TrafficLight): Observable<TrafficLight> {
    let headers = new Headers({ 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPostCall(JSON.stringify(trafficLight), LIBIAS_REST_URL + this.SERVICE_URL, headers);
  }

  updateTrafficLight(roleId: number, trafficLight: TrafficLight): Observable<TrafficLight> {
    let headers = new Headers({ 'Content-Type': CONTENT_TYPE_JSON });
    return this.httpService.httpPutCall(JSON.stringify(trafficLight), LIBIAS_REST_URL + this.SERVICE_URL + '/' + roleId, headers);
  }

  deleteTrafficLight(roleId: number): Observable<any> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpDeleteCall(LIBIAS_REST_URL + this.SERVICE_URL + '/' + roleId, headers);
  }
}