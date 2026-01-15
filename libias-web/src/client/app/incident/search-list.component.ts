import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { TranslateService } from 'ng2-translate';
import { SearchRequestSearchRequest } from '../shared/model/search-request/search-request-search-request.model';
import { SearchRequestListItem } from '../shared/model/search-request/search-request-list-item.model';
import { SearchRequestService, LoginService, WorkplaceService, Utils } from '../shared/service/index';
import { DataTable, Message, SelectItem } from 'primeng/primeng';
import { SessionStorageService } from 'ng2-webstorage';

import {
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  ROLE_ADMIN,
  ROLE_SEARCHER,
  ROLE_SUPERUSER,
  ROLE_USER
} from '../shared/constants';
import { DatePipe } from '@angular/common';

const DEFAULT_ITEM = '--';
const STORAGE_SEARCH_REQUEST = 'STORAGE_SEARCH_REQUEST';
const STORAGE_SEARCH_PAGE = 'STORAGE_SEARCH_PAGE';
const STORAGE_SEARCH_SELECTED_ID = 'STORAGE_SEARCH_SELECTED_ID';

@Component({
  moduleId: module.id,
  templateUrl: 'search-list.component.html'
})
export class SearchListComponent implements OnInit {

  searchRequests: SearchRequestListItem[];
  workplaceItems: SelectItem[];
  filter: SearchRequestSearchRequest;
  cachedFilter: SearchRequestSearchRequest;
  busy: boolean = false;
  msgs: Message[] = [];
  growlLife = GROWL_LIFE;
  page: any;
  selectedSearchRequest: SearchRequestListItem;
  defaultRows: number = 20;
  selectedRequestId: number;
  selectedWorkplaceId: string;
  totalSearchRequestsCount: number = 0;

  @ViewChild('dt') dataTable: DataTable;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private searchRequestService: SearchRequestService,
    private translate: TranslateService,
    private storage: SessionStorageService,
    private utils: Utils,
    private loginService: LoginService,
    private workplaceService: WorkplaceService,
    private datePipe: DatePipe
  ) { }

  ngOnInit() {
    this.selectedSearchRequest = new SearchRequestListItem();
    this.filter = new SearchRequestSearchRequest();
    this.cachedFilter = new SearchRequestSearchRequest();

    if (!this.loginService.isAuthenticated()) {
      this.loginService.logout(true);
    } else if (!this.loginService.isAuthorized([ROLE_ADMIN, ROLE_USER, ROLE_SUPERUSER, ROLE_SEARCHER])) {
      this.loginService.logout(true);
    } else {
      this.busy = true;
      this.retrieveCachedRequest();
      this.fillWorkplace();
      this.search();
    }
  }

  actionSearch() {
    this.storage.clear(STORAGE_SEARCH_REQUEST);
    this.storage.clear(STORAGE_SEARCH_PAGE);
    this.storage.clear(STORAGE_SEARCH_SELECTED_ID);
    this.selectedRequestId = undefined;
    this.dataTable.first = 0;
    this.utils.trimObjectAttibutes(this.filter);
    this.search();
  }

  navigateToSearcher(requestId: number) {
    this.storage.store(STORAGE_SEARCH_REQUEST, JSON.stringify(this.filter));
    this.storage.store(STORAGE_SEARCH_PAGE, JSON.stringify(this.page));
    this.selectedRequestId = requestId;
    this.findSelectedSearchRequest();
    this.storage.store(STORAGE_SEARCH_SELECTED_ID, requestId);
    this.router.navigate(['/searcher'], { queryParams: { requestId: requestId } });
  }

  clear() {
    this.storage.clear(STORAGE_SEARCH_REQUEST);
    this.storage.clear(STORAGE_SEARCH_PAGE);
    this.storage.clear(STORAGE_SEARCH_SELECTED_ID);
    this.filter = new SearchRequestSearchRequest();
    this.selectedWorkplaceId = DEFAULT_ITEM;
    this.page = { first: 0, rows: this.defaultRows, sortField: null, sortOrder: null };
    this.totalSearchRequestsCount = 0;
    this.selectedRequestId = undefined;
    this.selectedSearchRequest = undefined;
    this.dataTable.reset();
  }

  changedWorkplaceId(evt: any) {
    this.filter.departmentId = evt.value;
  }

  getWorkplaceName(workplaceId: string): string {
    if (!workplaceId || !this.workplaceItems) {
      return workplaceId;
    }
    for (let item of this.workplaceItems) {
      if (item.value === workplaceId) {
        return item.label;
      }
    }
    return workplaceId;
  }

  onLazyPage(e: any) {
    if (this.busy === false) {
      this.page = e;
      this.storage.store(STORAGE_SEARCH_PAGE, JSON.stringify(this.page));
      this.search();
    }
  }

  onRowSelect(e: any) {
    if (e.data && e.data.requestId) {
      this.storage.store(STORAGE_SEARCH_SELECTED_ID, e.data.requestId);
      this.selectedRequestId = e.data.requestId;
    }
  }

  changeStyleOfRow(requestId: number) {
    if (!isNaN(requestId) && (!this.selectedSearchRequest || this.selectedSearchRequest.requestId !== requestId)) {
      for (let sr of this.searchRequests) {
        if (sr.requestId === requestId) {
          this.selectedSearchRequest = sr;
          break;
        }
      }
    }
  }

  private search() {
    if (this.filter.departmentId === DEFAULT_ITEM) {
      this.filter.departmentId = null;
    }

    let dateFromStr = null;
    let dateToStr = null;
    
    if (this.filter.dateFrom) {
      if (typeof this.filter.dateFrom === 'object') {
        dateFromStr = this.datePipe.transform(this.filter.dateFrom, 'dd.MM.yyyy');
      } else {
        dateFromStr = this.filter.dateFrom;
      }
    }
    
    if (this.filter.dateTo) {
      if (typeof this.filter.dateTo === 'object') {
        dateToStr = this.datePipe.transform(this.filter.dateTo, 'dd.MM.yyyy');
      } else {
        dateToStr = this.filter.dateTo;
      }
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

    this.filter.dateFrom = dateFromStr;
    this.filter.dateTo = dateToStr;

    this.busy = true;
    this.searchRequestService.search(this.filter).subscribe(
      response => {
        this.searchRequests = response.searchRequests;
        this.totalSearchRequestsCount = response.totalCount;
        this.busy = false;
        this.findSelectedSearchRequest();
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetSearchRequests');
        console.log('Error getting search requests: ' + err);
        this.utils.isErrorForbidden(err);
      });
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
          this.selectedWorkplaceId = this.utils.defaultIfBlank(this.cachedFilter.departmentId, DEFAULT_ITEM);
        },
        err => {
          console.log('Error getting list of workplace: ' + err);
        });
    });
  }

  private retrieveCachedRequest() {
    let page = this.storage.retrieve(STORAGE_SEARCH_PAGE);
    if (this.utils.isNotBlank(page)) {
      let cachedPage = JSON.parse(page);
      this.page = { first: cachedPage.first, rows: cachedPage.rows, sortOrder: cachedPage.sortOrder, sortField: cachedPage.sortField };
      this.paginate();
    }

    let requestId = this.storage.retrieve(STORAGE_SEARCH_SELECTED_ID);
    if (this.utils.isNotBlank(requestId)) {
      this.selectedRequestId = parseInt(requestId);
    }

    let requestStorage = this.storage.retrieve(STORAGE_SEARCH_REQUEST);
    if (this.utils.isNotBlank(requestStorage) && this.utils.isValidJSON(requestStorage)) {
      this.cachedFilter = JSON.parse(requestStorage);
      this.filter.requestId = this.cachedFilter.requestId;
      this.filter.dateFrom = this.cachedFilter.dateFrom;
      this.filter.dateTo = this.cachedFilter.dateTo;
      this.filter.username = this.cachedFilter.username;
      this.filter.departmentId = this.cachedFilter.departmentId;
    } else {
      this.cachedFilter = new SearchRequestSearchRequest();
    }
  }

  private paginate() {
    if (this.utils.isNotBlank(this.page) && this.page.first && this.page.rows) {
      let paging = {
        first: this.page.first,
        rows: this.page.rows,
      };

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

  private findSelectedSearchRequest() {
    if (!isNaN(this.selectedRequestId)) {
      for (let sr of this.searchRequests) {
        if (sr.requestId === this.selectedRequestId) {
          this.selectedSearchRequest = sr;
          break;
        }
      }
    }
  }
}