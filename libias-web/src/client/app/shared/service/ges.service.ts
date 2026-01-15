import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';
import { HttpService } from './http.service';
import { ACCEPT_JSON, CONTENT_TYPE_JSON } from '../constants';
import { LIBIAS_REST_URL } from '../config/env.config';
import { GesAnalyseResponse } from '../model/ges/ges-analyse-response.model';
import { GesCompareResponse } from '../model/ges/ges-compare-response.model';

@Injectable()
export class GesService {
  private SERVICE_URL = '/ges';

  constructor(
    private httpService: HttpService,
  ) { }

  analyzeImage(imageBase64: string): Observable<GesAnalyseResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify({ img: imageBase64 });
    return this.httpService.httpPostCall(
      body,
      LIBIAS_REST_URL + this.SERVICE_URL + '/v2/analyse_image',
      headers
    ).do((resp: GesAnalyseResponse) => {
    }).catch((err: any) => {
      return Observable.throw(err);
    });
  }

  compareImages(img1Base64: string, img2Base64: string): Observable<GesCompareResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    let body = JSON.stringify({
      img1: img1Base64,
      img2: img2Base64
    });
    return this.httpService.httpPostCall(
      body,
      LIBIAS_REST_URL + this.SERVICE_URL + '/v2/compare',
      headers
    ).do((resp: GesCompareResponse) => {
    }).catch((err: any) => {
      return Observable.throw(err);
    });
  }

  compareEmbeddings(img1Base64: string, img2Base64: string): Observable<GesCompareResponse> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });

    return Observable.forkJoin(
      this.analyzeImage(img1Base64),
      this.analyzeImage(img2Base64)
    ).switchMap(([resp1, resp2]: [GesAnalyseResponse, GesAnalyseResponse]) => {
      let body = JSON.stringify({
        embedding_0: resp1.embedding,
        embedding_1: resp2.embedding
      });
      return this.httpService.httpPostCall(
        body,
        LIBIAS_REST_URL + this.SERVICE_URL + '/v2/compare',
        headers
      );
    }).catch((err: any) => {
      return Observable.throw(err);
    });
  }

  searchWithEmbedding(embedding: number[], size: number = 1): Observable<any> {
    let headers = new Headers({ 'Accept': ACCEPT_JSON, 'Content-Type': CONTENT_TYPE_JSON });
    const embeddingList = embedding.map(val => val);
    let body = JSON.stringify({
      embedding: embeddingList,
      size: size
    });
    return this.httpService.httpPostCall(
      body,
      LIBIAS_REST_URL + this.SERVICE_URL + '/search_with_embedding',
      headers
    ).do((resp: any) => {
      console.log(' GES searchWithEmbedding response:', resp);
    }).catch((err: any) => {
      console.error(' GES searchWithEmbedding error:', err);
      return Observable.throw(err);
    });
  }
}