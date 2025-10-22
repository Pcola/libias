import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { TranslateModule } from 'ng2-translate';
import { ButtonModule, DataTableModule, InputTextareaModule, SharedModule } from 'primeng/primeng';
import { ConfirmationService } from 'primeng/primeng';
import { IncidentService, HttpService, UserService, PersonService, StatisticService, ImageService, Logger,
  Utils, ReportService, LoginService, DataImportService, VersionService, WorkplaceService, CognitecService,
  NotificationService, PriorityService, 
  CompareDataHolderService}
  from './service/index';
import { AccountEventsService } from './account/account.events.service';

import { ScorePipe } from './pipe/index';
import {ImageTransformerComponent } from './image-transformer/image-transformer.component';
import { ImageComparerComponent } from './image-transformer/image-comparer/image-comparer.component';
import { ImgManComponent } from './image-transformer/img-man/img-man.component';

const PIPES = [ScorePipe];
const SERVICES = [IncidentService, UserService, PersonService, StatisticService, ImageService, HttpService, Logger, Utils, ReportService,
  LoginService, AccountEventsService, DataImportService, VersionService, WorkplaceService, CognitecService, NotificationService, PriorityService, CompareDataHolderService];

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */
@NgModule({
  imports: [CommonModule, RouterModule, FormsModule, TranslateModule, ButtonModule, InputTextareaModule],
  declarations: [...PIPES, ImageTransformerComponent, ImageComparerComponent, ImgManComponent],
  exports: [...PIPES, CommonModule, FormsModule, RouterModule, TranslateModule, ImageTransformerComponent, ImageComparerComponent, ImgManComponent],
  providers: [...SERVICES]
})
export class BAMFaceSharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: BAMFaceSharedModule,
      providers: [...SERVICES, ConfirmationService]
    };
  }
}
