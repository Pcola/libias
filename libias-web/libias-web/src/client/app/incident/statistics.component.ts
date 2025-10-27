import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Message } from 'primeng/primeng';
import { SessionStorageService } from 'ng2-webstorage';
import { TranslateService } from 'ng2-translate';

import { SiteStatisticsResponse } from '../shared/model/statistic/index';
import { IncidentCountResponse } from '../shared/model/incident/index';
import { Priority } from '../shared/model/priority/index';
import { CognitecService, IncidentService, LoginService, PriorityService, StatisticService, Utils } from '../shared/service/index';
import { SiteStatistics } from '../shared/model/statistic/sitestatistics.model';

import {
  GROWL_LIFE,
  GROWL_SEVERITY_ERROR,
  ROLE_ADMIN,
  ROLE_SUPEUSER,
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

@Component({
  moduleId: module.id,
  templateUrl: 'statistics.component.html',
})
export class StatisticsComponent implements OnInit {

  growlLife = GROWL_LIFE;

  msgs: Message[] = [];
  busy: boolean = false;
  msgConfirmation: string;
  priorities: Priority[];
  stats: IncidentCountResponse[];
  siteStats: SiteStatistics[];
  countImages: string;
  countIncidents: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private storage: SessionStorageService,
    private statisticService: StatisticService,
    private priorityService: PriorityService,
    private incidentService: IncidentService,
    private cognitecService: CognitecService,
    private utils: Utils,
    private loginService: LoginService,
  ) {
  }

  ngOnInit() {
    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_SUPEUSER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.busy = true;
      this.getCounts();
      this.getPriorities();
      this.getIncidentsCount();
      this.getSiteStatistics();
    }

    this.siteStats = [];
  }

  private getCounts() {
    this.incidentService.getCountAll().subscribe(
      response => {
        this.countIncidents = response.toLocaleString();
      }, error => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetStatistics');
      }
    );

    this.cognitecService.getCountAll().subscribe(
      response => {
        this.countImages = response.toLocaleString();
      }, error => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetStatistics');
      }
    );
  }

  private getPriorities() {
    this.priorityService.getPriorityItems().subscribe(
      response => {
        this.priorities = response;
      }, error => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetStatistics');
      });
  }

  private getIncidentsCount() {
    this.incidentService.getIncidentsTypeCount().subscribe(
      response => {
        this.stats = response;
      }, error => {
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetStatistics');
      }
    );
  }

  /**
   * get statistics about incidents, parse and display them
   */
  private getSiteStatistics() {
    let siteIds = new Array<string>();

    this.statisticService.getSiteStatistics().subscribe(
      response => {
        this.busy = false;

        for (let s of response) {
          let siteKey = s.siteId + '||' + s.priorityId;
          if (siteIds.indexOf(siteKey) < 0) {
            let stat = new SiteStatistics(
              s.siteId,
              s.siteName,
              s.priorityId,
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_OPEN, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_FILES_DOUBLET, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_FILES_NO_DOUBLET, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_NOT_CLEAR, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_NO_PROCESSING, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_FILES_NO_LINK, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_ADJUSTED, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_READY_TO_QA, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_DNUMBER_DIFF, response),
              this.getSiteCasesCount(s.siteId, s.priorityId, STATUS_ID_AUTO_ADJUSTED, response)
            );

            this.siteStats.push(stat);
            siteIds.push(siteKey);
          }
        }

      }, error => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetStatistics');
      }
    );

  }

  private getSiteCasesCount(id: string, priorityId: number, statusId: number, sites: SiteStatisticsResponse[]): number {
    for (let s of sites) {
      if (s.siteId === id && s.priorityId === priorityId && s.statusId === statusId) {
        return s.count;
      }
    }
    return 0;
  }

}
