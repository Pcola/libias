'use strict';

interface Position {
  x: number;
  y: number;
  w: number;
  h: number;
}

interface Point {
  x: number;
  y: number;
}

interface SliderConfig {
  selected: number;
}

class Slider {
  public r: number = 10;
  public x: number;
  public y: number;
  public selected: number = 0;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  isIn(x: number, y: number): number {
    if (Math.sqrt(Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2)) < this.r) {
      return 1;
    }
    return 0;
  }
}

class Sliders {
  public slider: Slider[] = [];
  private r: number;
  private cnv: HTMLCanvasElement | null = null;
  private x: number = 0;
  private y: number = 0;
  private lastX: number = 0;
  private lastY: number = 0;
  private mouseDown: number = 0;
  private TL: Point | null = null;
  private BL: Point | null = null;
  private TR: Point | null = null;
  private BR: Point | null = null;

  constructor(r: number) {
    this.r = r;
  }

  add(x: number, y: number): void {
    this.slider.push(new Slider(x, y));
  }

  setCnv(cnv: HTMLCanvasElement): void {
    this.cnv = cnv;
  }

  mouseDown(x: number, y: number): void {
    this.mouseDown = 1;
    this.lastX = x;
    this.lastY = y;
    this.check();
  }

  mouseUp(x: number, y: number): void {
    this.mouseDown = 0;
    this.lastX = x;
    this.lastY = y;
    this.check();
  }

  mouseMove(x: number, y: number): void {
    this.lastX = this.x;
    this.lastY = this.y;
    this.x = x;
    this.y = y;
    this.check();
  }

  setPos(i: number, x: number, y: number): void {
    this.slider[i].x = x;
    this.slider[i].y = y;
  }

  private selected(): number {
    for (let i = 0; i < this.slider.length; i++) {
      if (this.slider[i].selected !== 0) return 1;
    }
    return 0;
  }

  private check(): void {
    let sel: number;
    for (let i = 0; i < this.slider.length; i++) {
      sel = this.selected();
      if (sel === 0) {
        if (this.slider[i].isIn(this.x, this.y) !== 0) {
          this.slider[i].selected = 1;
        }
      }
      if (this.mouseDown === 0) {
        this.slider[i].selected = 0;
      }
      if (this.slider[i].selected !== 0 && this.mouseDown !== 0 && this.cnv) {
        if (this.slider[i].x === this.r || this.slider[i].x === this.cnv.width - this.r) {
          this.slider[i].y += ((this.slider[i].y + this.y - this.lastY) >= this.r) && 
                               ((this.slider[i].y + this.y - this.lastY) <= this.cnv.height - this.r) ? 
                               this.y - this.lastY : 0;
        }
        if (this.slider[i].y === this.r || this.slider[i].y === this.cnv.height - this.r) {
          this.slider[i].x += ((this.slider[i].x + this.x - this.lastX) >= this.r) && 
                               ((this.slider[i].x + this.x - this.lastX) <= this.cnv.width - this.r) ? 
                               this.x - this.lastX : 0;
        }
      }
    }
  }

  private where(i: number): number {
    if (!this.cnv) return 0;
    if (this.slider[i].y === this.r) return 4;  // top
    if (this.slider[i].y === this.cnv.height - this.r) return 2;  // bottom
    if (this.slider[i].x === this.r) return 3;  // left
    if (this.slider[i].x === this.cnv.width - this.r) return 1;  // right
    return 0;
  }

  getPoly(): Point[] {
    if (!this.cnv) return [];
    
    const poly: Point[] = [];
    this.TL = { x: this.r, y: this.r };
    this.BL = { x: this.r, y: this.cnv.height - this.r };
    this.TR = { x: this.cnv.width - this.r, y: this.r };
    this.BR = { x: this.cnv.width - this.r, y: this.cnv.height - this.r };

    if (this.where(0) === 2 && this.where(1) === 4) {
      poly.push(this.slider[0], this.slider[1], this.TR, this.BR);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 2) {
      poly.push(this.slider[1], this.slider[0], this.TR, this.BR);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 1) {
      poly.push(this.slider[0], this.slider[1], this.BR, this.BL);
      return poly;
    }
    if (this.where(0) === 1 && this.where(1) === 3) {
      poly.push(this.slider[1], this.slider[0], this.BR, this.BL);
      return poly;
    }

    if (this.where(0) === 1 && this.where(1) === 4) {
      poly.push(this.slider[0], this.slider[1], this.TR);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 1) {
      poly.push(this.slider[1], this.slider[0], this.TR);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 4) {
      poly.push(this.slider[0], this.slider[1], this.TR, this.BR, this.BL);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 3) {
      poly.push(this.slider[1], this.slider[0], this.TR, this.BR, this.BL);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 2) {
      poly.push(this.slider[0], this.slider[1], this.BR, this.TR, this.TL);
      return poly;
    }
    if (this.where(0) === 2 && this.where(1) === 3) {
      poly.push(this.slider[1], this.slider[0], this.BR, this.TR, this.TL);
      return poly;
    }

    if (this.where(0) === 1 && this.where(1) === 2) {
      poly.push(this.slider[0], this.slider[1], this.BR);
      return poly;
    }
    if (this.where(0) === 2 && this.where(1) === 1) {
      poly.push(this.slider[1], this.slider[0], this.BR);
      return poly;
    }
    poly.push(this.TR);
    return poly;
  }
}

class ImgCmp {
  public version: string = '0.0.1';
  public debug: number = 0;
  
  private elem: HTMLElement;
  private x: number = 0;
  private y: number = 0;
  private w: number = 0;
  private h: number = 0;
  private bw: number = 0;
  private rect: DOMRect | null = null;
  private mDown: number = 0;
  private offset: number = 40;
  private eyeLines: number = 0;
  private r: number = 10;
  private betweenEyes: number = 0;
  private fromLeft: number = 0;
  private fromTop: number = 0;

  // Canvas elements
  private cnv1: HTMLCanvasElement;
  private cnv2: HTMLCanvasElement;
  private cnvTop: HTMLCanvasElement;
  private cnvSld: HTMLCanvasElement;
  private cnvTemp: HTMLCanvasElement;
  private cnvT: HTMLCanvasElement;

  // Contexts
  private ctx1: CanvasRenderingContext2D;
  private ctx2: CanvasRenderingContext2D;
  private ctxTop: CanvasRenderingContext2D;
  private ctxSld: CanvasRenderingContext2D;
  private ctxTemp: CanvasRenderingContext2D;
  private ctxT: CanvasRenderingContext2D;

  private sliders: Sliders;

  constructor(elem: HTMLElement) {
    this.elem = elem;
    
    // Create canvas elements
    this.cnv1 = document.createElement('canvas');
    this.cnv2 = document.createElement('canvas');
    this.cnvTop = document.createElement('canvas');
    this.cnvSld = document.createElement('canvas');
    this.cnvTemp = document.createElement('canvas');
    this.cnvT = document.createElement('canvas');

    // Get contexts
    this.ctx1 = this.cnv1.getContext('2d')!;
    this.ctx2 = this.cnv2.getContext('2d')!;
    this.ctxTop = this.cnvTop.getContext('2d')!;
    this.ctxSld = this.cnvSld.getContext('2d')!;
    this.ctxTemp = this.cnvTemp.getContext('2d')!;
    this.ctxT = this.cnvT.getContext('2d')!;

    // Set class names
    this.cnv1.className = 'jsman';
    this.cnv2.className = 'jsman';
    this.cnvTop.className = 'jsman';
    this.cnvSld.className = 'jsman';

    // Append to element
    this.elem.appendChild(this.cnv1);
    this.elem.appendChild(this.cnv2);
    this.elem.appendChild(this.cnvTop);
    this.elem.appendChild(this.cnvSld);

    // Set z-index
    this.cnv1.style.zIndex = '0';
    this.cnv2.style.zIndex = '1';
    this.cnvTop.style.zIndex = '2';
    this.cnvSld.style.zIndex = '3';

    this.cnvSld.title = 'Zum Verschieben des Wischers Punkte am Rand bewegen';

    // Set borders
    this.cnv1.style.border = '1px solid #006AA3';
    this.cnv2.style.border = '1px solid #006AA3';
    this.cnvSld.style.border = '1px solid #006AA3';
    this.cnvTop.style.border = '1px solid #006AA3';

    // Set border radius
    this.cnv1.style.borderRadius = '10px';
    this.cnv2.style.borderRadius = '10px';
    this.cnvSld.style.borderRadius = '10px';
    this.cnvTop.style.borderRadius = '10px';

    this.ctx2.fillStyle = '#ffffff';

    this.sliders = new Sliders(this.r);
    this.sliders.add(0, 0);
    this.sliders.add(0, 0);
    this.sliders.setCnv(this.cnvSld);

    // Register mouse callbacks
    this.cnvSld.onmousemove = this.mouseMove.bind(this);
    this.cnvSld.onmousedown = this.mouseDown.bind(this);
    this.cnvSld.onmouseup = this.mouseUp.bind(this);
    this.cnvSld.onmouseout = this.mouseOut.bind(this);
    this.cnvSld.onmouseover = this.mouseOver.bind(this);
  }

  private log(text: string): void {
    if (this.debug !== 0) {
      console.log('CMP: ' + text);
    }
  }

  init(obj: Position): void {
    this.x = obj.x;
    this.y = obj.y;
    this.w = obj.w;
    this.h = obj.h;
    this.bw = parseInt(this.cnvSld.style.borderWidth || '0', 10);

    this.cnv1.style.left = (this.x + this.bw).toString() + 'px';
    this.cnv2.style.left = (this.x + this.bw).toString() + 'px';
    this.cnvTop.style.left = (this.x + this.bw).toString() + 'px';
    this.cnvSld.style.left = this.x.toString() + 'px';

    this.cnv1.style.top = (this.y + this.bw).toString() + 'px';
    this.cnv2.style.top = (this.y + this.bw).toString() + 'px';
    this.cnvTop.style.top = (this.y + this.bw).toString() + 'px';
    this.cnvSld.style.top = this.y.toString() + 'px';

    this.cnv1.width = this.w;
    this.cnv2.width = this.w;
    this.cnvTop.width = this.w;
    this.cnvSld.width = this.w;
    this.cnvTemp.width = this.w;

    this.cnv1.height = this.h;
    this.cnv2.height = this.h;
    this.cnvTop.height = this.h;
    this.cnvSld.height = this.h;
    this.cnvTemp.height = this.h;

    this.rect = this.cnvSld.getBoundingClientRect();

    // Draw slider border
    this.ctxTop.beginPath();
    this.ctxTop.strokeStyle = '#00bfff';
    this.ctxTop.lineWidth = 2 * this.r;
    this.ctxTop.rect(this.r, this.r, this.cnvTop.width - (2 * this.r), this.cnvTop.height - (2 * this.r));
    this.ctxTop.closePath();
    this.ctxTop.stroke();

    // Draw slider path
    this.ctxTop.beginPath();
    this.ctxTop.strokeStyle = '#0000ff';
    this.ctxTop.lineWidth = 1;
    this.ctxTop.rect(this.r, this.r, this.cnvTop.width - (2 * this.r), this.cnvTop.height - (2 * this.r));
    this.ctxTop.closePath();
    this.ctxTop.stroke();

    this.ctx2.fillStyle = '#ffffff';
    this.betweenEyes = this.cnvSld.width / 4;
    this.fromLeft = (this.cnvSld.width - this.betweenEyes) / 2;
    this.fromTop = ((this.cnvSld.height) / 2.5);
    this.sliders.setPos(0, this.cnvSld.width / 2, this.r);
    this.sliders.setPos(1, this.cnvSld.width / 2, this.cnvSld.height - this.r);

    this.log('---------------------------');
    this.log('INIT');
    this.log('VER:    ' + this.version);
    this.log('CNV XY: ' + this.cnvSld.style.left + ', ' + this.cnvSld.style.top);
    this.log('CNV WH: ' + this.cnvSld.width + ', ' + this.cnvSld.height);
  }

  loadFace1(img: ImageData): void {
    this.cnvT.width = img.width;
    this.cnvT.height = img.height;
    this.ctxT.putImageData(img, 0, 0);
    this.ctx1.clearRect(0, 0, this.cnv1.width, this.cnv1.height);
    this.ctx1.drawImage(this.cnvT, 0, 0, this.cnv1.width, this.cnv1.height);
    this.update();
  }

  loadFace2(img: ImageData): void {
    this.cnvT.width = img.width;
    this.cnvT.height = img.height;
    this.ctxT.putImageData(img, 0, 0);
    this.ctxTemp.clearRect(0, 0, this.cnvTemp.width, this.cnvTemp.height);
    this.ctxTemp.drawImage(this.cnvT, 0, 0, this.cnvTemp.width, this.cnvTemp.height);
    this.update();
  }

  disableEyeLines(): void {
    this.eyeLines = 0;
    this.update();
  }

  enableEyeLines(): void {
    this.eyeLines = 1;
    this.update();
  }

  getOptimizedImage(): string {
    const mergedComparisonImage = document.createElement('canvas');
    mergedComparisonImage.width = this.cnv1.width;
    mergedComparisonImage.height = this.cnv1.height;

    const mergedCtx = mergedComparisonImage.getContext("2d")!;
    mergedCtx.drawImage(this.cnv1, 0, 0);
    mergedCtx.drawImage(this.cnv2, 0, 0);
    mergedCtx.drawImage(this.cnvSld, 0, 0);

    const ximg = mergedComparisonImage.toDataURL('image/png').split(',');
    return ximg[1];
  }

  private update(): void {
    const poly = this.sliders.getPoly();
    this.rect = this.cnvSld.getBoundingClientRect();
    
    this.ctx2.clearRect(0, 0, this.cnv2.width, this.cnv2.height);
    this.ctx2.save();
    this.ctx2.beginPath();
    
    if (poly.length > 0) {
      this.ctx2.moveTo(poly[0].x, poly[0].y);
      for (let i = 1; i < poly.length; i++) {
        this.ctx2.lineTo(poly[i].x, poly[i].y);
      }
      this.ctx2.lineTo(poly[0].x, poly[0].y);
    }
    
    this.ctx2.closePath();
    this.ctx2.clip();
    this.ctx2.fillRect(0, 0, this.cnv2.width, this.cnv2.height);
    this.ctx2.drawImage(this.cnvTemp, 0, 0);
    this.ctx2.restore();

    this.ctxSld.clearRect(0, 0, this.cnvSld.width, this.cnvSld.height);
    
    if (this.eyeLines !== 0) {
      // Line between sliders
      this.ctxSld.beginPath();
      this.ctxSld.strokeStyle = '#ff0000';
      this.ctxSld.lineWidth = 1;
      this.ctxSld.setLineDash([10, 0]);
      this.ctxSld.moveTo(this.sliders.slider[0].x, this.sliders.slider[0].y);
      this.ctxSld.lineTo(this.sliders.slider[1].x, this.sliders.slider[1].y);
      this.ctxSld.closePath();
      this.ctxSld.stroke();

      // Eye lines
      this.ctxSld.beginPath();
      this.ctxSld.lineWidth = 1;
      this.ctxSld.setLineDash([5, 10]);
      this.ctxSld.strokeStyle = '#00ff00';
      this.ctxSld.globalAlpha = 1.0;
      this.ctxSld.moveTo(this.fromLeft, 0);
      this.ctxSld.lineTo(this.fromLeft, this.h);
      this.ctxSld.moveTo(this.fromLeft + this.betweenEyes, 0);
      this.ctxSld.lineTo(this.fromLeft + this.betweenEyes, this.h);
      this.ctxSld.moveTo(0, this.fromTop + 0);
      this.ctxSld.lineTo(this.cnvSld.width, this.fromTop + 0);
      this.ctxSld.closePath();
      this.ctxSld.stroke();
    }
    
    // Redraw sliders
    for (let i = 0; i < this.sliders.slider.length; i++) {
      this.ctxSld.beginPath();
      this.ctxSld.strokeStyle = '#0000ff';
      this.ctxSld.setLineDash([10, 0]);
      this.ctxSld.arc(this.sliders.slider[i].x, this.sliders.slider[i].y, this.r, 0, 2 * Math.PI, false);
      
      if (this.sliders.slider[i].selected !== 0) {
        this.ctxSld.fillStyle = 'white';
      } else {
        this.ctxSld.fillStyle = '#00bfff';
      }
      
      this.ctxSld.closePath();
      this.ctxSld.fill();
      this.ctxSld.stroke();
    }
  }

  private mouseMove(evt: MouseEvent): void {
    if (!this.rect) return;
    const x = (evt.clientX - this.rect.left - this.bw);
    const y = (evt.clientY - this.rect.top - this.bw);
    this.sliders.mouseMove(x, y);
    this.update();
  }

  private mouseDown(evt: MouseEvent): void {
    if (!this.rect) return;
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.mDown = 1;
    this.sliders.mouseDown(x, y);
  }

  private mouseUp(evt: MouseEvent): void {
    if (!this.rect) return;
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.mDown = 0;
    this.sliders.mouseUp(x, y);
  }

  private mouseOut(evt: MouseEvent): void {
    if (!this.rect) return;
    const x = evt.clientX - this.rect.left - this.bw;
    const y = evt.clientY - this.rect.top - this.bw;
    this.mDown = 0;
    this.sliders.mouseUp(x, y);
  }

  private mouseOver(evt: MouseEvent): void {
    // Empty implementation
  }
}

// Export for module usage
export { ImgCmp, Sliders, Slider };

// Keep global function for backward compatibility
declare global {
  interface Window {
    ImgCmp: typeof ImgCmp;
    Sliders: typeof Sliders;
    Slider: typeof Slider;
  }
}

if (typeof window !== 'undefined') {
  window.ImgCmp = ImgCmp;
  window.Sliders = Sliders;
  window.Slider = Slider;
}