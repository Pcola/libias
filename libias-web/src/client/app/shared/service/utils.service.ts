import { Injectable } from '@angular/core';
import { Message } from 'primeng/primeng';
import { MessageService } from 'primeng/components/common/messageservice';
import { TranslateService } from 'ng2-translate';
import { LoginService } from './login.service';
import { NotificationService } from './notification.service';
import { PersonResponse } from '../model/person';
import { TableData } from '../model/case/table-data.model';
import { DatePipe } from '@angular/common';

@Injectable()
export class Utils {

  constructor(
    private translate: TranslateService,
    private loginService: LoginService,
    private notificationService: NotificationService,
    private messageService: MessageService,
    private datePipe: DatePipe
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

    if (!personInfo) {
      return data;
    }

    data.push(new TableData('PKZ', personInfo.pkz ? personInfo.pkz.toString() : ''));
    data.push(new TableData('Aktenzeichen', personInfo.fileNumber ? personInfo.fileNumber : ''));
    data.push(new TableData('Antragstyp', personInfo.applicantType ? personInfo.applicantType : ''));
    data.push(new TableData('Familienname', personInfo.lastName ? personInfo.lastName : ''));
    data.push(new TableData('Vorname', personInfo.firstName ? personInfo.firstName : ''));
    
    const birthDateFormatted = personInfo.birthDate ? 
      this.datePipe.transform(new Date(personInfo.birthDate), 'dd.MM.yyyy') : '';
    data.push(new TableData('Geburtsdatum', birthDateFormatted as string));
    
    data.push(new TableData('Geburtsort', personInfo.birthPlace ? personInfo.birthPlace : ''));
    data.push(new TableData('Geburtsland', personInfo.birthCountry ? personInfo.birthCountry : ''));
    data.push(new TableData('Herkunftsland', personInfo.originCountry ? personInfo.originCountry : ''));
    data.push(new TableData('AZR-Nummer', personInfo.azrNumber ? personInfo.azrNumber : ''));
    data.push(new TableData('D-Nummer', personInfo.dNumber ? personInfo.dNumber : ''));
    data.push(new TableData('E-Nummer', personInfo.eNumber ? personInfo.eNumber : ''));
    data.push(new TableData('EURO-DAC Nummer', personInfo.euroDacNumber ? personInfo.euroDacNumber : ''));
    data.push(new TableData('Geschlecht', personInfo.gender ? personInfo.gender : ''));
    data.push(new TableData('Staatsangehörigkeit', personInfo.nationality ? personInfo.nationality : ''));
    data.push(new TableData('Arbeitsplatz', personInfo.workplace ? personInfo.workplace : ''));
    data.push(new TableData('Alter', personInfo.age ? personInfo.age.toString() : ''));
    
    const applicantDateFormatted = personInfo.applicantDate ? 
      this.datePipe.transform(new Date(personInfo.applicantDate), 'dd.MM.yyyy') : '';
    data.push(new TableData('Antragstellerdatum', applicantDateFormatted as string));
    
    const dateModifiedFormatted = personInfo.dateModified ? 
      this.datePipe.transform(new Date(personInfo.dateModified), 'dd.MM.yyyy') : '';
    data.push(new TableData('Zuletzt geändert', dateModifiedFormatted as string));

    return data;
  }
}