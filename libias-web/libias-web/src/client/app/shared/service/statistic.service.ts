import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { SiteStatisticsResponse } from '../model/statistic/index';
import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class StatisticService {

  private SERVICE_URL: string = '/statistic';

  constructor(
    private httpService: HttpService
  ) { }

  getSiteStatistics(): Observable<SiteStatisticsResponse[]> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/sitestatistics', headers);
  }
}
