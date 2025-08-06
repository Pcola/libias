import { Injectable } from '@angular/core';
import { LIBIAS_REST_URL } from '../config/env.config';
import { Observable } from 'rxjs/Observable';
import { Headers } from '@angular/http';
import { HttpService } from './http.service';

@Injectable()
export class NotificationService {

  private SERVICE_URL = LIBIAS_REST_URL + '/notification';

  constructor(private http: HttpService) {
  }

  getNotifications(): Observable<any[]> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.httpGetCall({}, this.SERVICE_URL + '/getAllValid', headers);
  }

  deleteNotification(id: number): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.httpDeleteCall(`${this.SERVICE_URL}/${id}`, headers);
  }

  editNotification(notification: any) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.httpPutCall(notification, `${this.SERVICE_URL}/${notification.id}`, headers);
  }
}
