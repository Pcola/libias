import { AfterViewInit, Component, Input } from "@angular/core";
import { BaseTransformerComponent } from "../base-transformer-component";
import { Sliders } from "./sliders";

@Component({
  selector: 'app-image-comparer',
  templateUrl: 'app/shared/image-transformer/image-comparer/image-comparer.component.html',
})
export class ImageComparerComponent extends BaseTransformerComponent implements AfterViewInit {
  
  @Input()
  height = 0;

  @Input()
  width = 0;

  @Input()
  x = 0;

  @Input()
  y = 0;

  leftCanvas: HTMLCanvasElement;
  rightCanvas: HTMLCanvasElement;
  overlayCanvas: HTMLCanvasElement;
  slideCanvas: HTMLCanvasElement;
  tempCanvas: HTMLCanvasElement;
  imageDrawerCanvas: HTMLCanvasElement;

  contextOverlay: CanvasRenderingContext2D;
  contextSlide: CanvasRenderingContext2D;
  contextLeft: CanvasRenderingContext2D;
  contextRight: CanvasRenderingContext2D;
  contextTmp: CanvasRenderingContext2D;
  contextT: CanvasRenderingContext2D;

  r = 10;

  bw = 0;

  rect: ClientRect | DOMRect = null;
  sliders: Sliders = null;

  constructor() {
    super();
  }

  ngAfterViewInit(): void {
    this.leftCanvas = <HTMLCanvasElement>document.getElementById('canvasLeft');
    this.contextLeft = this.leftCanvas.getContext('2d');

    this.rightCanvas = <HTMLCanvasElement>document.getElementById('canvasRight');
    this.contextRight = this.rightCanvas.getContext('2d');
    this.contextRight.fillStyle = '#ffffff';

    this.overlayCanvas = <HTMLCanvasElement>document.getElementById('canvasOverlay');
    this.contextOverlay = this.overlayCanvas.getContext('2d');

    this.slideCanvas = <HTMLCanvasElement>document.getElementById('canvasSlide');
    this.contextSlide = this.slideCanvas.getContext('2d');

    this.tempCanvas = <HTMLCanvasElement>document.getElementById('canvasTmp');
    this.contextTmp = this.tempCanvas.getContext('2d');

    this.imageDrawerCanvas = <HTMLCanvasElement>document.getElementById('canvasT');
    this.contextT = this.imageDrawerCanvas.getContext('2d');

    this.rect = this.slideCanvas.getBoundingClientRect();

    this.sliders = new Sliders(this.r);
    this.sliders.add(this.slideCanvas.width / 2, this.r);
    this.sliders.add(this.slideCanvas.width / 2, this.slideCanvas.height - this.r);
    this.sliders.setCnv(this.slideCanvas);

    this.betweenEyes = this.width / 4;
    this.fromLeft = (this.width - this.betweenEyes) / 2;
    this.fromTop = this.height / 2.5;

    this.bw = 1;

    this.drawSliderBorder();
    this.changeHelpLinesVisibility(false);
  }

  loadImage = (left: boolean, img: ImageData): void => {
    this.imageDrawerCanvas.width = img.width;
    this.imageDrawerCanvas.height = img.height;

    if (left) {
      this.contextT.putImageData(img, 0, 0);
      this.contextLeft.clearRect(0, 0, this.leftCanvas.width, this.leftCanvas.height);
      this.contextLeft.drawImage(this.imageDrawerCanvas, 0, 0, this.leftCanvas.width, this.leftCanvas.height);
      this.update();
    } else {
      this.contextT.putImageData(img, 0, 0);
      this.contextTmp.clearRect(0, 0, this.tempCanvas.width, this.tempCanvas.height);
      this.contextTmp.drawImage(this.imageDrawerCanvas, 0, 0, this.tempCanvas.width, this.tempCanvas.height);
      this.update();
    }
  };

  mouseMove(evt: { clientX: number; clientY: number; }) {
    const x = (evt.clientX - this.rect.left - this.bw);
    const y = (evt.clientY - this.rect.top - this.bw);
    this.sliders.mouseMove(x, y);
    this.update();
  }

  mouseDown(evt: { clientX: number; clientY: number; }) {
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.sliders.mouseDown(x, y);
  }

  mouseUp(evt: { clientX: number; clientY: number; }) {
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.sliders.mouseUp(x, y);
  }

  mouseOut(evt: { clientX: number; clientY: number; }) {
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.sliders.mouseUp(x, y);
  }

  update() {
    let i;
    const poly = this.sliders.getPoly();

    this.rect = this.slideCanvas.getBoundingClientRect();
    this.contextRight.clearRect(0, 0, this.rightCanvas.width, this.rightCanvas.height);
    this.contextRight.save();
    this.contextRight.beginPath();
    this.contextRight.moveTo(poly[0].x, poly[0].y);
    for (i = 1; i < poly.length; i++) {
      this.contextRight.lineTo(poly[i].x, poly[i].y);
    }
    this.contextRight.lineTo(poly[0].x, poly[0].y);
    this.contextRight.closePath();
    this.contextRight.clip();
    this.contextRight.fillRect(0, 0, this.rightCanvas.width, this.rightCanvas.height);
    this.contextRight.drawImage(this.tempCanvas, 0, 0);
    this.contextRight.restore();

    this.contextSlide.clearRect(0, 0, this.slideCanvas.width, this.slideCanvas.height);
    if (this.helpLines) {
      this.constructLine(this.contextSlide, this.sliders.slider[0].x, this.sliders.slider[0].y, this.sliders.slider[1].x, this.sliders.slider[1].y, [10, 0], '#ff0000');
      this.constructLine(this.contextSlide, this.fromLeft, 0, this.fromLeft, this.height, [5, 10], '#00ff00');
      this.constructLine(this.contextSlide, this.fromLeft + this.betweenEyes, 0, this.fromLeft + this.betweenEyes, this.height, [5, 10], '#00ff00');
      this.constructLine(this.contextSlide, 0, this.fromTop, this.width, this.fromTop, [5, 10], '#00ff00');
    }
    for (i = 0; i < this.sliders.slider.length; i++) {
      this.contextSlide.beginPath();
      this.contextSlide.strokeStyle = '#0000ff';
      this.contextSlide.setLineDash([10, 0]);
      this.contextSlide.arc(this.sliders.slider[i].x, this.sliders.slider[i].y, this.r, 0, 2 * Math.PI, false);
      if (this.sliders.slider[i].selected !== 0) {
        this.contextSlide.fillStyle = '#ffffff';
      } else {
        this.contextSlide.fillStyle = '#00bfff';
      }
      this.contextSlide.closePath();
      this.contextSlide.fill();
      this.contextSlide.stroke();
    }

    this.drawSliderBorder();
  }

  changeHelpLinesVisibility(change = true) {
    if (change) {
      this.helpLines = !this.helpLines;
    }

    this.update();
  }

  changeMonochrome() {
  }

  changeSynchronization() {
  }

  private drawSliderBorder() {
    this.contextOverlay.clearRect(0, 0, this.width, this.height);

    this.contextOverlay.beginPath();
    this.contextOverlay.strokeStyle = '#00bfff';
    this.contextOverlay.lineWidth = 2 * this.r;
    this.contextOverlay.rect(this.r, this.r, this.overlayCanvas.width - (2 * this.r), this.overlayCanvas.height - (2 * this.r));
    this.contextOverlay.closePath();
    this.contextOverlay.stroke();

    this.contextOverlay.beginPath();
    this.contextOverlay.strokeStyle = '#0000ff';
    this.contextOverlay.lineWidth = 1;
    this.contextOverlay.rect(this.r, this.r, this.overlayCanvas.width - (2 * this.r), this.overlayCanvas.height - (2 * this.r));
    this.contextOverlay.closePath();
    this.contextOverlay.stroke();
  }
}