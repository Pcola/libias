import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response, URLSearchParams, ResponseContentType, RequestOptionsArgs } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import * as Constants from '../constants';
import { SecurityToken } from '../security/securityToken';
import * as CryptoJS from 'crypto-js';
import { Config } from '../config/env.config';

@Injectable()
export class HttpService {

  constructor(
    private http: Http
  ) { }

  httpGetCall(request: any, serviceUrl: string, headers: Headers): any {
    let options = new RequestOptions({ headers: headers, search: new URLSearchParams(this.objectToUriParams(request)), withCredentials: true });
    this.addSecurityHeader(serviceUrl, 'GET', options, null);
    return this.http
      .get(serviceUrl, options)
      .map(this.extractData)
      .catch(this.handleError);
  }

  httpPostCall(body: string, serviceUrl: string, headers: Headers): any {
    let options = new RequestOptions({ headers: headers, withCredentials: true });
    this.addSecurityHeader(serviceUrl, 'POST', options, body);
    return this.http
      .post(serviceUrl, body, options)
      .map(this.extractData)
      .catch(this.handleError);
  }

  httpPutCall(body: string, serviceUrl: string, headers: Headers): any {
    let options = new RequestOptions({ headers: headers, withCredentials: true });
    this.addSecurityHeader(serviceUrl, 'PUT', options, body);
    return this.http
      .put(serviceUrl, body, options)
      .map(this.extractData)
      .catch(this.handleError);
  }

  httpPostBlob(body: string, serviceUrl: string, headers: Headers, applicationType: string): any {
      let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob, withCredentials: true });
      this.addSecurityHeader(serviceUrl, 'POST', options, body);
      return this.http
      .post(serviceUrl, body, options)
      .map(response => {
         return new Blob([response.blob()], { type: applicationType });
      }).catch(this.handleError);
  }

  httpGetBlob(request: any, serviceUrl: string, headers: Headers, applicationType: string): any {
    let options = new RequestOptions({ headers: headers, search: new URLSearchParams(this.objectToUriParams(request)), responseType: ResponseContentType.Blob, withCredentials: true });
    this.addSecurityHeader(serviceUrl, 'GET', options, null);
    return this.http.get(serviceUrl, options)
      .map(res => {
        return new Blob([res.blob()], { type: applicationType });
      }).catch(this.handleError);
  }

  httpDeleteCall(serviceUrl: string, headers: Headers): any {
    let options = new RequestOptions({ headers: headers, withCredentials: true });
    this.addSecurityHeader(serviceUrl, 'DELETE', options, null);
    return this.http
      .delete(serviceUrl, options)
      .map(this.extractData)
      .catch(this.handleError);
  }

  public createSecurityHeader(url: string, method: string, options: RequestOptionsArgs, body: any): RequestOptionsArgs {
    let newOptions: RequestOptionsArgs = options;
    this.addSecurityHeader(url, method, newOptions, body);
    return newOptions;
  }

  private handleError(error: any) {
    console.error('An error occurred: ' + error);
    return Observable.throw(error || 'Server error');
  }

  private extractData(res: Response) {
    let body: any;
    try {
      if (res.text() !== '') {
        if (res.headers.get('Content-Type').toString().indexOf('application/json') >= 0) {
          body = res.json();
        } else {
          body = res.text();
        }
      }
    } catch (e) {
      console.error('Unable to extract JSON body: ' + e);
      body = res.text();
    }
    let response = body || {};
    return response;
  }

  private objectToUriParams(obj: any): string {
    if (obj === undefined || obj === null) {
      return '';
    }
    let str = Object.keys(obj).map(function(key) {
      return encodeURIComponent(key) + '=' + encodeURIComponent(obj[key]);
    }).join('&');
    return str;
  }

  private addSecurityHeader(url: string, method: string, options: RequestOptionsArgs, body: any): void {
    /*
    if (Config.ENV === 'DEV') {
      url = 'http://localhost:18080' + url;
    }
    */
    url = url.replace(window.location.origin, '');
    //console.log('Url:', url);

    let securityToken: SecurityToken = new SecurityToken(JSON.parse(sessionStorage.getItem(Constants.STORAGE_SECURITY_TOKEN)));
    let date: string = new Date().toISOString();
    let secret: string = securityToken.publicSecret;

    let message = '';
    if (method === 'PUT' || method === 'POST' || method === 'PATCH') {
      message = method + body + url + date;
    } else {
      message = method + url + date;
    }
    options.headers.set(Constants.CSRF_CLAIM_HEADER, sessionStorage.getItem(Constants.CSRF_CLAIM_HEADER));

    if (securityToken.isEncoding('HmacSHA256')) {
      options.headers.set(Constants.HEADER_X_DIGEST, CryptoJS.HmacSHA256(message, secret).toString());
    } else if (securityToken.isEncoding('HmacSHA1')) {
      options.headers.set(Constants.HEADER_X_DIGEST, CryptoJS.HmacSHA1(message, secret).toString());
    } else if (securityToken.isEncoding('HmacMD5')) {
      options.headers.set(Constants.HEADER_X_DIGEST, CryptoJS.HmacMD5(message, secret).toString());
    }
    options.headers.set(Constants.HEADER_X_ONCE, date);

    this.setDataFromWebgate(options);
    /*
        if (Config.ENV === 'DEV') {
          console.log('Url:', url);
          console.log('Message:', message);
          console.log('Secret:', secret);
          console.log('HMAC message:', options.headers.get(Constants.HEADER_X_DIGEST));
        }
    */
  }

  private setDataFromWebgate(options: RequestOptionsArgs) {
    // if(sessionStorage.getItem('HTTP_GRP') !== null) {
    //   options.headers.set('HTTP_GRP', sessionStorage.getItem('HTTP_GRP'));
    // }
    // if(sessionStorage.getItem('HTTP_CN') !== null) {
    //   options.headers.set('HTTP_CN', sessionStorage.getItem('HTTP_CN'));
    // }
  }

}
