export interface EyeData {
  set: number;
  x: number;
  y: number;
}

export interface Eyes {
  left: EyeData;
  right: EyeData;
}

export class Transformation {
  scale: number;
  angle: number;
  shiftX: number;
  shiftY: number;
  rotation: number;
  contrast: number;
  lset: number | null = null;
  lx: number | null = null;
  ly: number | null = null;
  rset: number | null = null;
  rx: number | null = null;
  ry: number | null = null;

  constructor(
    scale: number,
    angle: number,
    shiftX: number,
    shiftY: number,
    rotation: number,
    contrast: number,
    withEyes: boolean,
    eyeObj?: Eyes
  ) {
    this.scale = scale;
    this.angle = angle;
    this.shiftX = shiftX;
    this.shiftY = shiftY;
    this.rotation = rotation;
    this.contrast = contrast;

    if (withEyes && eyeObj && eyeObj.left.set !== 0 && eyeObj.right.set !== 0) {
      this.lset = eyeObj.left.set;
      this.lx = eyeObj.left.x;
      this.ly = eyeObj.left.y;
      this.rset = eyeObj.right.set;
      this.rx = eyeObj.right.x;
      this.ry = eyeObj.right.y;
    }
  }
}
