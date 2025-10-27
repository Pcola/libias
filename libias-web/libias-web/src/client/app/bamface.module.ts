import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BAMFaceSharedModule } from './shared/bamface.shared.module';
import { HeaderComponent } from './shared/header/header.component';
import { ToolbarComponent } from './shared/toolbar/toolbar.component';
import { IncidentListComponent, IncidentItemComponent, AussenstellerListComponent, AussenstellerDetailComponent, IssueDetailComponent, StatisticsComponent, ComparerComponent, SearcherComponent }
  from './incident/index';
import { UserListComponent, ChangePasswordComponent } from './user/index';
import { LoginComponent } from './login/login.component';
import { DataImportComponent } from './import/data-import.component';
import { AboutComponent } from './about/about.component';

import { ListboxModule, DropdownModule, InputTextModule, CalendarModule, PasswordModule, InputTextareaModule, CheckboxModule } from 'primeng/primeng';
import { ButtonModule } from 'primeng/primeng';
import { PanelModule } from 'primeng/primeng';
import { DataTableModule, SharedModule } from 'primeng/primeng';
import { GrowlModule, ProgressBarModule } from 'primeng/primeng';
import { DialogModule, ConfirmDialogModule } from 'primeng/primeng';

import { CircleComponent } from 'ng2-spin-kit/dist/spinners';
import { InputTrimDirective } from 'ng2-trim-directive';

const PRIMENG_INPUT = [DropdownModule, ListboxModule, InputTextModule, CalendarModule, PasswordModule, InputTextareaModule, CheckboxModule];
const PRIMENG_BUTTON = [ButtonModule];
const PRIMENG_DATA = [DataTableModule];
const PRIMENG_PANEL = [PanelModule];
const PRIMENG_MISC = [GrowlModule, ProgressBarModule];
const PRIMENG_OVERLAY = [DialogModule, ConfirmDialogModule];
const PRIMENG_MODULES = [PRIMENG_INPUT, PRIMENG_BUTTON, PRIMENG_DATA, PRIMENG_PANEL, PRIMENG_MISC, PRIMENG_OVERLAY, SharedModule];

const LOGIN_COMPONENT = [LoginComponent];
const INCIDENT_COMPONENTS = [IncidentListComponent, IncidentItemComponent, AussenstellerListComponent, AussenstellerDetailComponent, IssueDetailComponent, StatisticsComponent, ComparerComponent,
  SearcherComponent];
const USER_COMPONENTS = [UserListComponent, ChangePasswordComponent];
const IMPORT_COMPONENT = [DataImportComponent];
const SHARED_COMPONENTS = [HeaderComponent, ToolbarComponent];
const BAMFACE_COMPONENTS = [INCIDENT_COMPONENTS, USER_COMPONENTS, LOGIN_COMPONENT, SHARED_COMPONENTS, IMPORT_COMPONENT, AboutComponent];

@NgModule({
  imports: [CommonModule, BAMFaceSharedModule, PRIMENG_MODULES],
  declarations: [CircleComponent, InputTrimDirective, BAMFACE_COMPONENTS],
  exports: [BAMFACE_COMPONENTS]
})
export class BAMFaceModule { }
