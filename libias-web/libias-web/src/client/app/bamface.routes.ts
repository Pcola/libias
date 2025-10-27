import { Route } from '@angular/router';

import { IncidentListComponent, IncidentItemComponent, AussenstellerListComponent, AussenstellerDetailComponent, IssueDetailComponent, StatisticsComponent, ComparerComponent, SearcherComponent }
  from './incident/index';
import { UserListComponent, ChangePasswordComponent } from './user/index';
import { LoginComponent } from './login/login.component';
import { DataImportComponent } from './import/data-import.component';
import { AboutComponent } from './about/about.component';

export const BAMFaceRoutes: Route[] = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'incident-list', component: IncidentListComponent },
  { path: 'incident-item/:id', component: IncidentItemComponent },
  { path: 'aussensteller-list', component: AussenstellerListComponent },
  { path: 'aussensteller-detail/:id', component: AussenstellerDetailComponent },
  { path: 'statistics', component: StatisticsComponent },
  { path: 'comparer', component: ComparerComponent },
  { path: 'searcher', component: SearcherComponent },
  { path: 'issue-detail', component: IssueDetailComponent },
  { path: 'user-list', component: UserListComponent },
  { path: 'password-change', component: ChangePasswordComponent },
  { path: 'data-import', component: DataImportComponent },
  { path: 'about', component: AboutComponent },
  { path: 'login', component: LoginComponent }
];
