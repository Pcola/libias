export class Slider {
  r = 10;
  x = 0;
  y = 0;
  selected = 0;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  isIn(newX: number, newY: number) {
    return Math.sqrt(Math.pow((this.x - newX), 2) + Math.pow((this.y - newY), 2)) < this.r;
  }
}