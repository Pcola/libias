import { Slider } from './slider';

export class Sliders {
  r: any;
  cnv: any = null;
  x: number = 0;
  y: number = 0;
  lastX: number = 0;
  lastY: number = 0;
  mouseDownVar: boolean = false;
  TL: any = null;
  BL: any = null;
  TR: any = null;
  BR: any = null;

  slider: Slider[] = [];

  constructor(r: any) {
    this.r = r;
  }

  add(x: number, y: number): void {
    this.slider.push(new Slider(x, y));
  }

  setCnv(_cnv: any): void {
    this.cnv = _cnv;
  }

  mouseDown(_x: number, _y: number): void {
    this.mouseDownVar = true;
    this.lastX = _x;
    this.lastY = _y;
    this.check();
  }

  mouseUp(_x: number, _y: number): void {
    this.mouseDownVar = false;
    this.lastX = _x;
    this.lastY = _y;
    this.check();
  }

  mouseMove(_x: number, _y: number): void {
    this.lastX = this.x;
    this.lastY = this.y;
    this.x = _x;
    this.y = _y;
    this.check();
  }

  setPos(i: number, _x: number, _y: number): void {
    this.slider[i].x = _x;
    this.slider[i].y = _y;
  }

  selected(): number {
    let i: number;
    for (i = 0; i < this.slider.length; i++) {
      if (this.slider[i].selected !== 0) {
        return 1;
      }
    }
    return 0;
  }

  check(): void {
    let sel: number;
    let i: number;
    for (i = 0; i < this.slider.length; i++) {
      sel = this.selected();
      if (sel === 0) {
        if (this.slider[i].isIn(this.x, this.y)) {
          this.slider[i].selected = 1;
        }
      }
      if (!this.mouseDownVar) {
        this.slider[i].selected = 0;
      }
      if (this.slider[i].selected !== 0 && this.mouseDownVar) {
        this.slider[i].x = Math.ceil(this.slider[i].x);
        this.slider[i].y = Math.ceil(this.slider[i].y);

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

  where(i: number): number {
    if (this.slider[i].y === this.r) {
      return 4;  // top
    }
    if (this.slider[i].y === this.cnv.height - this.r) {
      return 2;  // bottom
    }
    if (this.slider[i].x === this.r) {
      return 3;  // left
    }
    if (this.slider[i].x === this.cnv.width - this.r) {
      return 1;  // right
    }
    return 0;
  }

  getPoly(): any[] {
    const poly: any[] = [];
    this.TL = { x: this.r, y: this.r };
    this.BL = { x: this.r, y: this.cnv.height - this.r };
    this.TR = { x: this.cnv.width - this.r, y: this.r };
    this.BR = { x: this.cnv.width - this.r, y: this.cnv.height - this.r };

    if (this.where(0) === 2 && this.where(1) === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      poly.push(this.BR);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 2) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      poly.push(this.BR);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 1) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }
    if (this.where(0) === 1 && this.where(1) === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }

    if (this.where(0) === 1 && this.where(1) === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 1) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }
    if (this.where(0) === 4 && this.where(1) === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }

    if (this.where(0) === 3 && this.where(1) === 2) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      poly.push(this.TR);
      poly.push(this.TL);
      return poly;
    }
    if (this.where(0) === 2 && this.where(1) === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      poly.push(this.TR);
      poly.push(this.TL);
      return poly;
    }

    if (this.where(0) === 1 && this.where(1) === 2) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      return poly;
    }
    if (this.where(0) === 2 && this.where(1) === 1) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      return poly;
    }
    poly.push(this.TR);
    return poly;
  }
}