import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ConfirmationService, Message, SelectItem } from 'primeng/primeng';

import { TranslateService } from 'ng2-translate';
import { Image } from '../shared/model/image/index';
import { IncidentResponse, IncidentStatusRequest } from '../shared/model/incident/index';
import { ImageService, IncidentService, LoginService, PriorityService, ReportService, Utils, WorkplaceService } from '../shared/service/index';

import {
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  GROWL_SEVERITY_INFO,
  GROWL_SEVERITY_WARN,
  ROLE_ADMIN,
  ROLE_SUPEUSER,
  ROLE_USER,
  STATUS_ID_ADJUSTED,
  STATUS_ID_AUTO_ADJUSTED,
  STATUS_ID_DNUMBER_DIFF,
  STATUS_ID_FILES_DOUBLET,
  STATUS_ID_FILES_NO_DOUBLET,
  STATUS_ID_FILES_NO_LINK,
  STATUS_ID_NO_PROCESSING,
  STATUS_ID_NOT_CLEAR,
  STATUS_ID_OPEN,
  STATUS_ID_READY_TO_QA
} from '../shared/constants';

declare var saveAs: any;
declare var base64: any;

@Component({
  moduleId: module.id,
  templateUrl: 'incident-item.component.html',
})
export class IncidentItemComponent implements OnInit {

  growlLife = GROWL_LIFE;
  case: IncidentResponse;
  caseId: number;
  relatedCases: SelectItem[];
  priorityItems: SelectItem[];
  statusItems: SelectItem[];
  workplaceItems: SelectItem[];
  probeIdImageLoaded: boolean = false;
  galleryIdImageLoaded: boolean = false;
  probeIdImageMissing: boolean = false;
  galleryIdImageMissing: boolean = false;

  probeIdImage: Image;
  galleryIdImage: Image;
  selectedCaseId: number;
  selectedPriority: number;
  selectedStatus: number;
  selectedWorkplaceId: string;
  msgs: Message[] = [];
  disableStatusUpdate = true;
  disablePriorityChange = true;
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
    private loginService: LoginService,
    private priorityService: PriorityService,
    private workplaceService: WorkplaceService
  ) {
  }

  ngOnInit() {
    this.relatedCases = [];
    this.probeIdImage = new Image();
    this.galleryIdImage = new Image();
    this.case = new IncidentResponse();
    this.fillPriorities();
    this.setDisablePriorityUpdate();
    this.fillWorkplace();

    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPEUSER])) {
      this.loginService.logout(true);
    } else {
      this.route.params.forEach((params: Params) => {
        let caseId = +params['id'] || 0;
        if (isNaN(caseId)) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Warning', 'warning.NoCaseId');
          console.log('CaseId was not given.');
        } else {
          this.selectedCaseId = caseId;
          this.caseId = caseId;
          this.busy = true;
          this.getRelatedCases(caseId);
        }
      });

      this.translate.get('msg.SetIncidentStatus').subscribe(v => { this.msgConfirmation = v; });
      this.setHistoryRecord();
    }
  }

  /**
   *
   * @param selectedCaseId
   * when user change incident, call res to get info of selected case, load image, find workplace id based on incident wokplace id
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
      this.setHistoryRecord();
      if (this.case.status.statusId === STATUS_ID_AUTO_ADJUSTED) {
        this.fillStatusWtihAutomaticallyAdjusted();
      } else {
        this.fillStatus();
      }

      this.setDisableStatusUpdate();
      this.loadProbeIdImage(this.case.probeId);
      this.loadGalleryIdImage(this.case.galleryId);
      this.selectedStatus = this.case.status.statusId;
      this.selectedPriority = this.case.priority.priorityId;
      // fill workplace/aussenstelle
      if (this.workplaceItems && this.workplaceItems.length > 0) {
        let found: boolean = false;
        for (let w of this.workplaceItems) {
          if (this.case.workplace !== null && w.value === this.case.workplace.id) {
            this.selectedWorkplaceId = this.case.workplace.id;
            found = true;
            break;
          }
        }
        if (!found) {
          this.selectedWorkplaceId = this.workplaceItems[0].value;
        }
      } else {
        this.selectedWorkplaceId = null;
      }

      this.busy = false;

    },
      err => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidentCases');
        console.log('Cannot get case detail: ' + err);
        this.busy = false;
      });
  }

  /**
   * when user clicked button for detail
   */
  detailAnalysis() {
    this.router.navigate(['/issue-detail'], { queryParams: { probeId: this.case.probeId, galleryId: this.case.galleryId } });
  }

  /**
   * first generate report on rest and then call rest to get it
   */
  downloadReport() {
    this.busy = true;
    this.reportService.downloadReport(this.selectedCaseId).subscribe(
      resp => {

        this.reportService.getReportId(this.selectedCaseId).subscribe(
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
    this.router.navigate(['incident-list']);
  }

  /**
   * save changes = when status is changed
   */
  actionSave() {
    if (this.selectedCaseId === undefined) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_WARN, 'label.Warning', 'warning.MatchPersonNotSelected');
      return;
    }

    if ((this.selectedStatus === STATUS_ID_READY_TO_QA || this.selectedStatus === STATUS_ID_ADJUSTED) && !this.loginService.isAuthorized(['SUPERUSER'])) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_WARN, 'label.Warning', 'warning.IllegalOperation');
      return;
    }

    this.confirmationService.confirm({
      message: this.msgConfirmation,
      accept: () => {
        this.busy = true;
        let request = new IncidentStatusRequest();
        request.caseId = this.selectedCaseId;
        if (this.selectedStatus > 0) {
          request.statusId = this.selectedStatus;
        }
        request.note = this.case.note;
        request.workplaceId = this.selectedWorkplaceId;
        request.priorityId = this.selectedPriority;
        this.incidentService.setIncidentStatus(request).subscribe(
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
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetGalleryImage');
        console.log('Cannot get GalleryID image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  /**
   *
   * @param caseId - id of selected Incident
   *
   * call rest to get related cases(cases which images mark cognitec as related)
   */
  private getRelatedCases(caseId: number) {
    this.relatedCases = [];

    this.incidentService.getRelatedCases(caseId).subscribe(
      response => {

        for (let m of response) {
          this.relatedCases.push({ label: m.pkz1 + ' - ' + m.pkz2, value: m.caseId });
        }

        // get selected case detail
        this.incidentService.getIncident(caseId).subscribe(resp => {
          this.busy = false;
          this.case = resp;
          this.setDisableStatusUpdate();
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

  private fillPriorities() {
    this.priorityItems = [];
    this.priorityService.getPriorityItems().subscribe(
      response => {
        for (let c of response) {
          if (this.utils.isNotBlank(c)) {
            this.priorityItems.push({ label: c.priority, value: c.priorityId });
          }
        }
      },
      err => {
        console.log('Error getting list of priorities: ' + err);
      });
  }

  private fillStatus() {
    this.statusItems = [];
    let statusIds = [STATUS_ID_OPEN, STATUS_ID_NOT_CLEAR, STATUS_ID_NO_PROCESSING, STATUS_ID_FILES_DOUBLET,
      STATUS_ID_FILES_NO_DOUBLET, STATUS_ID_FILES_NO_LINK, STATUS_ID_ADJUSTED, STATUS_ID_DNUMBER_DIFF];
    if (this.loginService.isAuthorized([ROLE_SUPEUSER])) {
      statusIds.push(STATUS_ID_READY_TO_QA);
    }
    for (let s of statusIds) {
      this.translate.get('label.Status.' + s).subscribe(v => this.statusItems.push({ label: v, value: s }));
    }
  }

  private fillStatusWtihAutomaticallyAdjusted() {
    this.statusItems = [];
    this.fillStatus();
    this.translate.get('label.Status.' + STATUS_ID_AUTO_ADJUSTED).subscribe(v => this.statusItems.push({ label: v, value: STATUS_ID_AUTO_ADJUSTED }));
  }

  private fillWorkplace() {
    this.workplaceItems = [];
    this.workplaceService.getWorkplaceItems().subscribe(
      response => {
        this.workplaceItems.push({ label: '', value: '' });
        for (let c of response) {
          if (this.utils.isNotBlank(c)) {
            this.workplaceItems.push({ label: c.workplace, value: c.id });
          }
        }
        if (response && response.length > 0) {
          this.selectedWorkplaceId = this.workplaceItems[0].value;
        }
      },
      err => {
        console.log('Error getting list of workplace: ' + err);
      });
  }

  private setDisableStatusUpdate() {
    this.disableStatusUpdate = (this.selectedStatus === STATUS_ID_READY_TO_QA || this.selectedStatus === STATUS_ID_ADJUSTED) && !this.loginService.isAuthorized(['SUPERUSER']);
    if (this.case.status.statusId === STATUS_ID_AUTO_ADJUSTED) {
      this.disableStatusUpdate = true;
    }
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

  private setDisablePriorityUpdate() {
    this.disablePriorityChange = !(this.loginService.isAuthorized([ROLE_ADMIN]) || this.loginService.isAuthorized([ROLE_SUPEUSER]) || this.loginService.isAuthorized([ROLE_USER]));
  }
}
