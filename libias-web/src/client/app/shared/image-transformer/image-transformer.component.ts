import { AfterViewInit, ChangeDetectorRef, Component, Input, ViewChild } from '@angular/core';
import { BaseTransformerComponent } from './base-transformer-component';
import { CompareDataHolderService, Utils } from '../service/index';
import {ImageComparerComponent} from './image-comparer/image-comparer.component'
import { ImgManComponent } from './img-man/img-man.component';
import { Transformation } from '../model/case/transformation.model';

@Component({
  selector: 'app-image-transformer',
  templateUrl: 'app/shared/image-transformer/image-transformer.component.html'
})
export class ImageTransformerComponent extends BaseTransformerComponent implements AfterViewInit {
  
  @Input()
  annotateReqFun: (left: boolean, img: string, isCallingFirstTime: boolean) => void;

  @Input()
  eyesAnnotatedFun: (left: boolean, obj: any) => void;

  @Input()
  inSearchTab = true;

  @Input()
  public compareDataHolderService: CompareDataHolderService;

  @ViewChild('imgMan1') imgMan1: ImgManComponent;
  @ViewChild('imgMan2') imgMan2: ImgManComponent;

  @ViewChild('imageComparer') imageComparer: ImageComparerComponent;

  score = '';
  note = '';

  displayNote = true;

  constructor(
    private utilsService: Utils,
    private cd: ChangeDetectorRef
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

    this.cd.detectChanges();
  }

  changeSynchronization() {
    this.synchronized = !this.synchronized;
    this.imgMan1.changeSynchronization();
    this.imgMan2.changeSynchronization();
  }

  changeMonochrome() {
    this.monochrome = !this.monochrome;
    this.imgMan1.changeMonochrome();
    this.imgMan2.changeMonochrome();
  }

  changeHelpLinesVisibility() {
    this.imageComparer.changeHelpLinesVisibility();
    this.imgMan1.changeHelpLinesVisibility();
    this.imgMan2.changeHelpLinesVisibility();
  }

  clearAndReset() {
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
  }

  resetEyeDistance(left: boolean) {
    return left ? this.imgMan1.setEyeDistance('') : this.imgMan2.setEyeDistance('');
  }

  resetImage(left: boolean) {
    if (left === null) {
      this.imgMan1.resetCanvas();
      this.imgMan2.resetCanvas();
    } else {
      left ? this.imgMan1.resetCanvas() : this.imgMan2.resetCanvas();
    }
  }

  loadImage(left: boolean, src: string) {
    left ? this.imgMan1.loadImage(src) : this.imgMan2.loadImage(src);
  }

  loadImageAnnotated(left: boolean, annotation: { left: { x: number; y: number; set: number; }; right: { x: number; y: number; set: number; }; }) {
    left ? this.imgMan1.annotateImg(annotation) : this.imgMan2.annotateImg(annotation);
  }

  loadImageTransformed(left: boolean, src: string, transformation: Transformation) {
    left ? this.imgMan1.loadScaledImage(src, transformation) : this.imgMan2.loadScaledImage(src, transformation);
  }

  annotateCanvas(left: boolean, width: number, height: number, alpha: number, centerx: number, centery: number,
      imgType: number, normalize: boolean) {
    left ? this.imgMan1.annotateCanvas(width, height, alpha, centerx, centery, imgType, normalize) :
        this.imgMan2.annotateCanvas(width, height, alpha, centerx, centery, imgType, normalize);
  }

  getOriginalImage(left: boolean) {
    return left ? this.imgMan1.getOriginalImage() : this.imgMan2.getOriginalImage();
  }

  getModifiedImage(left: boolean) {
    return left ? this.imgMan1.getTransformedImage() : this.imgMan2.getTransformedImage();
  }

  setScore(score: number) {
    this.score = this.utilsService.floorFigure(score * 100.0, 2) + '%';
  }

  getAnnotatedEyes(left: boolean) {
    return left ? this.imgMan1.getAnnotatedEyes() : this.imgMan2.getAnnotatedEyes();
  }

  getNote() {
    return this.note;
  }

  getHeight() {
    const div = document.getElementById('div');
    return div.offsetWidth * 4 / 3;
  }

  getCompareHeight() {
    const div = document.getElementById('compare-div');
    return div.offsetWidth * 4 / 3;
  }

  getCompareWidth() {
    return document.getElementById('compare-div').offsetWidth - 20;
  }

  getWidth() {
    return document.getElementById('div').offsetWidth;
  }

  setTableData(left: boolean, data: any) {
    left ? this.imgMan1.setTableData(data) : this.imgMan2.setTableData(data);
  }

  changeInfoTableVisibility(left: boolean, visible: boolean) {
    return left ? this.imgMan1.setTableVisibility(visible) : this.imgMan2.setTableVisibility(visible);
  }

  setNote(note: string) {
    this.note = note;
  }

  getImageTransformation(left: boolean) {
    return left ? this.imgMan1.getTransformation(true) : this.imgMan2.getTransformation(false);
  }

  computeAndUpdateEyeDistance(left: boolean, obj: any, applyScale = false): number {
    const scale = left ? this.imgMan1.getScale() : this.imgMan2.getScale();
    let numDistance = this.computeEyeDistance(obj);

    if (applyScale && scale) {
      numDistance /= scale;
    }
    const distance = numDistance.toFixed(0);

    left ? this.imgMan1.setEyeDistance(distance) : this.imgMan2.setEyeDistance(distance);

    return Number(distance);
  }

  updateEyeDistance(left: boolean, distance: number) {
    left ? this.imgMan1.setEyeDistance(distance.toString()) : this.imgMan2.setEyeDistance(distance.toString());
  }

  imageDropped() {
    if (!this.displayNote) {
      this.displayNote = true;
    }
  }

  private computeEyeDistance(res: { left: { x: number; y: number; }; right: { x: number; y: number; }; }) {
    const a = res.left.x - res.right.x;
    const b = res.left.y - res.right.y;

    return Math.sqrt(a * a + b * b);
  }

  setAllLandmarks(left: boolean, faceLocation: any) {
    left ? this.imgMan1.setAllLandmarksFromFaceLocation(faceLocation) : 
          this.imgMan2.setAllLandmarksFromFaceLocation(faceLocation);
  }
}
