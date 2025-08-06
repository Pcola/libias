import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { TranslateService } from 'ng2-translate';
import { IncidentRequest, IncidentResponse } from '../shared/model/incident/index';
import { IncidentService, LoginService, PriorityService, Utils, WorkplaceService } from '../shared/service/index';
import { DataTable, Message, SelectItem } from 'primeng/primeng';
import { SessionStorageService } from 'ng2-webstorage';

import {
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  ROLE_ADMIN,
  ROLE_AUSSENSTELLEUSER,
  ROLE_COMPARER,
  ROLE_SEARCHER,
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
  STATUS_ID_READY_TO_QA,
  STORAGE_INCIDENT_CASE_ID,
  STORAGE_INCIDENT_PAGE,
  STORAGE_INCIDENT_REQUEST
} from '../shared/constants';

const DEFAULT_ITEM = '--';
const DEFAULT_ITEM_NULL = 'NULL';
const DEFAULT_ITEM_NUMBER = -1;

@Component({
  moduleId: module.id,
  templateUrl: 'incident-list.component.html',
})
export class IncidentListComponent implements OnInit {
  incidents: IncidentResponse[];
  nationalities: SelectItem[];
  referenceTypes: SelectItem[];
  priorities: SelectItem[];
  statuses: SelectItem[];
  workplaceItems: SelectItem[];
  filter: IncidentRequest;
  cachedFilter: IncidentRequest;
  busy: boolean = false;
  msgs: Message[] = [];
  growlLife = GROWL_LIFE;
  page: any;
  selectedIncident: IncidentResponse;
  defaultRows: number = 20;
  selectedCaseId: number;
  selectedPriority: number;
  selectedStatus: number;
  selectedNationality: string;
  selectedReferenceType: string;
  selectedWorkplaceId: string;
  totalIncidentsCount: number = 0;

  @ViewChild('dt') dataTable: DataTable;

  selectedRow: IncidentResponse;
  selectedRowsCount: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private incidentService: IncidentService,
    private translate: TranslateService,
    private storage: SessionStorageService,
    private utils: Utils,
    private loginService: LoginService,
    private priorityService: PriorityService,
    private workplaceService: WorkplaceService
  ) { }

  ngOnInit() {
    this.selectedIncident = new IncidentResponse();
    this.filter = new IncidentRequest();
    this.cachedFilter = new IncidentRequest();

    this.filter.referenceType = DEFAULT_ITEM;

    if (!this.loginService.isAuthenticated()) {
      this.loginService.logout(true);
    } else if (this.loginService.isAuthorized([ROLE_AUSSENSTELLEUSER]) && !this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPEUSER])) {
      this.router.navigate(['/aussensteller-list']);
    } else if (this.loginService.isAuthorized([ROLE_COMPARER]) && !this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPEUSER])) {
      this.router.navigate(['/comparer']);
    } else if (this.loginService.isAuthorized([ROLE_SEARCHER]) && !this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPEUSER])) {
      this.router.navigate(['/searcher']);
    } else if (!this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPEUSER])) {
      this.loginService.logout(true);
    } else {
      this.busy = true;
      this.retrieveCachedRequest();
      this.getNationality();
      this.fillPriorities();
      this.fillStatus();
      this.fillReferenceType();
      this.fillWorkplace();
      this.search();
    }
  }

  actionSearch() {
    this.storage.clear(STORAGE_INCIDENT_REQUEST);
    this.storage.clear(STORAGE_INCIDENT_PAGE);
    this.storage.clear(STORAGE_INCIDENT_CASE_ID);
    this.selectedCaseId = undefined;
    this.dataTable.first = 0;
    //this.page = undefined;
    this.utils.trimObjectAttibutes(this.filter);
    this.search();
  }

  /**
   *
   * @param caseId - selected Incident id
   *
   * when detail is pushed, save page in session storage
   */
  navigateCase(caseId: number) {
    this.storage.store(STORAGE_INCIDENT_REQUEST, JSON.stringify(this.filter));
    this.storage.store(STORAGE_INCIDENT_PAGE, JSON.stringify(this.page));
    this.selectedCaseId = caseId;
    this.findSelectedIncident();
    this.storage.store(STORAGE_INCIDENT_CASE_ID, caseId);
    this.router.navigate(['/incident-item/' + caseId]);
  }

  clear() {
    this.storage.clear(STORAGE_INCIDENT_REQUEST);
    this.storage.clear(STORAGE_INCIDENT_PAGE);
    this.storage.clear(STORAGE_INCIDENT_CASE_ID);
    this.filter = new IncidentRequest();
    this.filter.referenceType = DEFAULT_ITEM;
    this.selectedNationality = DEFAULT_ITEM;
    this.selectedWorkplaceId = DEFAULT_ITEM;
    this.selectedReferenceType = DEFAULT_ITEM;
    this.selectedPriority = DEFAULT_ITEM_NUMBER;
    this.selectedStatus = DEFAULT_ITEM_NUMBER;
    this.page = { first: 0, rows: this.defaultRows, sortField: null, sortOrder: null };
    this.totalIncidentsCount = 0;
    this.selectedCaseId = undefined;
    this.selectedIncident = undefined;
    this.dataTable.reset();
    //this.page = { first: 0, rows: this.defaultRows, sortField: null, sortOrder: null };
    //this.search();
  }

  changedNationality(evt: any) {
    this.filter.nationality = evt.value;
  }

  changedPriority(evt: any) {
    this.filter.priorityId = evt.value;
  }

  changedStatus(evt: any) {
    this.filter.statusId = evt.value;
  }

  changedReferenceType(evt: any) {
    this.filter.referenceType = evt.value;
  }

  changedWorkplaceId(evt: any) {
    this.filter.workplaceId = evt.value;
  }

  /**
   *
   * @param e
   *
   * if busy, try to load page from session storage
   */
  onLazyPage(e: any) {
    if (this.busy === false) {
      this.page = e;
      this.storage.store(STORAGE_INCIDENT_PAGE, JSON.stringify(this.page));
      this.search();
    }
  }

  onRowSelect(e: any) {
    if (e.data && e.data.caseId) {
      this.storage.store(STORAGE_INCIDENT_CASE_ID, e.data.caseId);
      this.selectedCaseId = e.data.caseId;
    }
  }

  changeStyleOfRow(caseId:number) {
    if (!isNaN(caseId) && (!this.selectedIncident || this.selectedIncident.caseId !== caseId)) {
      for (let inc of this.incidents) {
        if (inc.caseId === caseId) {
          this.selectedIncident = inc;
          break;
        }
      }
    }
  }

  /**
   * search for incidents with own filter, check if filter values are default(user doesn't change them)
   */
  private search() {
    if (this.filter.nationality === DEFAULT_ITEM) {
      this.filter.nationality = null;
    }
    if (this.filter.priorityId === DEFAULT_ITEM_NUMBER) {
      this.filter.priorityId = null;
    }
    if (this.filter.statusId === DEFAULT_ITEM_NUMBER) {
      this.filter.statusId = null;
    }
    if (this.filter.referenceType === DEFAULT_ITEM) {
      this.filter.referenceType = null;
    }
    if (this.filter.workplaceId === DEFAULT_ITEM) {
      this.filter.workplaceId = null;
    }

    if (this.page) {
        this.filter.rows = this.page.rows;
        this.filter.first = this.page.first;
        this.filter.sort = this.page.sortField;
        this.filter.order = this.page.sortOrder;
    } else {
        this.filter.rows = this.defaultRows;
        this.filter.first = 0;
        this.filter.sort = null;
        this.filter.order = null;
    }

    this.busy = true;
    this.incidentService.getIncidents(this.filter).subscribe(
      response => {
        this.incidents = response.incidents;
        this.totalIncidentsCount = response.totalCountOfIncidents;
        this.busy = false;
        this.findSelectedIncident();
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetIncidents');
        console.log('Error getting incidents: ' + err);
        this.utils.isErrorForbidden(err);
        //this.selectedStatus = 0; // OPEN incidents
      });
  }

  private setDefaultValues() {
    this.filter.nationality = DEFAULT_ITEM;
    this.filter.workplaceId = DEFAULT_ITEM;
    this.filter.statusId = DEFAULT_ITEM_NUMBER;
    this.filter.priorityId = DEFAULT_ITEM_NUMBER;
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
          this.selectedNationality = this.utils.defaultIfBlank(this.cachedFilter.nationality, DEFAULT_ITEM);
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
          this.selectedPriority = this.utils.defaultIfBlank(this.cachedFilter.priorityId, DEFAULT_ITEM_NUMBER);
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
      this.translate.get('label.Status.1').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_OPEN }));
      this.translate.get('label.Status.2').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_DOUBLET }));
      this.translate.get('label.Status.3').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_NO_DOUBLET }));
      this.translate.get('label.Status.4').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_NOT_CLEAR }));
      this.translate.get('label.Status.5').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_NO_PROCESSING }));
      this.translate.get('label.Status.6').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_FILES_NO_LINK }));
      this.translate.get('label.Status.7').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_ADJUSTED }));
      this.translate.get('label.Status.9').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_DNUMBER_DIFF }));
      if (this.loginService.isAuthorized([ROLE_SUPEUSER])) {
        this.translate.get('label.Status.8').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_READY_TO_QA }));
      }
      this.translate.get('label.Status.10').subscribe(v => this.statuses.push({ label: v, value: STATUS_ID_AUTO_ADJUSTED }));

      this.selectedStatus = this.utils.defaultIfBlank(this.cachedFilter.statusId, DEFAULT_ITEM_NUMBER);
    });
  }

  private fillReferenceType() {
    this.referenceTypes = [];
    this.translate.get('label.Select').subscribe(v => {
      this.referenceTypes.push({ label: v, value: DEFAULT_ITEM });

      this.translate.get('label.NoReferenceType').subscribe(v => {
        this.referenceTypes.push({ label: v, value: DEFAULT_ITEM_NULL });
        this.incidentService.getReferenceTypes().subscribe(
          response => {
            for (let c of response) {
              if (this.utils.isNotBlank(c)) {
                this.referenceTypes.push({ label: c, value: c });
              }
            }
            this.selectedReferenceType = this.utils.defaultIfBlank(this.cachedFilter.referenceType, DEFAULT_ITEM);
            this.filter.referenceType = this.selectedReferenceType;
          },
          err => {
            console.log('Error getting list of referenceType: ' + err);
          });
      });
    });
  }

  /**
   * get saved page from session storage and load data
   */
  private retrieveCachedRequest() {
    let page = this.storage.retrieve(STORAGE_INCIDENT_PAGE);
    if (this.utils.isNotBlank(page)) {
      let cachedPage = JSON.parse(page);
      this.page = {first: cachedPage.first, rows: cachedPage.rows, sortOrder: cachedPage.sortOrder, sortField: cachedPage.sortField };
      this.paginate();
    }

    let caseId = this.storage.retrieve(STORAGE_INCIDENT_CASE_ID);
    if (this.utils.isNotBlank(caseId)) {
      this.selectedCaseId = parseInt(caseId);
    }

    let requestStorage = this.storage.retrieve(STORAGE_INCIDENT_REQUEST);
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
  private findSelectedIncident() {
    if (!isNaN(this.selectedCaseId)) {
      for (let c of this.incidents) {
        if (c.caseId === this.selectedCaseId) {
          this.selectedIncident = c;
          break;
        }
      }
    }
  }

  private fillWorkplace() {
    this.workplaceItems = [];

    this.translate.get('label.Select').subscribe(v => {
      this.workplaceItems.push({ label: v, value: DEFAULT_ITEM });

      this.workplaceService.getWorkplaceItems().subscribe(
        response => {
          for (let c of response) {
            if (this.utils.isNotBlank(c)) {
              this.workplaceItems.push({ label: c.workplace, value: c.id });
            }
          }
          this.selectedWorkplaceId = this.utils.defaultIfBlank(this.cachedFilter.workplaceId, DEFAULT_ITEM_NUMBER);
        },
        err => {
          console.log('Error getting list of workplace: ' + err);
        });
    });
  }

}
