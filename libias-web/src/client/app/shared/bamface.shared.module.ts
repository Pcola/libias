import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { TranslateModule } from 'ng2-translate';
import { DataTableModule, SharedModule } from 'primeng/primeng';
import { ConfirmationService } from 'primeng/primeng';
import { IncidentService, HttpService, UserService, PersonService, StatisticService, ImageService, Logger,
  Utils, ReportService, LoginService, DataImportService, VersionService, WorkplaceService, CognitecService,
  NotificationService, PriorityService }
  from './service/index';
import { AccountEventsService } from './account/account.events.service';

import { ScorePipe } from './pipe/index';

const PIPES = [ScorePipe];
const SERVICES = [IncidentService, UserService, PersonService, StatisticService, ImageService, HttpService, Logger, Utils, ReportService,
  LoginService, AccountEventsService, DataImportService, VersionService, WorkplaceService, CognitecService, NotificationService, PriorityService];

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */
@NgModule({
  imports: [CommonModule, RouterModule],
  declarations: [PIPES],
  exports: [PIPES, CommonModule, FormsModule, RouterModule, TranslateModule],
  providers: [SERVICES]
})
export class BAMFaceSharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: BAMFaceSharedModule,
      providers: [SERVICES, ConfirmationService]
    };
  }
}
