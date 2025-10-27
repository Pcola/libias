import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, Output } from "@angular/core";
import { BaseTransformerComponent } from "../base-transformer-component";
import { Transformation } from "../../model/case/transformation.model";
import { IMAGE_PNG_PREFIX } from "../../constants";
import { TranslateService } from "ng2-translate";
import { CompareDataHolderService, Utils } from "../../service/index";

@Component({
  selector: 'app-img-man',
  templateUrl: 'app/shared/image-transformer/img-man/img-man.component.html',
})
export class ImgManComponent extends BaseTransformerComponent implements AfterViewInit {
  @Input()
  id = 0;

  @Input()
  inSearchTab: boolean;

  @Input()
  height = 0;

  @Input()
  width = 0;

  @Input()
  annotateReqFun: (left: boolean, img: string, isCallingFirstTime: boolean) => void;

  @Input()
  eyesAnnotatedFun: (left: boolean, obj: any) => void;

  @Input()
  public compareDataHolderService: CompareDataHolderService;

  @Input()
  neighbour: ImgManComponent;

  @Input()
  updateCallback: (left: boolean, img: any) => void;

  @Output()
  imageDropped = new EventEmitter<boolean>();

  reader = new FileReader();

  enableDrop = false;

  canvas: HTMLCanvasElement;
  helpCanvas: HTMLCanvasElement;
  tempCanvas: HTMLCanvasElement;
  inMemCanvas: HTMLCanvasElement;

  context: CanvasRenderingContext2D;
  helpContext: CanvasRenderingContext2D;
  tempContext: CanvasRenderingContext2D;
  inMemContext: CanvasRenderingContext2D;

  rect: ClientRect | DOMRect = null;

  betweenEyesPixels: string = undefined;

  image = new Image();

  eyeObj = {left: {x: 0, y: 0, set: 0}, right: {x: 0, y: 0, set: 0}};
  imgCoordinates = {x: 0, y: 0};
  sizeTemp = 0;
  t = {x: 0, y: 0};
  cOffset = {x: 0, y: 0};
  cOffsetTmp = {x: 0, y: 0};

  scale = 1;
  angle = 0;
  brightness = 0;
  contrast = 1;
  shift = {x: 0, y: 0};
  mouse = {down: 0, lastX: 0, lastY: 0, pressed: 0};

  rotationSliderValue = 0;
  brightnessSliderValue = 0;
  contrastSliderValue = 0;

  title = '';
  tableVisible = false;
  tableData: any;

  constructor(
    private translate: TranslateService,
    private utilsService: Utils,
    private cd: ChangeDetectorRef
  ) {
    super();
  }

  ngAfterViewInit(): void {
    this.tableVisible = this.inSearchTab;

    if (this.inSearchTab) {
      this.enableDrop = (this.id === 1) ? true : false;
      this.id === 1 ? this.title = this.translate.instant('label.SearchImageTitle') : this.title = this.translate.instant('label.ResultImageTitle');
    } else {
      this.enableDrop = true;
      this.title = 'Bild ' + this.id;
    }

    this.inMemCanvas = document.createElement('canvas');
    this.inMemContext = this.inMemCanvas.getContext('2d');

    this.canvas = <HTMLCanvasElement>document.getElementById('imgMan' + this.id);
    this.helpCanvas = <HTMLCanvasElement>document.getElementById('imgManOverlay' + this.id);
    this.tempCanvas = <HTMLCanvasElement>document.getElementById('imgManTemp' + this.id);
    this.rect = this.canvas.getBoundingClientRect();

    this.image.onload = () => {
      this.cOffsetTmp.x = ((this.tempCanvas.width - this.image.width) / 2);
      this.cOffsetTmp.y = ((this.tempCanvas.height - this.image.height) / 2);
      this.reDraw(0, 0, 0, 0);

      if (!this.inSearchTab) {
        this.imageDropped.emit(true);
      }
    };

    this.reader.onload = () => {
      this.image.src = this.reader.result as string;

      // TODO it is only hotfix
      setTimeout(() => {
        if (!this.inSearchTab) {
          if (this.image.src !== '') {
            this.resetCanvas();
            this.annotateReqOriginal();

            const image = new HTMLImageElement();
            image.src = this.image.src;
            const isLeftImg = this.id === 1;
            this.compareDataHolderService.hold(image, isLeftImg);
          }

          this.setTableData(this.utilsService.constructInfoTable(null));
        }
      }, 50);
    };

    this.helpCanvas.ondragover = function (e) {
      e.preventDefault();
    };

    this.context = this.canvas.getContext('2d');
    this.helpContext = this.helpCanvas.getContext('2d');
    this.tempContext = this.tempCanvas.getContext('2d');

    this.sizeTemp = Math.sqrt(Math.pow(this.width, 2) + Math.pow(this.height, 2));

    this.tempCanvas.height = this.sizeTemp;
    this.tempCanvas.width = this.sizeTemp;

    this.t.x = this.tempCanvas.width / 2;
    this.t.y = this.tempCanvas.height / 2;

    this.cOffset.x = (this.width - this.sizeTemp) / 2;
    this.cOffset.y = (this.height - this.sizeTemp) / 2;
    this.cOffsetTmp = {x: 0, y: 0};
    this.cOffsetTmp.x = ((this.tempCanvas.width - this.image.width) / 2);
    this.cOffsetTmp.y = ((this.tempCanvas.height - this.image.height) / 2);

    this.betweenEyes = this.width / 4;
    this.fromLeft = (this.width - this.betweenEyes) / 2;
    this.fromTop = this.height / 2.5;

    this.rotationSliderValue = this.angle;
    this.brightnessSliderValue = this.brightness * 100 / 255;
    this.contrastSliderValue = (this.contrast - 1) * 100;

    this.changeHelpLinesVisibility(false);

    this.cd.detectChanges();
  }

  loadImage(src: string) {
    this.image.src = src;
  }

  loadScaledImage(src: string, transformation: Transformation = null) {
    this.image.src = src;
    setTimeout(() => {
      if (transformation) {
        this.loadWithTransformation(transformation);
      } else {
        this.resetCanvas();
      }
    });
  }

  changeSynchronization() {
    this.synchronized = !this.synchronized;
  }

  changeMonochrome() {
    this.monochrome = !this.monochrome;
    this.reDraw(0, 0, 0, 0);
  }

  getTransformation(withEyes: boolean) {
    return new Transformation(this.scale, this.angle, this.shift.x, this.shift.y, this.brightness, this.contrast, withEyes, this.eyeObj);
  }

  getOriginalImage() {
    return this.image.src.split(',')[1];
  }

  getTransformedImage() {
    if (!this.getOriginalImage()) {
      return null;
    } else {
      return this.canvas.toDataURL(IMAGE_PNG_PREFIX).split(',')[1];
    }
  }

  getAnnotatedEyes() {
    const eyes: { left: any; right: any } = { left: null, right: null };
    if (this.eyeObj.left.set === 2 && this.eyeObj.right.set === 2) {
      eyes.left = this.imageToCanvasCoordinatesTranslate(this.eyeObj.left);
      eyes.right = this.imageToCanvasCoordinatesTranslate(this.eyeObj.right);
    }
    return eyes;
  }

  getScale() {
    return this.scale;
  }

  imageToCanvasCoordinatesTranslate(point: { x: number; y: number; set: number; }) {
    const centre = {x: (this.image.width / 2) - this.shift.x, y: (this.image.height / 2) + this.shift.y};
    const rotated = this.rotate(point, centre, this.angle * Math.PI / 180);
    const scaledAndShifted = {x: 0, y: 0};
    scaledAndShifted.x = (this.canvas.width / 2) - (((this.image.width / 2) - rotated.x - this.shift.x) * this.scale);
    scaledAndShifted.y = (this.canvas.height / 2) - (((this.image.height / 2) - rotated.y + this.shift.y) * this.scale);
    return scaledAndShifted;
  }

  setEyeDistance(distance: string) {
    this.betweenEyesPixels = distance;
  }

  setTableVisibility(visible: boolean) {
    this.tableVisible = visible;
  }

  setTableData(data: any) {
    this.tableVisible = true;

    this.tableData = data;
    this.cd.detectChanges();
  }

  onImageDrop(evt: { preventDefault: () => void; dataTransfer: { files: any[]; }; }) {
    evt.preventDefault();
    // evt.stopPropagation();

    if (this.enableDrop) {
      const file = evt.dataTransfer.files[0];
      if (file && file.type && file.type.indexOf('image/') === 0) {
        this.reader.readAsDataURL(file);
      } 
    } 
  }

  resetCanvas() {
    const hRatio = this.canvas.width / this.image.width;
    const vRatio = this.canvas.height / this.image.height;
    const ratio = Math.min(Math.min(hRatio, vRatio), 1);

    this.shift.x = 0;
    this.shift.y = 0;
    this.scale = ratio;
    this.angle = 0;
    this.brightness = 0;
    this.contrast = 1;
    this.eyeObj.right.set = 0;
    this.eyeObj.left.set = 0;
    this.reDraw(0, 0, 0, 0);
  }

  reDrawAnnotated() {
    if (!this.eyeObj.right.set || !this.eyeObj.left.set) {
      return;
    }
    const dX = this.eyeObj.right.x - this.eyeObj.left.x;
    const dY = this.eyeObj.right.y - this.eyeObj.left.y;
    const bEyes = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    let alpha = Math.atan2(dY, dX);
    if (this.betweenEyes / bEyes > 100) {
      return;
    }
    this.scale = this.betweenEyes / bEyes;
    this.angle = -alpha * 180 / Math.PI;

    const centre = {x: this.image.width / 2, y: this.image.height / 2};
    const pr = this.rotate(this.eyeObj.left, centre, -alpha);
    const sx = ((this.image.width / 2) - pr.x) - (((this.canvas.width / 2) - this.fromLeft) / this.scale);
    const sy = ((this.image.height / 2) - pr.y) - (((this.canvas.height / 2) - this.fromTop) / this.scale);
    const sh = Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2));
    alpha = Math.atan2(sy, sx);
    this.shift.x = (sh * Math.cos((this.angle * Math.PI / 180) - alpha));
    this.shift.y = (sh * Math.sin((this.angle * Math.PI / 180) - alpha));

    this.reDraw(0, 0, 0, 0);
  }

  changeHelpLinesVisibility(change = true) {
    if (change) {
      this.helpLines = !this.helpLines;
    }

    if (this.helpLines) {
      this.helpContext.clearRect(0, 0, this.width, this.height);

      const centre = {x: (this.image.width / 2) - this.shift.x, y: (this.image.height / 2) + this.shift.y};
      if (this.eyeObj.left.set) {
        const prL = this.rotate(this.eyeObj.left, centre, this.angle * Math.PI / 180);
        const eXL = (this.canvas.width / 2) - (((this.image.width / 2) - prL.x - this.shift.x) * this.scale);
        const eYL = (this.canvas.height / 2) - (((this.image.height / 2) - prL.y + this.shift.y) * this.scale);
        this.constructCross(eXL, eYL);
      }
      if (this.eyeObj.right.set) {
        const prR = this.rotate(this.eyeObj.right, centre, this.angle * Math.PI / 180);
        const eXR = (this.canvas.width / 2) - (((this.image.width / 2) - prR.x - this.shift.x) * this.scale);
        const eYR = (this.canvas.height / 2) - (((this.image.height / 2) - prR.y + this.shift.y) * this.scale);
        this.constructCross(eXR, eYR);
      }

      const alpha = Math.atan2(this.shift.y, this.shift.x);
      const a = (this.angle * Math.PI / 180) - alpha;
      const sh = Math.sqrt(Math.pow(this.shift.x, 2) + Math.pow(this.shift.y, 2)) * this.scale;
      const xs = (this.helpCanvas.width / 2) + (sh * Math.cos(a));
      const ys = (this.helpCanvas.height / 2) + (sh * Math.sin(a));

      this.helpContext.beginPath();
      this.helpContext.strokeStyle = '#ff0000';
      this.helpContext.moveTo((this.canvas.width / 2), (this.canvas.height / 2));
      this.helpContext.lineTo(xs, ys);
      this.helpContext.stroke();

      this.constructLine(this.helpContext, this.fromLeft, 0, this.fromLeft, this.height, [5, 10], '#00ff00');
      this.constructLine(this.helpContext, this.fromLeft + this.betweenEyes, 0, this.fromLeft + this.betweenEyes, this.height, [5, 10], '#00ff00');
      this.constructLine(this.helpContext, 0, this.fromTop, this.width, this.fromTop, [5, 10], '#00ff00');

      this.constructLine(this.helpContext, 0, 0, this.width, this.height, [100, 0], '#0000ff');
      this.constructLine(this.helpContext, this.width, 0, 0, this.height, [100, 0], '#0000ff');
    } else {
      this.helpContext.clearRect(0, 0, this.width, this.height);
    }
  }

  annotateImg(annotation: { left: { x: number; y: number; set: number; }; right: { x: number; y: number; set: number; }; }) {
    this.eyeObj.left.x = annotation.left.x;
    this.eyeObj.left.y = annotation.left.y;
    this.eyeObj.right.x = annotation.right.x;
    this.eyeObj.right.y = annotation.right.y;

    this.eyeObj.left.set = annotation.left.set;
    this.eyeObj.right.set = annotation.right.set;
    this.reDraw(0, 0, 0, 0);
  }

  annotateCanvas(annotation: any, imgType: number) {
    if (imgType === 0) {
      annotation.left = this.imageToCanvasCoordinatesTranslate(annotation.left);
      annotation.right = this.imageToCanvasCoordinatesTranslate(annotation.right);
    }

    const centre = {x: (this.canvas.width / 2), y: (this.canvas.height / 2)};
    const re = this.rotate(annotation.right, centre, -this.angle * Math.PI / 180);
    const le = this.rotate(annotation.left, centre, -this.angle * Math.PI / 180);

    this.eyeObj.left.x = ((this.image.width / 2)) - ((((this.canvas.width / 2) - le.x) / this.scale) + this.shift.x);
    this.eyeObj.left.y = ((this.image.height / 2)) - ((((this.canvas.height / 2) - le.y) / this.scale) - this.shift.y);
    this.eyeObj.right.x = ((this.image.width / 2)) - ((((this.canvas.width / 2) - re.x) / this.scale) + this.shift.x);
    this.eyeObj.right.y = ((this.image.height / 2)) - ((((this.canvas.height / 2) - re.y) / this.scale) - this.shift.y);

    this.eyeObj.left.set = annotation.left.set;
    this.eyeObj.right.set = annotation.right.set;
    this.reDraw(0, 0, 0, 0);
  }

  annotateReq() {
    this.annotateReqFun(this.id === 1, this.getTransformedImage(), null);
  }

  annotateReqOriginal() {
    this.annotateReqFun(this.id === 1, this.getOriginalImage(), true);
  }

  setLeftEye() {
    if (this.eyeObj.left.set) {
      if (this.eyeObj.right.x === this.imgCoordinates.x && this.eyeObj.right.y === this.imgCoordinates.y) {
        return;
      }
    }
    this.eyeObj.left.x = this.imgCoordinates.x;
    this.eyeObj.left.y = this.imgCoordinates.y;
    this.eyeObj.left.set = 2;
    this.reDraw(0, 0, 0, 0);

    if (this.eyeObj.right.set === 2) {
      this.eyesAnnotatedFun(this.id === 1, this.eyeObj);
    }
  }

  setRightEye() {
    if (this.eyeObj.right.set) {
      if (this.eyeObj.left.x === this.imgCoordinates.x && this.eyeObj.left.y === this.imgCoordinates.y) {
        return;
      }
    }
    this.eyeObj.right.x = this.imgCoordinates.x;
    this.eyeObj.right.y = this.imgCoordinates.y;
    this.eyeObj.right.set = 2;
    this.reDraw(0, 0, 0, 0);

    if (this.eyeObj.left.set === 2) {
      this.eyesAnnotatedFun(this.id === 1, this.eyeObj);
    }
  }

  mouseMove(evt: { clientX: number; clientY: number; }) {
    if (this.mouse.down !== 0) {
      const x = evt.clientX - this.rect.left;
      const y = evt.clientY - this.rect.top;
      const dx = x - this.mouse.lastX;
      const dy = y - this.mouse.lastY;
      this.mouse.lastX = x;
      this.mouse.lastY = y;
      this.update(0, dx, dy, 0);
    }
  }

  mouseWheel(evt: { preventDefault: () => void; wheelDelta: any; detail: number; }) {
    evt.preventDefault();
    const delta = evt.wheelDelta ? evt.wheelDelta : (-10 * evt.detail);
    if (this.mouse.pressed === 17) {
      this.update(0, 0, 0, +delta / 10);
      return;
    }
    if (this.mouse.pressed === 90) {
      this.update(0, 0, +delta / 10, 0);
      return;
    }
    if (this.mouse.pressed === 88) {
      this.update(0, +delta / 10, 0, 0);
      return;
    }
    this.update(+delta, 0, 0, 0);
  }

  mouseDown(evt: MouseEvent) {
    this.mouse.down = 1;
    this.mouse.lastX = evt.clientX - this.rect.left;
    this.mouse.lastY = evt.clientY - this.rect.top;
  }

  disableMouse() {
    this.mouse.down = 0;
  }

  rotationChanged() {
    const dr = this.rotationSliderValue - this.angle;
    this.update(0, 0, 0, dr);
  }

  brightnessChanged() {
    this.brightness = +this.brightnessSliderValue * 255 / 100;
    this.update(0, 0, 0, 0);
  }

  contrastChanged() {
    this.contrast = (+this.contrastSliderValue / 100) + 1;
    this.update(0, 0, 0, 0);
  }

  reDraw(dS: number, dX: number, dY: number, dR: number) {
    this.scale *= Math.pow(1.0005, dS);
    dX = dX / this.scale;
    dY = dY / this.scale;

    const sh = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    const alpha = Math.atan2(dY, dX);
    this.shift.x += (sh * Math.cos((this.angle * Math.PI / 180) - alpha));
    this.shift.y += (sh * Math.sin((this.angle * Math.PI / 180) - alpha));

    if (this.angle + dR >= -180 && this.angle + dR <= 180) {
      this.angle += dR;
    }

    this.tempContext.clearRect(0, 0, this.tempCanvas.width, this.tempCanvas.height);
    this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.tempContext.save();
    this.tempContext.translate(this.t.x, this.t.y);
    this.tempContext.rotate(this.angle * Math.PI / 180);
    this.tempContext.scale(this.scale, this.scale);
    this.tempContext.drawImage(this.image, this.cOffsetTmp.x + this.shift.x - this.t.x, this.cOffsetTmp.y - this.shift.y - this.t.y);
    this.tempContext.restore();
    this.context.drawImage(this.tempCanvas, this.cOffset.x, this.cOffset.y);

    const imgData = this.tempContext.getImageData(0, 0, this.tempCanvas.width, this.tempCanvas.height);
    const res = this.imgFilter(imgData, this.brightness, this.contrast, this.monochrome);
    this.context.putImageData(res, this.cOffset.x, this.cOffset.y);

    this.updateCallback(this.id === 1, this.context.getImageData(0, 0, this.canvas.width, this.canvas.height));

    if (this.helpLines) {
      this.changeHelpLinesVisibility(false);
    }
    this.imgCoordinates.x = (this.image.width / 2) - this.shift.x;
    this.imgCoordinates.y = (this.image.height / 2) + this.shift.y;
    this.rotationSliderValue = this.angle;
    this.brightnessSliderValue = this.brightness * 100 / 255;
    this.contrastSliderValue = (this.contrast - 1) * 100;
  }

  private loadWithTransformation(transformation: Transformation) {
    this.scale = transformation.scale;
    this.angle = transformation.angle;
    this.shift.x = transformation.shiftX;
    this.shift.y = transformation.shiftY;
    this.brightness = transformation.rotation;
    this.contrast = transformation.contrast;
    this.eyeObj.right.set = 0;
    this.eyeObj.left.set = 0;

    if (transformation.lset && transformation.lx && transformation.ly) {
      this.eyeObj.left.set = transformation.lset;
      this.eyeObj.left.x = transformation.lx;
      this.eyeObj.left.y = transformation.ly;
    } else {
      this.eyeObj.left.set = 0;
      this.eyeObj.left.x = 0;
      this.eyeObj.left.y = 0;
    }

    if (transformation.rset && transformation.rx && transformation.ry) {
      this.eyeObj.right.set = transformation.rset;
      this.eyeObj.right.x = transformation.rx;
      this.eyeObj.right.y = transformation.ry;
    } else {
      this.eyeObj.right.set = 0;
      this.eyeObj.right.x = 0;
      this.eyeObj.right.y = 0;
    }


    this.reDraw(0, 0, 0, 0);
  }

  private update(dS: number, dX: number, dY: number, dR: number) {
    if (this.synchronized) {
      this.neighbour.reDraw(dS, dX, dY, dR);
    }
    this.reDraw(dS, dX, dY, dR);
  }

  private imgFilter(imgData: ImageData, br: number, ct: number, gs: boolean) {
    const data = imgData.data;
    const len = data.length;
    let i;
    for (i = 0; i < len; i += 4) {
      data[i] += br;
      data[i + 1] += br;
      data[i + 2] += br;

      data[i] *= ct;
      data[i + 1] *= ct;
      data[i + 2] *= ct;
    }
    if (gs) {
      for (i = 0; i < len; i += 4) {
        const res = 0.2126 * data[i] + 0.7152 * data[i + 1] + 0.0722 * data[i + 2];
        data[i] = res;
        data[i + 1] = res;
        data[i + 2] = res;
      }
    }
    return (imgData);
  }

  private rotate(p: { x: any; y: any; set?: number; }, c: { x: any; y: any; }, a: number) {
    return {
      x: (((p.x - c.x) * Math.cos(a)) - ((p.y - c.y) * Math.sin(a)) + c.x) | 0,
      y: (((p.x - c.x) * Math.sin(a)) + ((p.y - c.y) * Math.cos(a)) + c.y) | 0
    };
  }

  private constructCross(x: number, y: number) {
    this.helpContext.strokeStyle = '#ff0000';
    this.helpContext.setLineDash([100, 0]);
    this.helpContext.lineWidth = 4;
    this.helpContext.beginPath();
    this.helpContext.moveTo(x - 20, y - 20);
    this.helpContext.lineTo(x + 20, y + 20);
    this.helpContext.moveTo(x - 20, y + 20);
    this.helpContext.lineTo(x + 20, y - 20);
    this.helpContext.stroke();
    this.helpContext.closePath();
    this.helpContext.lineWidth = 1;
  }

  onDragOver(evt: DragEvent) {
  evt.preventDefault();
  evt.stopPropagation();
}
}
