import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { TranslateModule } from 'ng2-translate';
import { Ng2Webstorage, SessionStorageService, LocalStorageService } from 'ng2-webstorage';
import { BAMFaceSharedModule } from './shared/bamface.shared.module';
import { BAMFaceModule } from './bamface.module';
import { NotificationService } from './shared/service/notification.service';
import { MessageService } from 'primeng/components/common/messageservice';
import { MessagesModule } from 'primeng/primeng';

@NgModule({
  imports: [BrowserModule, BrowserAnimationsModule, HttpModule, AppRoutingModule, Ng2Webstorage, BAMFaceModule, BAMFaceSharedModule.forRoot(),
    TranslateModule.forRoot(), MessagesModule],
  declarations: [AppComponent],
  providers: [{
      provide: LocationStrategy,
      useClass: HashLocationStrategy
  }, LocalStorageService, SessionStorageService, NotificationService, MessageService],
  bootstrap: [AppComponent]

})
export class AppModule { }
