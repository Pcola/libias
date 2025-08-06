declare var ImgCompare: any;

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from 'ng2-translate';
import { CognitecService, ImageService, LoginService, Utils } from '../shared/service/index';
import { Image } from '../shared/model/image/index';
import { Message } from 'primeng/primeng';

import { GROWL_LIFE, GROWL_SEVERITY_ERROR, PAGE_WIDTH } from '../shared/constants';


@Component({
  moduleId: module.id,
  templateUrl: 'issue-detail.component.html',
})
export class IssueDetailComponent implements OnInit {
  growlLife = GROWL_LIFE;
  probeIdImageLoaded: boolean = false;
  galleryIdImageLoaded: boolean = false;
  probeIdImageMissing: boolean = false;
  galleryIdImageMissing: boolean = false;
  msgs: Message[] = [];
  busy: boolean = false;
  img1 = new Image;
  img2 = new Image;
  imgCmp: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private utils: Utils,
    private imageService: ImageService,
    private loginService: LoginService,
    private cognitecService: CognitecService
  ) { }

  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        let probeId = +params['probeId'] || 0;
        let galleryId = +params['galleryId'] || 0;
        this.initImgCompare(probeId, galleryId);
      });
  }

  actionBack() {
    window.history.back();
  }

  /**
   * add javascript file imgcompare.js as grid for searching(2 images + one compare canvas + buttons)
   * also add method for verifying and annotation
   */
  private initImgCompare(probeId: number, galleryId: number) {
    window.addEventListener('dragover', function(e: any) { e = e || event; e.preventDefault(); }, false);
    window.addEventListener('drop', function(e: any) { e = e || event; e.preventDefault(); }, false);

    var bodyRect = document.body.getBoundingClientRect();
    let imgCmpDivId = document.getElementById('imgcmp');
    let rect = imgCmpDivId.getBoundingClientRect();
    let sideMargin = (screen.width - PAGE_WIDTH) / 2;
    let shiftLeft = Math.abs(bodyRect.left) + rect.left;
    this.imgCmp = new ImgCompare(imgCmpDivId, shiftLeft, 250, sideMargin, window.innerHeight, 60, false);
    this.imgCmp.setDebug(0);
    this.imgCmp.disableVerify();
    this.imgCmp.enableSyncManual();
    this.imgCmp.disableDrop(0);
    this.imgCmp.disableDrop(1);
    this.imgCmp.enableLock(0);
    this.imgCmp.enableLock(1);
    //this.imgCmp.enableSync();

    this.loadProbeIdImage(probeId);
    this.loadGalleryIdImage(galleryId);
  }

  private loadProbeIdImage(oid: number) {
    this.imageService.getImage(oid).subscribe(
      resp => {
        if (resp && resp.imageData) {
          this.probeIdImageLoaded = true;
        } else {
          this.probeIdImageMissing = true;
        }
        if (this.galleryIdImageLoaded || this.galleryIdImageMissing) {
          this.busy = false;
        }
        if (this.probeIdImageLoaded) {
          this.loadImg(oid, 0, resp.imageData);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetProbeImage');
        console.log('Cannot get image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  private loadGalleryIdImage(oid: number) {
    this.imageService.getImage(oid).subscribe(
      resp => {
        if (resp && resp.imageData) {
          this.galleryIdImageLoaded = true;
        } else {
          this.galleryIdImageMissing = true;
        }
        if (this.probeIdImageLoaded || this.probeIdImageMissing) {
          this.busy = false;
        }
        if (this.galleryIdImageLoaded) {
          this.loadImg(oid, 1, resp.imageData);
        }
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetGalleryImage');
        console.log('Cannot get GalleryID image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

  /**
   *
   * @param oid - image id
   * @param id - which image (0 - left, 1 - right)
   * @param imageData - image
   *
   * get cognitec image
   */
  private loadImg(oid: number, id: number, imageData: any) {
    this.imgCmp.loadImg(id, 'data:image/png;base64,' + imageData);
    this.cognitecService.getImage(oid).subscribe(
      resp => {
        if (resp && resp.eyelx && resp.eyely && resp.eyerx && resp.eyery) {
          var obj = { left: { x: resp.eyerx, y: resp.eyery }, right: { x: resp.eyelx, y: resp.eyely } };
          this.imgCmp.annotateImg(id, obj);
        }
        this.imgCmp.disableLock(id);
      },
      err => {
        this.busy = false;
        this.utils.showGrowl(this.msgs, GROWL_SEVERITY_ERROR, 'label.Error', 'error.GetImage');
        console.log('Cannot get Cognitec image: ' + err);
        this.utils.isErrorForbidden(err);
      }
    );
  }

}
