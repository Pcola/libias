
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';

import { Image } from '../model/image/index';
import { HttpService } from './http.service';

import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';

@Injectable()
export class ImageService {

  private SERVICE_URL: string = '/image';

  constructor(
    private httpService: HttpService
  ) { }

  getImage(oid: number): Observable<Image> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON });
    return this.httpService.httpGetCall(null, LIBIAS_REST_URL + this.SERVICE_URL + '/image/' + oid, headers);
  }
}
