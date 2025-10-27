import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ConfirmationService, Message, SelectItem } from 'primeng/primeng';
import { TranslateService } from 'ng2-translate';

import { Image } from '../shared/model/image/index';
import { FinishCaseRequest, IncidentResponse } from '../shared/model/incident/index';
import { ImageService, IncidentService, LoginService, ReportService, Utils } from '../shared/service/index';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR, GROWL_SEVERITY_INFO, ROLE_ADMIN, ROLE_AUSSENSTELLEUSER } from '../shared/constants';

declare var saveAs: any;
declare var base64: any;

const DEFAULT_ITEM = '--';
const DEFAULT_ITEM_NULL = 'null';
const DEFAULT_ITEM_NUMBER = -1;

@Component({
  moduleId: module.id,
  templateUrl: 'aussensteller-detail.component.html',
})
export class AussenstellerDetailComponent implements OnInit {

  growlLife = GROWL_LIFE;

  case: IncidentResponse;
  caseId: number;
  selectedCaseId: number;
  relatedCases: SelectItem[];
  probeIdImageLoaded: boolean = false;
  galleryIdImageLoaded: boolean = false;
  probeIdImageMissing: boolean = false;
  galleryIdImageMissing: boolean = false;
  caseLoaded: boolean = false;
  probeIdImage: Image;
  galleryIdImage: Image;
  msgs: Message[] = [];
  disableStatusUpdate = true;
  busy: boolean = false;
  historyRecord: string = '';

  msgConfirmation: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private incidentService: IncidentService,
    private imageService: ImageService,
    private utils: Utils,
    private confirmationService: ConfirmationService,
    private reportService: ReportService,
    private loginService: LoginService
  ) {
  }

  ngOnInit() {
    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_AUSSENSTELLEUSER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.route.params.forEach((params: Params) => {
        let caseId = +params['id'] || 0;
        if (isNaN(caseId)) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Warning', 'warning.NoCaseId');
          console.log('Case ID was not given.');
        } else {
          this.busy = true;

          this.initObjects();
          this.selectedCaseId = caseId;
          this.caseId = caseId;
          this.getSiteRelatedCases(caseId);

          this.translate.get('msg.SetIncidentStatus').subscribe(v => { this.msgConfirmation = v; });
        }
      });
    }
    this.setHistoryRecord();
  }

  /**
   *
   * @param selectedCaseId - id of selected inciden
   *
   * when user change selected case, call rest to get incident, then load images and set history record
   */
  onChangedIncidentMatch(selectedCaseId: number) {
    console.debug('Selected case: ' + selectedCaseId);
    this.busy = true;
    this.probeIdImageLoaded = false;
    this.galleryIdImageLoaded = false;
    this.probeIdImageMissing = false;
    this.galleryIdImageMissing = false;
    this.selectedCaseId = selectedCaseId;
    this.case = new IncidentResponse();

    // get selected case detail
    this.incidentService.getIncident(selectedCaseId).subscribe(resp => {
      this.case = resp;
      this.loadProbeIdImage(this.case.probeId);
      this.loadGalleryIdImage(this.case.galleryId);
      this.setDisableStatusUpdate();
      this.busy = false;
      this.setHistoryRecord();
    },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidentCases');
        console.log('Cannot get case detail: ' + err);
        this.busy = false;
      });
  }

  downloadReport() {
    this.busy = true;
    this.reportService.downloadReport(this.case.caseId).subscribe(
      resp => {

        this.reportService.getReportId(this.caseId).subscribe(
          resp2 => {
            saveAs(resp, 'LIBIAS_' + this.case.aPkz + '_' + this.case.bPkz + '.pdf');
            this.busy = false;
          },
          err2 => {
            this.busy = false;
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
            console.log('Cannot generate report: ' + err2);
          });

      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        console.log('Cannot download report: ' + err);
      }
    );
  }

  actionBack() {
    this.router.navigate(['aussensteller-list']);
  }

  /**
   * after button click, show confifrmation dialog and then call rest to finish selected case
   * after reponse, update history record
   */
  actionFinish() {

    this.confirmationService.confirm({
      message: this.msgConfirmation,
      accept: () => {
        this.busy = true;
        let request = new FinishCaseRequest();
        request.caseId = this.selectedCaseId;
        request.workplaceNote = this.case.workplaceNote;
        this.incidentService.finishCase(request).subscribe(
          resp => {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_INFO, 'label.Info', 'info.StatusUpdated');
            // refresh incident status
            this.incidentService.getIncident(request.caseId).subscribe(resp => {
              this.case = resp;
              this.setDisableStatusUpdate();
              this.busy = false;
              this.setHistoryRecord();
            },
              err => {
                this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidentCases');
                console.log('Cannot get incident matches: ' + err);
                this.busy = false;
              });
          },
          err => {
            this.busy = false;
            console.log('Cannot update incident status: ' + err);
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.StatusUpdate');
          }
        );
      }
    });
  }

  getMatchRowClass(v1: any, v2: any): string {
    if (this.busy) {
      return 'empty-class';
    }
    if (v1 === v2) {
      return 'match-table-row-ok';
    } else if (String(v1).trim().toLowerCase() === String(v2).trim().toLowerCase()) {
      return 'match-table-row-warning';
    }
    return 'match-table-row-error';
  }

  getMatchRowIcon(v1: any, v2: any): string {
    if (this.busy) {
      return 'FFFFFF-0.8_1px.png';
    }
    if (v1 === v2) {
      return 'Actions-dialog-ok-apply-icon.png';
    } else if (String(v1).trim().toLowerCase() === String(v2).trim().toLowerCase()) {
      return 'Status-dialog-warning-icon.png';
    }
    return 'Actions-edit-delete-icon.png';
  }

  private loadProbeIdImage(oid: number) {
    this.probeIdImage = new Image();
    this.imageService.getImage(oid).subscribe(
      resp => {
        if (resp && resp.imageData) {
          this.probeIdImage = resp;
          this.probeIdImageLoaded = true;
        } else {
          this.probeIdImageMissing = true;
        }
        if (this.galleryIdImageLoaded || this.galleryIdImageMissing) {
          this.busy = false;
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetProbeImage');
        console.log('Cannot get image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  private loadGalleryIdImage(oid: number) {
    this.galleryIdImage = new Image();
    this.imageService.getImage(oid).subscribe(
      resp => {
        if (resp && resp.imageData) {
          this.galleryIdImage = resp;
          this.galleryIdImageLoaded = true;
        } else {
          this.galleryIdImageMissing = true;
        }
        if (this.probeIdImageLoaded || this.probeIdImageMissing) {
          this.busy = false;
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetGalleryImage');
        console.log('Cannot get GalleryID image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  private initObjects() {
    this.probeIdImage = new Image();
    this.galleryIdImage = new Image();
    this.case = new IncidentResponse();
    this.probeIdImageLoaded = false;
    this.galleryIdImageLoaded = false;
    this.probeIdImageMissing = false;
    this.galleryIdImageMissing = false;
  }

  /**
   *
   * @param caseId - id of selected Incident
   *
   * call rest to get related cases(cases which images mark cognitec as related)
   */
  private getSiteRelatedCases(caseId: number) {
    this.relatedCases = [];

    this.incidentService.getSiteRelatedCases(caseId).subscribe(
      response => {

        for (let m of response) {
          this.relatedCases.push({ label: m.pkz1 + ' - ' + m.pkz2, value: m.caseId });
        }

        // get selected case detail
        this.incidentService.getIncident(caseId).subscribe(resp => {
          this.busy = false;
          this.case = resp;
          this.onChangedIncidentMatch(this.selectedCaseId);
          this.setHistoryRecord();
        },
          err => {
            this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidentCases');
            console.log('Cannot get incident matches: ' + err);
            this.busy = false;
          });
      },
      error => {
        this.busy = false;
        console.log(error);
      });
  }

  /**
   * add history records to text area, sort them by date of change, added which note was changed
   */
  private setHistoryRecord() {
    this.historyRecord = '';
    if (this.case === null || this.case.incidentHistory === null || this.case.incidentHistory === undefined) {
      return;
    }
    this.case.incidentHistory.sort((a, b) => a.changedOn > b.changedOn ? -1 : a.changedOn < b.changedOn ? 1 : 0);
    for(let entry of this.case.incidentHistory) {
      let type: String;
      if(entry.type === 'e') {
        this.translate.get('text.ChangeHistory.E').subscribe(v => { type = v; });
      } else {
        this.translate.get('text.ChangeHistory.B').subscribe(v => { type = v; });
      }
      this.historyRecord = this.historyRecord + entry.changedBy + ', ' + entry.changedOn + type + '\n';
    }
  }

  private setDisableStatusUpdate() {
    this.disableStatusUpdate = false;
  }

}
