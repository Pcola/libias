import { AfterViewInit, ChangeDetectorRef, Component, HostListener, Input, ViewChild } from '@angular/core';
import { BaseTransformerComponent } from './base-transformer-component';
import { CompareDataHolderService, LoginService, TrafficLightService, UserService, Utils } from '../service/index';
import { ImageComparerComponent } from './image-comparer/image-comparer.component';
import { ImgManComponent } from './img-man/img-man.component';
import { Transformation } from '../model/case/transformation.model';
import { TrafficLight } from '../model/traffic-light/traffic-light.model';
import { UserInfoResponse } from '../model/user';

@Component({
  selector: 'app-image-transformer',
  templateUrl: 'app/shared/image-transformer/image-transformer.component.html',
  styleUrls: ['app/shared/image-transformer/image-transformer.component.css']
})
export class ImageTransformerComponent extends BaseTransformerComponent implements AfterViewInit {

  @Input()
  annotateReqFun: (left: boolean, img: string, isCallingFirstTime: boolean) => void;

  @Input()
  eyesAnnotatedFun: (left: boolean, obj: any) => void;

  @Input()
  inSearchTab: boolean = true;

  @Input()
  public compareDataHolderService: CompareDataHolderService;

  @ViewChild('imgMan1') imgMan1: ImgManComponent;
  @ViewChild('imgMan2') imgMan2: ImgManComponent;
  @ViewChild('imageComparer') imageComparer: ImageComparerComponent;

  score: string = '';
  note: string = '';
  displayNote: boolean = true;

  showVerifyButtons = false;
  annotateCnvReqFun: (left: boolean, img: string, isCallingFirstTime: boolean) => void;
  trafficLight: any;
  trafficLightForCurrentRole: TrafficLight;
  quality: number;

  private primaryActionReq: () => void;
  private secondaryActionReq: () => void;

  constructor(
    private utilsService: Utils,
    private cd: ChangeDetectorRef,
    private trafficLightService: TrafficLightService,
    private userService: UserService,
    private loginService: LoginService
  ) {
    super();
  }

  ngAfterViewInit(): void {
    this.displayNote = this.inSearchTab;

    this.width = this.getCompareWidth();
    this.height = this.getCompareHeight();

    this.betweenEyes = this.width / 4;
    this.fromLeft = (this.width - this.betweenEyes) / 2;
    this.fromTop = this.height / 2.5;

    this.loadTrafficLightForCurrentRole();

    this.cd.detectChanges();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.width = this.getCompareWidth();
    this.height = this.getCompareHeight();
  }

  private loadTrafficLightForCurrentRole() {
    this.userService.getLoggedUserInfo().subscribe(
      (userInfo: UserInfoResponse) => {
        if (userInfo && userInfo.userRoleCollection && userInfo.userRoleCollection.length > 0) {
          const roleId = userInfo.userRoleCollection[0].roleId;

          this.trafficLightService.getTrafficLight(roleId).subscribe(
            (trafficLight: TrafficLight) => {
              this.trafficLightForCurrentRole = trafficLight;
            },
            err => {
              this.trafficLightForCurrentRole = null;
            }
          );
        }
      },
      err => {
        this.trafficLightForCurrentRole = null;
      }
    );
  }

  changeSynchronization(): void {
    this.synchronized = !this.synchronized;
    this.imgMan1.changeSynchronization();
    this.imgMan2.changeSynchronization();
  }

  changeMonochrome(): void {
    this.monochrome = !this.monochrome;
    this.imgMan1.changeMonochrome();
    this.imgMan2.changeMonochrome();
  }

  changeHelpLinesVisibility(): void {
    this.imageComparer.changeHelpLinesVisibility();
    this.imgMan1.changeHelpLinesVisibility();
    this.imgMan2.changeHelpLinesVisibility();
  }

  clearAndReset(): void {
    this.imgMan1.loadImage('');
    this.imgMan2.loadImage('');
    this.setScore(0);

    this.imgMan1.setTableVisibility(false);
    this.imgMan2.setTableVisibility(false);
    this.resetImage(null);

    this.resetEyeDistance(true);
    this.resetEyeDistance(false);

    if (!this.inSearchTab) {
      this.note = '';
      this.displayNote = false;
    }

    this.trafficLight = null;
  }

  resetEyeDistance(left: boolean): void {
    if (left) {
      this.imgMan1.setEyeDistance('');
    } else {
      this.imgMan2.setEyeDistance('');
    }
  }

  resetImage(left: boolean): void {
    if (left === null) {
      this.imgMan1.resetCanvas();
      this.imgMan2.resetCanvas();
    } else {
      if (left) {
        this.imgMan1.resetCanvas();
      } else {
        this.imgMan2.resetCanvas();
      }
    }
  }

  loadImage(left: boolean, src: string): void {
    if (left) {
      this.imgMan1.loadImage(src);
    } else {
      this.imgMan2.loadImage(src);
    }
  }

  loadImageAnnotated(left: boolean, annotation: any): void {
    if (left) {
      this.imgMan1.annotateImg(annotation);
    } else {
      this.imgMan2.annotateImg(annotation);
    }
  }

  loadImageTransformed(left: boolean, src: string, transformation: Transformation): void {
    if (left) {
      this.imgMan1.loadScaledImage(src, transformation);
    } else {
      this.imgMan2.loadScaledImage(src, transformation);
    }
  }

  annotateCanvas(left: boolean, width: number, height: number, alpha: number, centerx: number, centery: number,
    imgType: number, normalize: boolean) {
    if (left) {
      this.imgMan1.annotateCanvas(width, height, alpha, centerx, centery, imgType, normalize);
    } else {
      this.imgMan2.annotateCanvas(width, height, alpha, centerx, centery, imgType, normalize);
    }
  }

  getOriginalImage(left: boolean): string {
    return left ? this.imgMan1.getOriginalImage() : this.imgMan2.getOriginalImage();
  }

  getModifiedImage(left: boolean): string {
    return left ? this.imgMan1.getTransformedImage() : this.imgMan2.getTransformedImage();
  }

  setScore(score: number, quality: number = 0): void {
    this.quality = quality;
    this.score = this.utilsService.floorFigure(score, 2);

    if (score > 0 && this.trafficLightForCurrentRole) {
      this.trafficLight = this.getTrafficLightForScore(score, quality);
    } else {
      this.trafficLight = null;
    }
  }

  private getTrafficLightForScore(score: number, quality: number): any {
    const tl = this.trafficLightForCurrentRole;
    let color: string;
    let comment: string;

    if (score < tl.scoreFrom) {
      color = 'red';
      comment = tl.commentRed;
    }
    else if (score > tl.scoreTo) {
      color = 'green';
      comment = tl.commentGreen;
    }
    else {
      color = 'yellow';
      comment = tl.commentYellow;
    }

    return { color, comment, score, quality };
  }

  getColorStyle(): any {
    if (!this.trafficLight) return {};

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

  getAnnotatedEyes(left: boolean): any {
    return left ? this.imgMan1.getAnnotatedEyes() : this.imgMan2.getAnnotatedEyes();
  }

  getNote(): string {
    return this.note;
  }

  getHeight(): number {
    const div: HTMLElement = document.getElementById('div');
    if (!div) return 0;
    return div.offsetWidth * 4 / 3;
  }

  getCompareHeight(): number {
    const div: HTMLElement = document.getElementById('compare-div');
    if (!div) return 0;
    return div.offsetWidth * 4 / 3;
  }

  getCompareWidth(): number {
    const div = document.getElementById('compare-div');
    if (!div) return 0;
    return div.offsetWidth - 20;
  }

  getWidth(): number {
    const div = document.getElementById('div');
    if (!div) return 0;
    return div.offsetWidth;
  }

  setTableData(left: boolean, data: any[]): void {
    if (left) {
      this.imgMan1.setTableData(data);
    } else {
      this.imgMan2.setTableData(data);
    }
  }

  changeInfoTableVisibility(left: boolean, visible: boolean): void {
    if (left) {
      this.imgMan1.setTableVisibility(visible);
    } else {
      this.imgMan2.setTableVisibility(visible);
    }
  }

  setNote(note: string): void {
    this.note = note;
  }

  getImageTransformation(left: boolean): Transformation {
    return left ? this.imgMan1.getTransformation(true) : this.imgMan2.getTransformation(false);
  }

  computeAndUpdateEyeDistance(left: boolean, obj: any, applyScale: boolean = false): number {
    const scale: number = left ? this.imgMan1.getScale() : this.imgMan2.getScale();
    let numDistance: number = this.computeEyeDistance(obj);

    if (applyScale && scale) {
      numDistance /= scale;
    }
    const distance: string = numDistance.toFixed(0);

    if (left) {
      this.imgMan1.setEyeDistance(distance);
    } else {
      this.imgMan2.setEyeDistance(distance);
    }

    return Number(distance);
  }

  updateEyeDistance(left: boolean, distance: number): void {
    if (left) {
      this.imgMan1.setEyeDistance(distance.toString());
    } else {
      this.imgMan2.setEyeDistance(distance.toString());
    }
  }

  imageDropped(): void {
    if (!this.displayNote) {
      this.displayNote = true;
    }
  }

  private computeEyeDistance(res: any): number {
    const a: number = res.left.x - res.right.x;
    const b: number = res.left.y - res.right.y;

    return Math.sqrt(a * a + b * b);
  }

  setAnnotateCnvReq(callback: (left: boolean, img: string, isCallingFirstTime: boolean) => void) {
    this.annotateCnvReqFun = callback;
  }

  enableVerifyButtons() {
    this.showVerifyButtons = true;
  }

  setAllLandmarks(left: boolean, faceLocation: any) {
    if (left) {
      this.imgMan1.setAllLandmarksFromFaceLocation(faceLocation);
    } else {
      this.imgMan2.setAllLandmarksFromFaceLocation(faceLocation);
    }
  }

  setPrimaryActionReq(fn: () => void): void {
    this.primaryActionReq = fn;
  }

  setSecondaryActionReq(fn: () => void): void {
    this.secondaryActionReq = fn;
  }

  primaryAction(): void {
    if (this.primaryActionReq) {
      this.primaryActionReq();
    }
  }

  secondaryAction(): void {
    if (this.secondaryActionReq) {
      this.secondaryActionReq();
    }
  }
}