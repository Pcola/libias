import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';

import { ConfirmationService, DataTable, Message, SelectItem } from 'primeng/primeng';
import { SessionStorageService } from 'ng2-webstorage';
import { TranslateService } from 'ng2-translate';
import { UserInfoResponse } from '../shared/model/user/index';
import { IncidentRequest, IncidentResponse } from '../shared/model/incident/index';
import { IncidentService, LoginService, ReportService, UserService, Utils, PriorityService } from '../shared/service/index';
import {
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  ROLE_ADMIN,
  ROLE_AUSSENSTELLEUSER,
  STATUS_ID_FILES_DOUBLET,
  STATUS_ID_FILES_NO_DOUBLET,
  STATUS_ID_FILES_NO_LINK,
  STATUS_ID_READY_TO_QA,
  STORAGE_AUSSENSTELLER_CASE_ID,
  STORAGE_AUSSENSTELLER_PAGE,
  STORAGE_AUSSENSTELLER_REQUEST
} from '../shared/constants';

const DEFAULT_ITEM = '--';
const DEFAULT_ITEM_NULL = 'NULL';
const DEFAULT_ITEM_NUMBER = -1;

@Component({
  moduleId: module.id,
  templateUrl: 'aussensteller-list.component.html',
})
export class AussenstellerListComponent implements OnInit {

  growlLife = GROWL_LIFE;
  busy: boolean = false;
  msgs: Message[] = [];
  defaultRows: number = 20;

  selectedCase: IncidentResponse;
  priorities: SelectItem[];
  cases: IncidentResponse[];
  nationalities: SelectItem[];
  fileReferences: SelectItem[];
  statuses: SelectItem[];
  workplaceOptions: SelectItem[] = [];
  userInfo: UserInfoResponse;

  disableStatusUpdate = true;
  filter: IncidentRequest;
  cachedFilter: IncidentRequest;

  selectedCaseId: number;
  filterPriority: number;
  filterStatus: number;
  filterNationality: string;
  filterReferenceType: string;
  filterWorkplace: string = '';

  msgConfirmation: string;
  totalCasesCount: number;

  @ViewChild('dt') dataTable: DataTable;
  page: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private storage: SessionStorageService,
    private incidentService: IncidentService,
    private utils: Utils,
    private confirmationService: ConfirmationService,
    private reportService: ReportService,
    private loginService: LoginService,
    private priorityService: PriorityService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.userInfo = new UserInfoResponse();

    //this.clear();
    this.filter = new IncidentRequest();
    this.cachedFilter = new IncidentRequest();

    this.filter.referenceType = DEFAULT_ITEM;

    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_AUSSENSTELLEUSER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.busy = true;
      this.retrieveCachedFilter();
      this.getUserInfo().subscribe(
        resp => {
          this.userInfo = resp;
          this.setWorkplaces();
          this.getNationality();
          this.fillPriorities();
          this.fillStatus();
          this.fillFileReference();
          this.search();
        }, err => {
          console.error('Cannot get logged in user info: ' + err);
          return null;
        }
      );
    }
  }

  actionSearch() {
    this.storage.clear(STORAGE_AUSSENSTELLER_REQUEST);
    this.storage.clear(STORAGE_AUSSENSTELLER_PAGE);
    this.storage.clear(STORAGE_AUSSENSTELLER_CASE_ID);

    this.selectedCaseId = undefined;
    this.dataTable.first = 0;
    this.utils.trimObjectAttibutes(this.filter);
    this.search();
  }

  /**
   * call rest to get Incident by filter, checking for default values
   */
  search() {
    if (this.filter.priorityId === DEFAULT_ITEM_NUMBER) {
      this.filter.priorityId = null;
    }
    if (this.filter.nationality === DEFAULT_ITEM) {
      this.filter.nationality = null;
    }
    if (this.filter.statusId === DEFAULT_ITEM_NUMBER) {
      this.filter.statusId = null;
    }
    if (this.page) {
        this.filter.first = this.page.first;
        this.filter.rows = this.page.rows;
        this.filter.order = this.page.sortOrder;
        this.filter.sort = this.page.sortField;
    } else {
        this.filter.first = 0;
        this.filter.rows = this.defaultRows;
        this.filter.order = null;
        this.filter.sort = null;
    }
    if (this.filter.referenceType === DEFAULT_ITEM) {
      this.filter.referenceType = null;
    }

    this.getAussenstellerCases();
  }

  changedPriority(evt: any) {
    this.filter.priorityId = evt.value;
  }

  changedNationality(evt: any) {
    this.filter.nationality = evt.value;
  }

  changedStatus(evt: any) {
    this.filter.statusId = evt.value;
  }

  changedFileReference(evt: any) {
    this.filter.referenceType = evt.value;
  }

  changedWorkplace(evt: any) {
    this.filter.workplaceId = evt.value;
    this.cachedFilter.workplaceId = evt.value;
    this.getAussenstellerCases();
  }

  onRowSelect(e: any) {
    if (e.data && e.data.caseId) {
      this.storage.store(STORAGE_AUSSENSTELLER_CASE_ID, e.data.caseId);
      this.selectedCaseId = e.data.caseId;
    }
  }

  /**
   *
   * @param e
   *
   * try to load page from storage if page is busy
   */
  onLazyPage(e: any) {
    if (this.busy === false) {
      this.page = e;
      this.storage.store(STORAGE_AUSSENSTELLER_PAGE, JSON.stringify(this.page));
      this.search();
    }
  }

  actionClear() {
    this.clear();
    //this.search();
  }

  /**
   * set default values to all dropdowns, filter etc.
   */
  clear() {
    this.storage.clear(STORAGE_AUSSENSTELLER_REQUEST);
    this.storage.clear(STORAGE_AUSSENSTELLER_PAGE);
    this.storage.clear(STORAGE_AUSSENSTELLER_CASE_ID);
    this.selectedCase = undefined;
    this.selectedCaseId = undefined;
    this.page = { first: 0, rows: this.defaultRows, sortField: null, sortOrder: null };
    this.totalCasesCount = 0;
    this.filter = new IncidentRequest();
    this.filter.referenceType = DEFAULT_ITEM;
    this.filterPriority = DEFAULT_ITEM_NUMBER;
    this.filterNationality = DEFAULT_ITEM;
    this.filterReferenceType = DEFAULT_ITEM;
    this.filterStatus = DEFAULT_ITEM_NUMBER;
    this.dataTable.reset();
  }

  caseDetail(caseId: number) {
    this.storage.store(STORAGE_AUSSENSTELLER_REQUEST, JSON.stringify(this.filter));
    this.storage.store(STORAGE_AUSSENSTELLER_PAGE, JSON.stringify(this.page));
    this.selectedCaseId = caseId;
    this.findSelectedCase();
    this.storage.store(STORAGE_AUSSENSTELLER_CASE_ID, caseId);
    this.router.navigate(['/aussensteller-detail/' + caseId]);
  }

  changeStyleOfRow(caseId:number) {
    if (!isNaN(caseId) && (!this.selectedCase || this.selectedCase.caseId !== caseId)) {
      for (let cs of this.cases) {
        if (cs.caseId === caseId) {
          this.selectedCase = cs;
          break;
        }
      }
    }
  }

  private getAussenstellerCases() {
    this.filter.workplaceId = this.filterWorkplace;
    this.busy = true;
    this.incidentService.getAussenstellerCases(this.filter).subscribe(
      response => {
        this.busy = false;
        this.cases = response.incidents;
        this.totalCasesCount = response.totalCountOfIncidents;
        this.findSelectedCase();
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidentCases');
        console.log('Cannot get incident matches: ' + err);
      }
    );
  }

  private setDefaultValues() {
    this.filter.priorityId = DEFAULT_ITEM_NUMBER;
    this.filter.nationality = DEFAULT_ITEM;
    this.filter.statusId = DEFAULT_ITEM_NUMBER;
  }

  /**
   * load page from storage sesssion and update page with this data(filters and selected incident)
   */
  private retrieveCachedFilter() {

    let page = this.storage.retrieve(STORAGE_AUSSENSTELLER_PAGE);
    if (this.utils.isNotBlank(page)) {
      let cachedPage  = JSON.parse(page);
      this.page = {first: cachedPage.first, rows: cachedPage.rows, sortField: cachedPage.sortField, sortOrder: cachedPage.sortOrder };
      this.paginate();
    }

    let caseId = this.storage.retrieve(STORAGE_AUSSENSTELLER_CASE_ID);
    if (this.utils.isNotBlank(caseId)) {
      this.selectedCaseId = parseInt(caseId);
    }

    let requestStorage = this.storage.retrieve(STORAGE_AUSSENSTELLER_REQUEST);
    if (this.utils.isNotBlank(requestStorage) && this.utils.isValidJSON(requestStorage)) {
      this.cachedFilter = JSON.parse(requestStorage);
      this.filter.caseId = this.cachedFilter.caseId;
      this.filter.priorityId = this.cachedFilter.priorityId;
      this.filter.pkz = this.cachedFilter.pkz;
      this.filter.createdDate = this.cachedFilter.createdDate;
      this.filter.firstName = this.cachedFilter.firstName;
      this.filter.lastName = this.cachedFilter.lastName;
      this.filter.statusId = this.cachedFilter.statusId;
      this.filter.nationality = this.cachedFilter.nationality;
      this.filter.azrNumber = this.cachedFilter.azrNumber;
      this.filter.dNumber = this.cachedFilter.dNumber;
      this.filter.fileNumber = this.cachedFilter.fileNumber;
      this.filter.workplaceId = this.cachedFilter.workplaceId;
      this.filter.referenceType = this.cachedFilter.referenceType;
      this.filter.showDoubleEvents = this.cachedFilter.showDoubleEvents;
    } else {
      this.cachedFilter = new IncidentRequest();
      this.setDefaultValues();
    }
  }

  private getNationality() {
    this.nationalities = [];
    this.translate.get('label.Select').subscribe(v => {
      this.nationalities.push({ label: v, value: DEFAULT_ITEM });
      this.incidentService.getNationalities().subscribe(
        response => {
          for (let c of response) {
            if (this.utils.isNotBlank(c)) {
              this.nationalities.push({ label: c, value: c });
            }
          }
          this.filterNationality = this.utils.defaultIfBlank(this.cachedFilter.nationality, DEFAULT_ITEM);
        },
        err => {
          console.log('Error getting list of nationalities: ' + err);
        });
    });
  }

  private fillPriorities() {
    this.priorities = [];
    this.translate.get('label.Select').subscribe(v => {
      this.priorities.push({ label: v, value: DEFAULT_ITEM_NUMBER });
      this.priorityService.getPriorityItems().subscribe(
        response => {
          for (let c of response) {
            if (this.utils.isNotBlank(c)) {
              this.priorities.push({ label: c.priority, value: c.priorityId });
            }
          }
          this.filterPriority = this.utils.defaultIfBlank(this.cachedFilter.priorityId, DEFAULT_ITEM_NUMBER);
        },
        err => {
          console.log('Error getting list of priorities: ' + err);
        });
    });
  }

  private fillStatus() {
    this.statuses = [];
    this.translate.get('label.Select').subscribe(v => {
      this.statuses.push({ label: v, value: DEFAULT_ITEM_NUMBER });
      this.translate.get('label.Status.' + STATUS_ID_FILES_NO_LINK).subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_NO_LINK }));
      this.translate.get('label.Status.' + STATUS_ID_FILES_DOUBLET).subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_DOUBLET }));
      this.translate.get('label.Status.' + STATUS_ID_FILES_NO_DOUBLET).subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_NO_DOUBLET }));
      this.translate.get('label.Status.' + STATUS_ID_READY_TO_QA).subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_READY_TO_QA }));
    });

    this.filterStatus = this.utils.defaultIfBlank(this.cachedFilter.statusId, DEFAULT_ITEM_NUMBER);
  }

  private fillFileReference() {
    this.fileReferences = [];
    this.translate.get('label.Select').subscribe(v => {
      this.fileReferences.push({ label: v, value: DEFAULT_ITEM });

      this.translate.get('label.NoReferenceType').subscribe(v => {
        this.fileReferences.push({ label: v, value: DEFAULT_ITEM_NULL });
        this.incidentService.getReferenceTypes().subscribe(
          response => {
            for (let c of response) {
              if (this.utils.isNotBlank(c)) {
                this.fileReferences.push({ label: c, value: c });
              }
            }
            this.filterReferenceType = this.utils.defaultIfBlank(this.cachedFilter.referenceType, DEFAULT_ITEM);
            this.filter.referenceType = this.filterReferenceType;
          },
          err => {
            console.log('Error getting list of referenceTypes: ' + err);
          });
      });
    });
  }

  /**
  * Paginate on action back
  */
  private paginate() {
    if (this.utils.isNotBlank(this.page) && this.page.first && this.page.rows) {
      let paging = {
        first: this.page.first,
        rows: this.page.rows,
      };

      // some delay required because a sorting of the table needs some time, and then paginate doesn't work  without a timer
      let timer = Observable.timer(100);
      timer.subscribe((t: any) => {
        this.dataTable.first = paging.first;
        this.dataTable.rows = paging.rows;
        this.dataTable.paginate();
      });
    }

    this.dataTable.sortOrder = this.page.sortOrder;
    this.dataTable.sortField = this.page.sortField;
  }

  /**
  * Find selected item by CaseId on action back.
  */
  private findSelectedCase() {
    if (!isNaN(this.selectedCaseId)) {
      for (let c of this.cases) {
        if (c.caseId === this.selectedCaseId) {
          this.selectedCase = c;
          break;
        }
      }
    }
  }

  private getUserInfo(): Observable<UserInfoResponse> {
    return this.userService.getLoggedUserInfo();
  }

  private setWorkplaces() {
    this.workplaceOptions = [];
    if (this.userInfo.workplace.length !== 0) {
      for (let workplace of this.userInfo.workplace) {
        if (this.utils.isNotBlank(workplace)) {
          this.workplaceOptions.push({label: workplace.workplace, value: workplace.id});
        }
      }
      this.workplaceOptions.sort((a, b) => (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0));
      this.filterWorkplace = this.utils.defaultIfBlank(this.cachedFilter.workplaceId, this.workplaceOptions[0].value);
      this.filter.workplaceId = this.filterWorkplace;
    }
  }
}
