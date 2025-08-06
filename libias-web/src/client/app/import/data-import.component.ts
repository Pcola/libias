import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataImportService, LoginService, Utils } from '../shared/service/index';
import { Message } from 'primeng/primeng';
import * as moment from 'moment';
import { JobStatusResponse } from '../shared/model/import/index';

import { GROWL_LIFE, GROWL_SEVERITY_ERROR, GROWL_SEVERITY_INFO, ROLE_ADMIN } from '../shared/constants';

import * as DATA_IMPORT_CONSTANTS from '../shared/service/dataImport.service';
import { WSTestRequest } from '../shared/model/import/wsTest-request.model';

@Component({
  moduleId: module.id,
  templateUrl: 'data-import.component.html',
})
export class DataImportComponent implements OnInit {

  busy: boolean = false;
  msgs: Message[] = [];
  growlLife = GROWL_LIFE;
  hasAdminRole: boolean = false;

  dataImportStatus: boolean = false;
  enrollmentStatus: boolean = false;
  identificationStatus: boolean = false;

  dataImportFinished: boolean = false;
  enrollmentFinished: boolean = false;
  dataIdentificationFinished: boolean = false;

  dataImportRunning: boolean = false;
  enrollmentRunning: boolean = false;
  dataIdentificationRunning: boolean = false;

  jobId: string;
  jobStatus: JobStatusResponse;

  request: WSTestRequest;

  constructor(
    private router: Router,
    private utils: Utils,
    private loginService: LoginService,
    private dataImportService: DataImportService
  ) { }

  ngOnInit() {
    this.jobStatus = new JobStatusResponse();
    this.request = new WSTestRequest();
    if (!this.loginService.isAuthorized([ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.busy = true;
      this.getJobStatus();
    }
  }

  dataImportFull() {

    this.dataImportService.getJobStatus().subscribe(
      resp => {
        this.jobStatus = resp;

        if (this.jobStatus.jobStatus !== DATA_IMPORT_CONSTANTS.JOB_STATUS_NONE &&
          this.jobStatus.jobStatus !== DATA_IMPORT_CONSTANTS.JOB_STATUS_FINISHED &&
          this.utils.isBlank(this.jobStatus.jobFinished)) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_INFO, 'label.Info', 'label.JobRunning');
        } else {

          // start new data import
          this.dataImportStatus = false;
          this.enrollmentStatus = false;
          this.identificationStatus = false;

          this.dataImportFinished = false;
          this.enrollmentFinished = false;
          this.dataIdentificationFinished = false;

          this.busy = true;
          this.dataImportRunning = true;
          this.jobStatus = new JobStatusResponse();
          this.jobStatus.jobStatus = DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_MARIS2LIBIAS;
          this.jobStatus.jobStarted = moment(new Date().getTime()).format('DD.MM.YYYY HH:mm:ss');

          this.dataImportService.startDataImportFull().subscribe(
            resp => {
              this.getJobStatus();
            },
            err => {
              console.error('Error starting new data import. ' + err);
              this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.DataImport');
            }
          );
        }
      },
      err => {
        console.error('Error checking running job. ' + err);
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CheckingRunningDataImport');
      }
    );
  }

  dataImportSkipMaris() {
    this.dataImportService.getJobStatus().subscribe(
      resp => {
        this.jobStatus = resp;

        if (this.jobStatus.jobStatus !== DATA_IMPORT_CONSTANTS.JOB_STATUS_NONE &&
          this.jobStatus.jobStatus !== DATA_IMPORT_CONSTANTS.JOB_STATUS_FINISHED &&
          this.utils.isBlank(this.jobStatus.jobFinished)) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_INFO, 'label.Info', 'label.JobRunning');
        } else {

          // start new data import
          this.dataImportStatus = false;
          this.enrollmentStatus = false;
          this.identificationStatus = false;

          this.dataImportFinished = false;
          this.enrollmentFinished = false;
          this.dataIdentificationFinished = false;

          this.busy = true;
          this.dataImportRunning = true;
          this.jobStatus = new JobStatusResponse();
          this.jobStatus.jobStatus = DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_MARIS2LIBIAS;
          this.jobStatus.jobStarted = moment(new Date().getTime()).format('DD.MM.YYYY HH:mm:ss');

          this.dataImportService.startDataImportSkipMaris().subscribe(
            resp => {
              this.getJobStatus();
            },
            err => {
              console.error('Error starting new data import. ' + err);
              this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.DataImport');
            }
          );
        }
      },
      err => {
        console.error('Error checking running job. ' + err);
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CheckingRunningDataImport');
      }
    );
  }

  get jobDuration(): string {
    if (this.utils.isBlank(this.jobStatus) || this.utils.isBlank(this.jobStatus.jobFinished)) {
      return '';
    }
    let start = moment(this.jobStatus.jobStarted, 'DD.MM.YYYY HH:mm:ss');
    let finish = moment(this.jobStatus.jobFinished, 'DD.MM.YYYY HH:mm:ss');
    let span = moment.duration(finish.diff(start));
    return span.get('hours') + 'h ' + span.get('minutes') + 'm ' + span.get('seconds') + 's';
  }

  getJobStatus() {
    this.dataImportService.getJobStatus().subscribe(
      resp => {
        this.jobStatus = resp;

        if (resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_NONE) {
          this.dataImportStatus = false;
          this.enrollmentStatus = false;
          this.identificationStatus = false;

          this.dataImportFinished = false;
          this.enrollmentFinished = false;
          this.dataIdentificationFinished = false;

          this.dataImportRunning = false;
          this.enrollmentRunning = false;
          this.dataIdentificationRunning = false;

          this.busy = false;

        } else if (resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_FINISHED) {
          this.dataImportStatus = true;
          this.enrollmentStatus = true;
          this.identificationStatus = true;

          this.dataImportFinished = true;
          this.enrollmentFinished = true;
          this.dataIdentificationFinished = true;

          this.dataImportRunning = false;
          this.enrollmentRunning = false;
          this.dataIdentificationRunning = false;

          this.busy = false;

        } else if (resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_MARIS2LIBIAS || resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_LIBIAS2COGNITEC) {
          if (this.utils.isBlank(resp.jobFinished)) {
            this.dataImportRunning = true;
          } else {
            this.dataImportRunning = false;
            this.dataImportStatus = false;
            this.dataImportFinished = true;
            this.busy = false;
          }
        } else if (resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_DBENROLLMENT) {
          this.dataImportRunning = false;
          this.dataImportStatus = true;
          this.dataImportFinished = true;

          if (this.utils.isBlank(resp.jobFinished)) {
            this.enrollmentRunning = true;
          } else {
            this.enrollmentRunning = false;
            this.enrollmentStatus = false;
            this.enrollmentFinished = true;
            this.busy = false;
          }
        } else if (resp.jobStatus === DATA_IMPORT_CONSTANTS.JOB_STATUS_RUNNING_COGNITEC2LIBIAS) {
          this.dataImportRunning = false;
          this.dataImportStatus = true;
          this.dataImportFinished = true;

          this.enrollmentRunning = false;
          this.enrollmentStatus = true;
          this.enrollmentFinished = true;

          if (this.utils.isBlank(resp.jobFinished)) {
            this.dataIdentificationRunning = true;
          } else {
            this.dataIdentificationRunning = false;
            this.identificationStatus = false;
            this.dataIdentificationFinished = true;
            this.busy = false;
          }
        }
      },
      err => {
        console.error('Error checking running job. ' + err);
        this.utils.isErrorForbidden(err);
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CheckingRunningDataImport');
      }
    );
  }

  getBild() {
    if (this.request.bildId === null) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Empty token id or bild id');
      return;
    }

    this.dataImportService.getBild(this.request).subscribe(
      resp => {
        console.log('Bild call was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error bild call');
        console.log('Error call bild. ' + err);
      }
    );
  }

  getAktenzeichen() {
    if (this.request.aktenzeichenA === null || this.request.aktenzeichenB === null) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Empty token id or aktenzeichen');
      return;
    }

    this.dataImportService.getAkte(this.request).subscribe(
      resp => {
        console.log('Aktenzeichen call was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error aktenzeichen call');
        console.log('Error call aktenzeichen. ' + err);
      }
    );
  }

  getBildPerformance() {
    if (this.request.bildId === null) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Empty token id or bild id');
      return;
    }

    this.dataImportService.getBildPer(this.request).subscribe(
      resp => {
        console.log('Bild performance call was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error bild performance call');
        console.log('Error call bild performance. ' + err);
      }
    );
  }

  getAktenzeichenPerformance() {
    if (this.request.aktenzeichenA === null || this.request.aktenzeichenB === null) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Empty token id or aktenzeichen');
      return;
    }

    this.dataImportService.getAktePer(this.request).subscribe(
      resp => {
        console.log('Aktenzeichen performance call was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error akte performance call');
        console.log('Error call akte performance. ' + err);
      }
    );
  }

  getUpdatedApplicants() {
    this.dataImportService.getUpdatedApplicants(this.request).subscribe(
      resp => {
        console.log('Call get updated applicants was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error call of get updated applicants');
        console.log('Error call of get updated applicants. ' + err);
      }
    );
  }

  getToken() {
    this.dataImportService.getToken().subscribe(
      resp => {
        console.log('Call get token was successfull.');
      },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'Error calling of get token');
        console.log('Error calling of get token. ' + err);
      }
    );
  }
}
