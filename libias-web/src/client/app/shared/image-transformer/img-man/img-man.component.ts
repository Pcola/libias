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

  facialLandmarks = {
    leftEye: { x: 0, y: 0, set: 0 },
    rightEye: { x: 0, y: 0, set: 0 },
    noseTip: { x: 0, y: 0, set: 0 },
    leftMouthCorner: { x: 0, y: 0, set: 0 },
    rightMouthCorner: { x: 0, y: 0, set: 0 }
  };

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

  faceSize = {x: 0, y: 0};
  faceCenter = {x: 0, y: 0};
  faceAlpha = 0;

  boxTopLeft = {x: 0, y: 0};
  boxTopRight = {x: 0, y: 0};
  boxBottomLeft = {x: 0, y: 0};
  boxBottomRight = {x: 0, y: 0};

  mirrored = false;

  boundingBoxMode = 0;
  boundingBoxFirstCorner = { x: 0, y: 0 };
  boundingBoxClickPosition = { x: 0, y: 0 };

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

      setTimeout(() => {
        if (!this.inSearchTab) {
          if (this.image.src !== '') {
            this.resetCanvas();
            this.annotateReqOriginal();

            const image = new Image();
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
    const eyes = {
      left: this.facialLandmarks.leftEye,
      right: this.facialLandmarks.rightEye
    };
    return new Transformation(this.scale, this.angle, this.shift.x, this.shift.y, this.brightness, this.contrast, withEyes, eyes);
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
    if (this.facialLandmarks.leftEye.set === 2 && this.facialLandmarks.rightEye.set === 2) {
      eyes.left = this.imageToCanvasCoordinatesTranslate(this.facialLandmarks.leftEye);
      eyes.right = this.imageToCanvasCoordinatesTranslate(this.facialLandmarks.rightEye);
    }
    return eyes;
  }

  getScale() {
    return this.scale;
  }

  imageToCanvasCoordinatesTranslate(point: { x: number; y: number; set?: number; }) {
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
    
    this.facialLandmarks = {
      leftEye: { x: 0, y: 0, set: 0 },
      rightEye: { x: 0, y: 0, set: 0 },
      noseTip: { x: 0, y: 0, set: 0 },
      leftMouthCorner: { x: 0, y: 0, set: 0 },
      rightMouthCorner: { x: 0, y: 0, set: 0 }
    };
    
    this.reDraw(0, 0, 0, 0);
  }

  reDrawAnnotated() {
    if (!this.facialLandmarks.rightEye.set || !this.facialLandmarks.leftEye.set) {
      return;
    }
    const dX = this.facialLandmarks.rightEye.x - this.facialLandmarks.leftEye.x;
    const dY = this.facialLandmarks.rightEye.y - this.facialLandmarks.leftEye.y;
    const bEyes = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    let alpha = Math.atan2(dY, dX);
    if (this.betweenEyes / bEyes > 100) {
      return;
    }
    this.scale = this.betweenEyes / bEyes;
    this.angle = -alpha * 180 / Math.PI;

    const centre = {x: this.image.width / 2, y: this.image.height / 2};
    const pr = this.rotate(this.facialLandmarks.leftEye, centre, -alpha);
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

      if (this.faceSize.x > 0 && this.faceSize.y > 0) {
        const tl = this.rotate(this.boxTopLeft, centre, this.angle * Math.PI / 180);
        const tr = this.rotate(this.boxTopRight, centre, this.angle * Math.PI / 180);
        const bl = this.rotate(this.boxBottomLeft, centre, this.angle * Math.PI / 180);
        const br = this.rotate(this.boxBottomRight, centre, this.angle * Math.PI / 180);

        const tlCanvas = {
          x: (this.canvas.width / 2) - (((this.image.width / 2) - tl.x - this.shift.x) * this.scale),
          y: (this.canvas.height / 2) - (((this.image.height / 2) - tl.y + this.shift.y) * this.scale)
        };

        const trCanvas = {
          x: (this.canvas.width / 2) - (((this.image.width / 2) - tr.x - this.shift.x) * this.scale),
          y: (this.canvas.height / 2) - (((this.image.height / 2) - tr.y + this.shift.y) * this.scale)
        };

        const blCanvas = {
          x: (this.canvas.width / 2) - (((this.image.width / 2) - bl.x - this.shift.x) * this.scale),
          y: (this.canvas.height / 2) - (((this.image.height / 2) - bl.y + this.shift.y) * this.scale)
        };

        const brCanvas = {
          x: (this.canvas.width / 2) - (((this.image.width / 2) - br.x - this.shift.x) * this.scale),
          y: (this.canvas.height / 2) - (((this.image.height / 2) - br.y + this.shift.y) * this.scale)
        };

        this.helpContext.beginPath();
        this.helpContext.moveTo(tlCanvas.x, tlCanvas.y);
        this.helpContext.lineTo(trCanvas.x, trCanvas.y);
        this.helpContext.lineTo(brCanvas.x, brCanvas.y);
        this.helpContext.lineTo(blCanvas.x, blCanvas.y);
        this.helpContext.lineTo(tlCanvas.x, tlCanvas.y);
        this.helpContext.closePath();
        
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
        
        this.helpContext.fillStyle = 'rgba(0, 255, 255, 0.1)';
        this.helpContext.fill();
      }

      if (this.facialLandmarks.leftEye.set) {
        const prL = this.rotate(this.facialLandmarks.leftEye, centre, this.angle * Math.PI / 180);
        const eXL = (this.canvas.width / 2) - (((this.image.width / 2) - prL.x - this.shift.x) * this.scale);
        const eYL = (this.canvas.height / 2) - (((this.image.height / 2) - prL.y + this.shift.y) * this.scale);
        
        this.helpContext.beginPath();
        this.helpContext.arc(eXL, eYL, 6, 0, 2 * Math.PI, false);
        this.helpContext.fillStyle = '#FF0000';
        this.helpContext.fill();
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
      }
      
      if (this.facialLandmarks.rightEye.set) {
        const prR = this.rotate(this.facialLandmarks.rightEye, centre, this.angle * Math.PI / 180);
        const eXR = (this.canvas.width / 2) - (((this.image.width / 2) - prR.x - this.shift.x) * this.scale);
        const eYR = (this.canvas.height / 2) - (((this.image.height / 2) - prR.y + this.shift.y) * this.scale);
        
        this.helpContext.beginPath();
        this.helpContext.arc(eXR, eYR, 6, 0, 2 * Math.PI, false);
        this.helpContext.fillStyle = '#00FF00';
        this.helpContext.fill();
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
      }

      if (this.facialLandmarks.noseTip.set > 0) {
        const rotated = this.rotate(this.facialLandmarks.noseTip, centre, this.angle * Math.PI / 180);
        const x = (this.canvas.width / 2) - (((this.image.width / 2) - rotated.x - this.shift.x) * this.scale);
        const y = (this.canvas.height / 2) - (((this.image.height / 2) - rotated.y + this.shift.y) * this.scale);
        
        this.helpContext.beginPath();
        this.helpContext.arc(x, y, 6, 0, 2 * Math.PI, false);
        this.helpContext.fillStyle = '#0000FF';
        this.helpContext.fill();
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
      }

      if (this.facialLandmarks.leftMouthCorner.set > 0) {
        const rotated = this.rotate(this.facialLandmarks.leftMouthCorner, centre, this.angle * Math.PI / 180);
        const x = (this.canvas.width / 2) - (((this.image.width / 2) - rotated.x - this.shift.x) * this.scale);
        const y = (this.canvas.height / 2) - (((this.image.height / 2) - rotated.y + this.shift.y) * this.scale);
        
        this.helpContext.beginPath();
        this.helpContext.arc(x, y, 6, 0, 2 * Math.PI, false);
        this.helpContext.fillStyle = '#FFFF00';
        this.helpContext.fill();
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
      }

      if (this.facialLandmarks.rightMouthCorner.set > 0) {
        const rotated = this.rotate(this.facialLandmarks.rightMouthCorner, centre, this.angle * Math.PI / 180);
        const x = (this.canvas.width / 2) - (((this.image.width / 2) - rotated.x - this.shift.x) * this.scale);
        const y = (this.canvas.height / 2) - (((this.image.height / 2) - rotated.y + this.shift.y) * this.scale);
        
        this.helpContext.beginPath();
        this.helpContext.arc(x, y, 6, 0, 2 * Math.PI, false);
        this.helpContext.fillStyle = '#FF00FF';
        this.helpContext.fill();
        this.helpContext.strokeStyle = '#FFFFFF';
        this.helpContext.lineWidth = 2;
        this.helpContext.stroke();
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

  annotateImg(annotation: { left: any; right: any; }) {
    this.facialLandmarks.leftEye = annotation.left;
    this.facialLandmarks.rightEye = annotation.right;
    this.reDraw(0, 0, 0, 0);
  }

  annotateCanvas(width: number, height: number, alpha: number, centerx: number, centery: number, imgType: number, normalize: boolean) {
    if (imgType !== 0) {
      const mirrorFactor = this.mirrored ? -1 : 1;
      alpha = alpha - mirrorFactor * this.angle * Math.PI / 180;
      width = width / this.scale;
      height = height / this.scale;

      const translated = this.translateCoordinatesToOrig(centerx, centery);
      centerx = translated.x;
      centery = translated.y;
    }

    this.faceSize.x = width;
    this.faceSize.y = height;
    this.faceCenter.x = centerx;
    this.faceCenter.y = centery;
    this.faceAlpha = alpha;
    this.computeBoundingBox();

    if (normalize) {
      this.reDrawAnnotated();
    } else {
      this.reDraw(0, 0, 0, 0);
    }
  }

  private translateCoordinatesToOrig(x: number, y: number) {
    const center = {x: (this.image.width / 2) - this.shift.x, y: (this.image.height / 2) + this.shift.y};
    const mirrorFactor = this.mirrored ? -1 : 1;
    const translated = {x: 0, y: 0};
    translated.x = this.image.width / 2 - (this.canvas.width / 2 - x) / this.scale * mirrorFactor - this.shift.x;
    translated.y = this.image.height / 2 - (this.canvas.height / 2 - y) / this.scale + this.shift.y;
    return this.rotate(translated, center, -this.angle * Math.PI / 180);
  }

  private computeBoundingBox() {
    if (!this.faceSize.x || !this.faceSize.y) {
      return;
    }

    const dx = this.faceSize.x / 2;
    const dy = this.faceSize.y / 2;
    this.boxTopLeft = this.rotate({x: this.faceCenter.x - dx, y: this.faceCenter.y - dy}, this.faceCenter, this.faceAlpha);
    this.boxTopRight = this.rotate({x: this.faceCenter.x + dx, y: this.faceCenter.y - dy}, this.faceCenter, this.faceAlpha);
    this.boxBottomLeft = this.rotate({x: this.faceCenter.x - dx, y: this.faceCenter.y + dy}, this.faceCenter, this.faceAlpha);
    this.boxBottomRight = this.rotate({x: this.faceCenter.x + dx, y: this.faceCenter.y + dy}, this.faceCenter, this.faceAlpha);
  }

  annotateReq() {
    this.annotateReqFun(this.id === 1, this.getTransformedImage(), null);
  }

  annotateReqOriginal() {
    this.annotateReqFun(this.id === 1, this.getOriginalImage(), true);
  }

  setLeftEye() {
    if (this.facialLandmarks.leftEye.set) {
      if (this.facialLandmarks.rightEye.x === this.imgCoordinates.x && this.facialLandmarks.rightEye.y === this.imgCoordinates.y) {
        return;
      }
    }
    this.facialLandmarks.leftEye.x = this.imgCoordinates.x;
    this.facialLandmarks.leftEye.y = this.imgCoordinates.y;
    this.facialLandmarks.leftEye.set = 2;
    this.reDraw(0, 0, 0, 0);

    if (this.facialLandmarks.rightEye.set === 2) {
      this.eyesAnnotatedFun(this.id === 1, {
        left: this.facialLandmarks.leftEye,
        right: this.facialLandmarks.rightEye
      });
    }
  }

  setRightEye() {
    if (this.facialLandmarks.rightEye.set) {
      if (this.facialLandmarks.leftEye.x === this.imgCoordinates.x && this.facialLandmarks.leftEye.y === this.imgCoordinates.y) {
        return;
      }
    }
    this.facialLandmarks.rightEye.x = this.imgCoordinates.x;
    this.facialLandmarks.rightEye.y = this.imgCoordinates.y;
    this.facialLandmarks.rightEye.set = 2;
    this.reDraw(0, 0, 0, 0);

    if (this.facialLandmarks.leftEye.set === 2) {
      this.eyesAnnotatedFun(this.id === 1, {
        left: this.facialLandmarks.leftEye,
        right: this.facialLandmarks.rightEye
      });
    }
  }

  setNose() {
    this.facialLandmarks.noseTip.x = this.imgCoordinates.x;
    this.facialLandmarks.noseTip.y = this.imgCoordinates.y;
    this.facialLandmarks.noseTip.set = 2;
    this.reDraw(0, 0, 0, 0);
  }

  setLeftMouthCorner() {
    this.facialLandmarks.leftMouthCorner.x = this.imgCoordinates.x;
    this.facialLandmarks.leftMouthCorner.y = this.imgCoordinates.y;
    this.facialLandmarks.leftMouthCorner.set = 2;
    this.reDraw(0, 0, 0, 0);
  }

  setRightMouthCorner() {
    this.facialLandmarks.rightMouthCorner.x = this.imgCoordinates.x;
    this.facialLandmarks.rightMouthCorner.y = this.imgCoordinates.y;
    this.facialLandmarks.rightMouthCorner.set = 2;
    this.reDraw(0, 0, 0, 0);
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
  if (this.boundingBoxMode > 0) {
    this.reDraw(0, 0, 0, 0);
    
    this.boundingBoxClickPosition.x = evt.clientX - this.rect.left;
    this.boundingBoxClickPosition.y = evt.clientY - this.rect.top;
    
    console.log('Canvas click position:', this.boundingBoxClickPosition);
    
    this.handleBoundingBoxClick();
    return;
  }
  
  this.mouse.down = 1;
  this.mouse.lastX = evt.clientX - this.rect.left;
  this.mouse.lastY = evt.clientY - this.rect.top;
}

private handleBoundingBoxClick() {
  const canvasX = this.boundingBoxClickPosition.x;
  const canvasY = this.boundingBoxClickPosition.y;
  
  const canvasCenterX = this.canvas.width / 2;
  const canvasCenterY = this.canvas.height / 2;
  
  const offsetX = canvasX - canvasCenterX;
  const offsetY = canvasY - canvasCenterY;
  
  const clickX = this.imgCoordinates.x + (offsetX / this.scale);
  const clickY = this.imgCoordinates.y + (offsetY / this.scale);
  
  console.log('Canvas click:', {x: canvasX, y: canvasY});
  console.log('Image click coords:', {x: clickX, y: clickY});
  
  this.helpContext.fillStyle = 'rgba(255, 0, 0, 0.8)';
  this.helpContext.beginPath();
  this.helpContext.arc(canvasX, canvasY, 8, 0, 2 * Math.PI);
  this.helpContext.fill();
  
  if (this.boundingBoxMode === 1) {
    this.boundingBoxFirstCorner.x = clickX;
    this.boundingBoxFirstCorner.y = clickY;
    
    console.log('1. bod:', this.boundingBoxFirstCorner);
    
    this.boundingBoxMode = 2;
    this.helpCanvas.style.cursor = 'crosshair';
  } else if (this.boundingBoxMode === 2) {
    const secondCorner = { x: clickX, y: clickY };
    
    const minX = Math.min(this.boundingBoxFirstCorner.x, secondCorner.x);
    const maxX = Math.max(this.boundingBoxFirstCorner.x, secondCorner.x);
    const minY = Math.min(this.boundingBoxFirstCorner.y, secondCorner.y);
    const maxY = Math.max(this.boundingBoxFirstCorner.y, secondCorner.y);
    
    this.faceCenter.x = (minX + maxX) / 2;
    this.faceCenter.y = (minY + maxY) / 2;
    this.faceSize.x = maxX - minX;
    this.faceSize.y = maxY - minY;
    this.faceAlpha = 0;
    
    this.computeBoundingBox();
    
    this.boundingBoxMode = 0;
    this.helpCanvas.style.cursor = 'pointer';
    console.log('✅ Box hotovo!');
    
    this.reDraw(0, 0, 0, 0);
  }
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
    
    this.facialLandmarks = {
      leftEye: { x: 0, y: 0, set: 0 },
      rightEye: { x: 0, y: 0, set: 0 },
      noseTip: { x: 0, y: 0, set: 0 },
      leftMouthCorner: { x: 0, y: 0, set: 0 },
      rightMouthCorner: { x: 0, y: 0, set: 0 }
    };

    if (transformation.lset && transformation.lx && transformation.ly) {
      this.facialLandmarks.leftEye.set = transformation.lset;
      this.facialLandmarks.leftEye.x = transformation.lx;
      this.facialLandmarks.leftEye.y = transformation.ly;
    }

    if (transformation.rset && transformation.rx && transformation.ry) {
      this.facialLandmarks.rightEye.set = transformation.rset;
      this.facialLandmarks.rightEye.x = transformation.rx;
      this.facialLandmarks.rightEye.y = transformation.ry;
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

  onDragOver(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
  }

  setAllLandmarksFromFaceLocation(faceLocation: any) {
  console.log('Incoming faceLocation:', faceLocation);
  
  // HELPER FUNKCIA - EXTRAHOVAŤ POSITION Z JAXB ELEMENTU
  const extractPosition = (jaxbElement: any) => {
    if (!jaxbElement) return null;
    // Ak je to JAXBElement (má .value), vem .value.value
    if (jaxbElement.value) {
      return jaxbElement.value;
    }
    // Inak vem priamo
    return jaxbElement;
  };
  
  // SKONTROLUJ ČI SÚ LANDMARKS NULL
  const leftEyePos = extractPosition(faceLocation.leftEye);
  const rightEyePos = extractPosition(faceLocation.rightEye);
  
  const hasLandmarks = leftEyePos && rightEyePos && leftEyePos.x !== undefined && rightEyePos.x !== undefined;
  
  if (!hasLandmarks && faceLocation.boundingBox) {
    // Landmarks sú null - nastav len bounding box
    const bbox = faceLocation.boundingBox;
    
    this.faceCenter.x = bbox.center.x;
    this.faceCenter.y = bbox.center.y;
    this.faceSize.x = bbox.width;
    this.faceSize.y = bbox.height;
    this.faceAlpha = bbox.alpha || 0;
    
    console.log('✅ Box nastavený z backendu:', {
      center: this.faceCenter,
      size: this.faceSize,
      alpha: this.faceAlpha
    });
    
    // RESETUJ landmarks
    this.facialLandmarks = {
      leftEye: { x: 0, y: 0, set: 0 },
      rightEye: { x: 0, y: 0, set: 0 },
      noseTip: { x: 0, y: 0, set: 0 },
      leftMouthCorner: { x: 0, y: 0, set: 0 },
      rightMouthCorner: { x: 0, y: 0, set: 0 }
    };
    
    this.computeBoundingBox();
    this.helpLines = true;
    this.reDraw(0, 0, 0, 0);
    this.changeHelpLinesVisibility(false);
    return;
  }
  
  // AK MÁŠ LANDMARKS, NASTAV ICH
  if (leftEyePos && leftEyePos.x !== undefined) {
    this.facialLandmarks.leftEye = { 
      x: leftEyePos.x, 
      y: leftEyePos.y, 
      set: 1 
    };
    console.log('✅ LeftEye set:', this.facialLandmarks.leftEye);
  }
  
  if (rightEyePos && rightEyePos.x !== undefined) {
    this.facialLandmarks.rightEye = { 
      x: rightEyePos.x, 
      y: rightEyePos.y, 
      set: 1 
    };
    console.log('✅ RightEye set:', this.facialLandmarks.rightEye);
  }
  
  const noseTipPos = extractPosition(faceLocation.noseTip);
  if (noseTipPos && noseTipPos.x !== undefined) {
    this.facialLandmarks.noseTip = { 
      x: noseTipPos.x, 
      y: noseTipPos.y, 
      set: 1 
    };
    console.log('✅ NoseTip set:', this.facialLandmarks.noseTip);
  }
  
  const leftMouthPos = extractPosition(faceLocation.leftMouthCorner);
  if (leftMouthPos && leftMouthPos.x !== undefined) {
    this.facialLandmarks.leftMouthCorner = { 
      x: leftMouthPos.x, 
      y: leftMouthPos.y, 
      set: 1 
    };
    console.log('✅ LeftMouth set:', this.facialLandmarks.leftMouthCorner);
  }
  
  const rightMouthPos = extractPosition(faceLocation.rightMouthCorner);
  if (rightMouthPos && rightMouthPos.x !== undefined) {
    this.facialLandmarks.rightMouthCorner = { 
      x: rightMouthPos.x, 
      y: rightMouthPos.y, 
      set: 1 
    };
    console.log('✅ RightMouth set:', this.facialLandmarks.rightMouthCorner);
  }
  
  console.log('✅ All landmarks after set:', this.facialLandmarks);
  
  this.helpLines = true;
  this.reDraw(0, 0, 0, 0);
  this.changeHelpLinesVisibility(false);
}

  setBoundingBox() {
    if (this.boundingBoxMode === 0) {
      this.boundingBoxMode = 1;
      this.helpCanvas.style.cursor = 'crosshair';
      console.log('Bounding Box Mode: Klikni na 1. bod');
    } else {
      this.boundingBoxMode = 0;
      this.helpCanvas.style.cursor = 'pointer';
      console.log('Bounding Box Mode: Zrušené');
    }
  }
}