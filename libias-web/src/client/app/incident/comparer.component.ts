import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import { LoginService, Utils, ReportService, CompareDataHolderService, GesService } from '../shared/service/index';
import { Message } from 'primeng/primeng';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR, ROLE_ADMIN, ROLE_COMPARER } from '../shared/constants';
import { SearchReportRequest } from '../shared/model/report/search-report-request';
import { ImageTransformerComponent } from '../shared/image-transformer/image-transformer.component';
import { GesAnalyseResponse } from '../shared/model/ges/ges-analyse-response.model';
import { DatePipe } from '@angular/common';

declare var saveAs: any;

@Component({
  moduleId: module.id,
  templateUrl: 'comparer.component.html',
  providers: [DatePipe]
})
export class ComparerComponent implements OnInit, AfterViewInit {
  @ViewChild('imageTransformer') imageTransformer: ImageTransformerComponent;

  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  busy: boolean = false;
  exportVisible = false;
  exportEnabled = false;
  exportFullName = false;
  notes: string = '';
  
  trafficLight: any;
  compareDataHolderService: CompareDataHolderService;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private loginService: LoginService,
    private gesService: GesService,
    private reportService: ReportService,
    private datePipe: DatePipe,
    compareDataHolderService: CompareDataHolderService
  ) {
    this.compareDataHolderService = compareDataHolderService;
  }

  ngOnInit() {
    if (! this.loginService.isAuthenticated() || 
        !this.loginService.isAuthorized([ROLE_COMPARER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      setTimeout(() => {
        this.exportVisible = true;
      }, 100);
    }
  }

  ngAfterViewInit() {
    if (this.imageTransformer) {
      this.imageTransformer.setAnnotateCnvReq(this.annotateReq.bind(this));
      this.imageTransformer.setPrimaryActionReq(this.gesVerifyOriginal.bind(this));
      this.imageTransformer.setSecondaryActionReq(this.gesVerifyModified.bind(this));
    }
  }

  private gesVerifyOriginal(): void {
    const img1 = this.imageTransformer.getOriginalImage(true);
    const img2 = this.imageTransformer.getOriginalImage(false);

    if (! img1 || !img2) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ImagesNotLoaded');
      return;
    }

    this.busy = true;
    this.exportEnabled = false;

    this.gesService.compareEmbeddings(img1, img2).subscribe(
      resp => {
        this.busy = false;
        if (resp && resp.score !== undefined) {
          var score = parseFloat(this.utils.floorFigure(resp.score, 2));
          this.imageTransformer.setScore(score, resp.score);
          this.exportEnabled = true;
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GesCompareFailed');
      }
    );
  }

  private gesVerifyModified(): void {
    const img1 = this.imageTransformer.getModifiedImage(true);
    const img2 = this.imageTransformer.getModifiedImage(false);

    if (!img1 || !img2) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.ImagesNotLoaded');
      return;
    }

    this.busy = true;
    this.exportEnabled = false;

    this.gesService.compareEmbeddings(img1, img2).subscribe(
      resp => {
        this.busy = false;
        if (resp && resp.score !== undefined) {
          var score = parseFloat(this.utils.floorFigure(resp.score, 2));
          this.imageTransformer.setScore(score, resp.score);
          this.exportEnabled = true;
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GesCompareFailed');
      }
    );
  }

  annotateReq = (left: boolean, img: string, isCallingFirstTime: boolean = false): void => {
    this.busy = true;

    this.gesService.analyzeImage(img).subscribe(
      (resp: GesAnalyseResponse) => {
        this.busy = false;

        if (! resp || !resp.embedding) {
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.NoFaceDetected');
          return;
        }

        if (resp.bbox && resp.bbox.length >= 5) {
          const x_top = resp.bbox[0];
          const y_top = resp.bbox[1];
          const x_bottom = resp.bbox[2];
          const y_bottom = resp.bbox[3];
          const alpha = resp.bbox[4] || 0;
          
          const width = x_bottom - x_top;
          const height = y_bottom - y_top;
          const centerX = x_top + width / 2;
          const centerY = y_top + height / 2;
          
          this.imageTransformer.annotateCanvas(
            left, 
            width, 
            height, 
            alpha,
            centerX, 
            centerY, 
            isCallingFirstTime ? 0 : 1, 
            false
          );
        }

        if (resp.landmarks && resp.landmarks.length >= 10) {
          const landmarks = {
            leftEye: { x: resp.landmarks[0], y: resp.landmarks[1] },
            rightEye: { x: resp.landmarks[2], y: resp.landmarks[3] },
            noseTip: { x: resp.landmarks[4], y: resp.landmarks[5] },
            leftMouthCorner: { x: resp.landmarks[6], y: resp.landmarks[7] },
            rightMouthCorner: { x: resp.landmarks[8], y: resp.landmarks[9] }
          };
          this.imageTransformer.setAllLandmarks(left, landmarks);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GesAnalysisFailed');
      }
    );
  };

  eyesAnnotated = (left: boolean, eyeObj: any): void => {
    const distance = this.imageTransformer.computeAndUpdateEyeDistance(left, eyeObj, false);    
  }

  exportSingle() {
    this.busy = true;
    let comparerReportRequest = new SearchReportRequest();
    comparerReportRequest.marisImageOptimized = this.imageTransformer.getOriginalImage(true);
    comparerReportRequest.extImageOptimized = this.imageTransformer.getOriginalImage(false);
    comparerReportRequest.note = this.notes;
    comparerReportRequest.lang = this.translate.getBrowserLang();
    comparerReportRequest.isFullName = this.exportFullName;
    comparerReportRequest.isFull = false;
    comparerReportRequest.isWord = false;

    this.reportService.createComparerExport(comparerReportRequest).subscribe(
      response => {
        if (response && response.size === 0) {
          this.busy = false;
          this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        } else {
          saveAs(response, 'LIBIAS_Vergleich.pdf');
          this.busy = false;
        }
      }, 
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
      }
    );
  }

  getColorStyle(): any {
    if (! this.trafficLight) return {};
    
    switch (this.trafficLight.color) {
      case 'red':
        return { 'background-color': '#ff4444', 'color': '#fff' };
      case 'yellow':
        return { 'background-color': '#ffdd44', 'color': '#000' };
      case 'green':
        return { 'background-color': '#44ff44', 'color': '#000' };
      default:
        return { 'background-color': '#ccc', 'color': '#000' };
    }
  }
}