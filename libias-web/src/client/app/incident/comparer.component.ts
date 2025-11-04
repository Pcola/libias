import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import { CognitecService, LoginService, Utils, ReportService, CompareDataHolderService } from '../shared/service/index';
import { Message } from 'primeng/primeng';
import { AnalyzePortraitRequest, VerificationPortraitsRequest } from '../shared/model/cognitec/index';
import { GROWL_LIFE, GROWL_SEVERITY_ERROR, ROLE_ADMIN, ROLE_COMPARER } from '../shared/constants';
import { SearchReportRequest } from '../shared/model/report/search-report-request';
import { ImageTransformerComponent } from '../shared/image-transformer/image-transformer.component';

declare var saveAs: any;

@Component({
  moduleId: module.id,
  templateUrl: 'comparer.component.html',
})
export class ComparerComponent implements OnInit {
  @ViewChild('imageTransformer') imageTransformer: ImageTransformerComponent;

  growlLife = GROWL_LIFE;
  msgs: Message[] = [];
  busy: boolean = false;
  exportVisible = false;
  exportEnabled = false;
  exportFullName = false;
  notes: string = '';
  
  compareDataHolderService: CompareDataHolderService;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private loginService: LoginService,
    private cognitecService: CognitecService,
    private reportService: ReportService,
    compareDataHolderService: CompareDataHolderService
  ) {
    this.compareDataHolderService = compareDataHolderService;
  }

  ngOnInit() {
    if (!this.loginService.isAuthenticated() || 
        !this.loginService.isAuthorized([ROLE_COMPARER, ROLE_ADMIN])) {
      this.loginService.logout(true);
    } else {
      setTimeout(() => {
        this.exportVisible = true;
      }, 100);
    }
  }
  annotateReq = (left: boolean, img: string, isCallingFirstTime: boolean = false): void => {
  const req = new AnalyzePortraitRequest();
  req.img = img;
  this.busy = true;

  this.cognitecService.findFaces(req).subscribe(
    resp => {
      this.busy = false;

      if (!resp.val || !resp.val.faces || !resp.val.faces[0]) {
        return;
      }

      const face = resp.val.faces[0];
      
      if (face.boundingBox) {
        this.imageTransformer.annotateCanvas(left, face.boundingBox.width, 
          face.boundingBox.height, face.boundingBox.alpha,
          face.boundingBox.center.x, face.boundingBox.center.y, 
          isCallingFirstTime ? 0 : 1, false);
      }

      this.imageTransformer.setAllLandmarks(left, face);
    },
    err => {
      this.busy = false;
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
      console.error('error findFaces: ' + err);
    }
  );
}

  

  eyesAnnotated = (left: boolean, eyeObj: any): void => {
    const distance = this.imageTransformer.computeAndUpdateEyeDistance(left, eyeObj, false);
    console.log('Eyes annotated for', (left ? 'left' : 'right'), 'image, distance:', distance);
    
    const leftEyes = this.imageTransformer.getAnnotatedEyes(true);
    const rightEyes = this.imageTransformer.getAnnotatedEyes(false);
    
    if (leftEyes && leftEyes.left && leftEyes.right && rightEyes && rightEyes.left && rightEyes.right) {
      this.performVerification();
    }
  }

  private performVerification() {
    const img1 = this.imageTransformer.getModifiedImage(true);
    const img2 = this.imageTransformer.getModifiedImage(false);
    
    if (!img1 || !img2) {
      this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.NoImgToCompare');
      console.warn('Cannot perform verification: one or both images missing');
      return;
    }

    let req = new VerificationPortraitsRequest();
    req.img1 = img1;
    req.img2 = img2;

    this.busy = true;
    this.exportEnabled = false;
    
    this.cognitecService.verificationPortraits(req).subscribe(
      resp => {
        this.busy = false;
        let score = this.utils.floorFigure(resp.val.score * 100.0, 2);
        console.debug('VerificationPortraits score: ' + score + '%');
        this.imageTransformer.setScore(resp.val.score);
        this.exportEnabled = true;
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.CallCognitec');
        console.error('error verificationPortraits: ' + err);
      }
    );
  }

  private exportSingle() {
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
          console.warn('Cannot download report: response size is 0');
        } else {
          saveAs(response, 'LIBIAS_Vergleich.pdf');
          this.busy = false;
        }
      }, 
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetReport');
        console.error('Cannot download report: ' + err);
      }
    );
  }
}