import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BAMFaceRoutes } from './bamface.routes';

@NgModule({
  imports: [
    RouterModule.forRoot([
      ...BAMFaceRoutes
    ])
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
