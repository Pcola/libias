declare var ImgCompare: any;
declare var saveAs: any;

import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import { CognitecService, LoginService, Utils, ReportService } from '../shared/service/index';
import { Message } from 'primeng/primeng';
import { AnalyzePortraitRequest, VerificationPortraitsRequest } from '../shared/model/cognitec/index';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR, PAGE_WIDTH, ROLE_ADMIN, ROLE_COMPARER } from '../shared/constants';
import { SearchReportRequest } from '../shared/model/report/search-report-request';

@Component({
  moduleId: module.id,
  templateUrl: 'comparer.component.html',
})
export class ComparerComponent implements OnInit, AfterViewInit {
  @ViewChild('notesTextarea') notesTextarea: ElementRef;

  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  busy: boolean = false;
  img1 = new Image();
  img2 = new Image();
  imgCmp: any;
  exportVisible = false;
  exportEnabled = false;
  exportFullName = false;
  notes: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private loginService: LoginService,
    private cognitecService: CognitecService,
    private reportService: ReportService
  ) { }

  ngOnInit() {
    if (!this.loginService.isAuthenticated() || !this.loginService.isAuthorized([ROLE_COMPARER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      this.initImgCompare();
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.exportVisible = true;
    });
  }

  /**
   * add javascript file imgcompare.js as grid for searching(2 images + one compare canvas + buttons)
   * also add method for verifying and annotation
   */
  private initImgCompare() {
    window.addEventListener('dragover', function(e: any) { e = e || event; e.preventDefault(); }, false);
    window.addEventListener('drop', function(e: any) { e = e || event; e.preventDefault(); }, false);

    var bodyRect = document.body.getBoundingClientRect();
    let imgCmpDivId = document.getElementById('imgcmp');
    let rect = imgCmpDivId.getBoundingClientRect();
    let sideMargin = (screen.width - PAGE_WIDTH) / 2;
    let shiftLeft = Math.abs(bodyRect.left) + rect.left;
    this.imgCmp = new ImgCompare(imgCmpDivId, shiftLeft, 200, sideMargin, window.innerHeight, 200, false);
    this.imgCmp.setDebug(0);

    this.imgCmp.setVerifyReq(this.verifyReq.bind(this));
    this.imgCmp.enableAnnotation(0);
    this.imgCmp.enableAnnotation(1);
    this.imgCmp.setAnnotateCnvReq(this.annotateReq.bind(this));
    //this.imgCmp.enableVerify();

    this.imgCmp.enableEyeLines();
    this.imgCmp.enableSyncManual();
    this.imgCmp.enableDrop(0);
    this.imgCmp.enableDrop(1);
    this.imgCmp.disableLock(0);
    this.imgCmp.disableLock(1);
  }

  private exportSingle() {
    this.busy = true;
    let comparerReportRequest = new SearchReportRequest();
    comparerReportRequest.marisImageOptimized = this.imgCmp.getOriginalImage(0);
    comparerReportRequest.extImageOptimized = this.imgCmp.getOriginalImage(1);
    comparerReportRequest.note = this.notes;
    comparerReportRequest.lang = this.translate.getBrowserLang();
    comparerReportRequest.isFullName = this.exportFullName;
    comparerReportRequest.isFull = false;
    comparerReportRequest.isWord = false;

    this.reportService.createComparerExport(comparerReportRequest).subscribe(
      response => {
        if (response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
          console.log('Cannot download report: size 0');
        } else {
          saveAs(response, 'LIBIAS_Vergleich.pdf');
          this.busy = false;
        }
      }, err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        console.log('Cannot download report: ' + err);
      }
    );
  }

  /**
   *
   * @param _img1
   * @param _img2
   *
   * call cognitec to verify 2 images - set score of verification
   */
  private verifyReq(_img1: string, _img2: string) {
    let req = new VerificationPortraitsRequest();
    req.img1 = _img1;
    req.img2 = _img2;

    this.busy = true;
    this.exportEnabled = false;
    this.cognitecService.verificationPortraits(req).subscribe(
      resp => {
        this.busy = false;
        var score = this.utils.floorFigure(resp.val.score * 100.0, 2);
        console.debug('VerificationPortraits set score: ' + score);
        this.imgCmp.setScore(score);
        this.exportEnabled = true;
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
        console.error('error verificationPortraits: ' + err);
      }
    );
  }

  /**
   *
   * @param _ID
   * @param _img
   *
   * call cognitec to put image to best position
   */
  private annotateReq(_ID: number, _img: string) {
    let req = new AnalyzePortraitRequest();
    req.img = _img;
    this.busy = true;

    this.cognitecService.analyzePortrait(req).subscribe(
      resp => {
        this.busy = false;
        let portrait = resp.val.portraitCharacteristics;
        if (portrait && portrait.leftEye && portrait.rightEye) {
          var res = {
            left: {x: portrait.rightEye.x, y: portrait.rightEye.y, set: 1},
            right: {x: portrait.leftEye.x, y: portrait.leftEye.y, set: 1}
          };

          this.imgCmp.annotateCnv(_ID, res);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
        console.error('error verificationPortraits: ' + err);
      }
    );
  }

  private getComponentPos(idx: number): any {
    let pos = this.imgCmp.getPositionCmp();
    if (idx === 0) {
      return {
        'left': pos.x + 'px',
        'top': (pos.y + pos.h + 10) + 'px',
        'width': pos.w + 'px',
        'height': '100px'
      };
    } else if (idx === 1) {
      return {
        'left': pos.x + 'px',
        'top': (pos.y + pos.h + 120) + 'px',
        'width': pos.w + 'px'
      };
    } else if (idx === 2) {
      return {
        'left': pos.x + 'px',
        'top': (pos.y + pos.h + 160) + 'px',
        'width': pos.w + 'px',
        'text-align': 'center'
      };
    } else {
      return {};
    }
  }
}
