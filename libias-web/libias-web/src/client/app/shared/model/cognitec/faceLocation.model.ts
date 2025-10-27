import { Position } from './position.model';

export class FaceLocation {
  producer: Producer;
  boundingBox: BoundingBox;
  confidence: number;
  rightEye?: Position;
  leftEye?: Position;
  noseTip?: Position;
  rightMouthCorner?: Position;
  leftMouthCorner?: Position;
}

export class Producer {
  id: string;
  domain: string;
}

export class BoundingBox {
  center: Position;
  width: number;
  height: number;
  alpha: number;
}