import { Injectable } from '@angular/core';
import { Message } from 'primeng/primeng';
import { MessageService } from 'primeng/components/common/messageservice';
import { TranslateService } from 'ng2-translate';
import { LoginService } from './login.service';
import { NotificationService } from './notification.service';
import { PersonResponse } from '../model/person';
import { TableData } from '../model/case/table-data.model';

@Injectable()
export class Utils {

  constructor(
    private translate: TranslateService,
    private loginService: LoginService,
    private notificationService: NotificationService,
    private messageService: MessageService
  ) {
  }

  isBlank(s: any) {
    return s === undefined || s === null || s === 'undefined' || s === 'null' || s === '';
  }

  isNotBlank(s: any) {
    return !this.isBlank(s);
  }

  objectToUriParams(obj: any): string {
    let str = Object.keys(obj).map(function(key) {
      return encodeURIComponent(key) + '=' + encodeURIComponent(obj[key]);
    }).join('&');
    return str;
  }

  string2Hex(s: string): string {
    var str = '';
    for (var i = 0; i < s.length; i++) {
      str += s[i].charCodeAt(0).toString(16);
    }
    return str;
  }

  isValidJSON(s: string): boolean {
    try {
      JSON.parse(s);
      return true;
    } catch (e) {
      return false;
    }
  }

  nullToUndefined(s: any) {
    return s === null ? undefined : s;
  }

  defaultIfBlank(s: any, def: any) {
    return this.isBlank(s) ? def : s;
  }

  floorFigure(figure: any, decimals: number) {
    var re = new RegExp('^-?\\d+(?:\.\\d{0,' + (decimals || -1) + '})?');
    return parseFloat(figure.toString().match(re)[0]).toFixed(decimals);
  }

  fetchNotifications() {
    this.notificationService.getNotifications().subscribe(
      notifications => {
        notifications.forEach((notification: any) => {
          if (notification.text) {
            this.messageService.add({ severity: 'info', summary: 'Info', detail: notification.text });
          }
        });
      },
      err => {
        console.error('Error fetching notifications: ' + err);
      }
    );
  }

  showGrowl(msgs: Message[], severity: string, title: string, msg: string, translateMsg?: boolean) {
    this.translate.get(title).subscribe(t => {
      if (translateMsg === undefined || translateMsg) {
        this.translate.get(msg).subscribe(m => {
          msgs.push({ severity: severity, summary: t, detail: m });
        });
      } else {
        msgs.push({ severity: severity, summary: t, detail: msg });
      }
    });
  }

  isErrorForbidden(error: any) {
    if (error.status === 403) {
      this.loginService.logout(true);
    }
  }

  containsNullOnly(list: any) {
    for (let item of list) {
      if (item !== null) {
        return false;
      }
    }
    return true;
  }

  trimObjectAttibutes(obj: any) {
    if (obj !== null && obj !== undefined) {
      for (let key of Object.keys(obj)) {
        if (obj[key] !== null && obj[key] !== undefined) {
          if (typeof obj[key] === 'string') {
            obj[key] = obj[key].replace(/^\s+|\s+$/gm,'');
          } else if (typeof obj[key] === 'number') {
            obj[key] = parseInt(obj[key].toString().replace(/^\s+|\s+$/gm,''));
          } else {
            obj[key] = obj[key].toString().replace(/^\s+|\s+$/gm,'');
          }
          obj[key] = obj[key] === '' ? null : obj[key];
        }
      }
    }
  }

  constructInfoTable(personInfo: PersonResponse): TableData[] {
    const data: TableData[] = [];

    // data.push(new TableData('Anwendung',  personInfo ? personInfo.application : 'Lokaler speicher'));
    // data.push(new TableData('Rechtsgrund', personInfo ? personInfo.reason : ''));
    // data.push(new TableData('EDV-Zahl', personInfo ? personInfo.edvNumber : ''));
    // data.push(new TableData('Lichtbildnummer', personInfo && personInfo.photoNumber ? personInfo.photoNumber.toString() : ''));
    // data.push(new TableData('Bilddatum', personInfo && personInfo.photoDate ? personInfo.photoDate : ''));
    // data.push(new TableData('ED-Datum', personInfo && personInfo.edDate ? personInfo.edDate : ''));
    // data.push(new TableData('AFIS-Zahl', personInfo && personInfo.afisNumber ? personInfo.afisNumber.toString() : ''));

    data.push(new TableData('Antragsteller', personInfo ? personInfo.applicantType : ''));
    data.push(new TableData('Nachname', personInfo ? personInfo.lastName : ''));
    data.push(new TableData('Vorname', personInfo ? personInfo.firstName : ''));
    data.push(new TableData('Geburtsdatum', personInfo && personInfo.birthDate ? personInfo.birthDate.toString() : ''));
    data.push(new TableData('Geburtsland', personInfo ? personInfo.birthCountry : ''));
    data.push(new TableData('Geburtsort', personInfo ? personInfo.birthPlace : ''));
    data.push(new TableData('Herkunftsland', personInfo ? personInfo.originCountry : ''));
    data.push(new TableData('Antragstellerdatum', personInfo && personInfo.applicantDate ? personInfo.applicantDate.toString() : ''));
    data.push(new TableData('Arbeitsplatz', personInfo ? personInfo.workplace : ''));

    return data;
  }
}
