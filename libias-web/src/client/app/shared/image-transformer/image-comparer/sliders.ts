import { Slider } from './slider';

export class Sliders {
  r: number;
  cnv: HTMLCanvasElement = null;
  x: number = 0;
  y: number = 0;
  lastX: number = 0;
  lastY: number = 0;
  mouseDownVar: boolean = false;
  TL: { x: number, y: number } = null;
  BL: { x: number, y: number } = null;
  TR: { x: number, y: number } = null;
  BR: { x: number, y: number } = null;

  slider: Slider[] = [];

  constructor(r: number) {
    this.r = r;
  }

  add(x: number, y: number): void {
    this.slider.push(new Slider(x, y));
  }

  setCnv(_cnv: HTMLCanvasElement): void {
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
    for (let i = 0; i < this.slider.length; i++) {
      if (this.slider[i].selected !== 0) {
        return 1;
      }
    }
    return 0;
  }

  check(): void {
    for (let i = 0; i < this.slider.length; i++) {
      const sel = this.selected();
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
          const newY = this.slider[i].y + this.y - this.lastY;
          if (newY >= this.r && newY <= this.cnv.height - this.r) {
            this.slider[i].y = newY;
          }
        }
        if (this.slider[i].y === this.r || this.slider[i].y === this.cnv.height - this.r) {
          const newX = this.slider[i].x + this.x - this.lastX;
          if (newX >= this.r && newX <= this.cnv.width - this.r) {
            this.slider[i].x = newX;
          }
        }
      }
    }
  }

  where(i: number): number {
    if (this.slider[i].y === this.r) {
      return 4;
    }
    if (this.slider[i].y === this.cnv.height - this.r) {
      return 2;
    }
    if (this.slider[i].x === this.r) {
      return 3;
    }
    if (this.slider[i].x === this.cnv.width - this.r) {
      return 1;
    }
    return 0;
  }

  getPoly(): Array<{ x: number, y: number }> {
    const poly: Array<{ x: number, y: number }> = [];

    this.TL = { x: this.r, y: this.r };
    this.BL = { x: this.r, y: this.cnv.height - this.r };
    this.TR = { x: this.cnv.width - this.r, y: this.r };
    this.BR = { x: this.cnv.width - this.r, y: this.cnv.height - this.r };

    const w0 = this.where(0);
    const w1 = this.where(1);

    if (w0 === 2 && w1 === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      poly.push(this.BR);
      return poly;
    }
    if (w0 === 4 && w1 === 2) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      poly.push(this.BR);
      return poly;
    }

    if (w0 === 3 && w1 === 1) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }
    if (w0 === 1 && w1 === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }

    if (w0 === 1 && w1 === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      return poly;
    }
    if (w0 === 4 && w1 === 1) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      return poly;
    }

    if (w0 === 3 && w1 === 4) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.TR);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }
    if (w0 === 4 && w1 === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.TR);
      poly.push(this.BR);
      poly.push(this.BL);
      return poly;
    }

    if (w0 === 3 && w1 === 2) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      poly.push(this.TR);
      poly.push(this.TL);
      return poly;
    }
    if (w0 === 2 && w1 === 3) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      poly.push(this.TR);
      poly.push(this.TL);
      return poly;
    }

    if (w0 === 1 && w1 === 2) {
      poly.push(this.slider[0]);
      poly.push(this.slider[1]);
      poly.push(this.BR);
      return poly;
    }
    if (w0 === 2 && w1 === 1) {
      poly.push(this.slider[1]);
      poly.push(this.slider[0]);
      poly.push(this.BR);
      return poly;
    }

    poly.push(this.TR);
    return poly;
  }
}