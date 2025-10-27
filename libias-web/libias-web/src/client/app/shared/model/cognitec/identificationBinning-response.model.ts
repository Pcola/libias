import { ImageType } from './imageType.enum';

export interface IdentificationBinningResponse {
  val: IdentificationResult;
  imgType: ImageType;
}

interface IdentificationResult {
  transactionId: string;
  processedImage: ImageProcessingInfo;
  matches: MatchSet;
}

interface ImageProcessingInfo {
  id: string;
  foundFace: boolean;
  faceLocation: FaceLocation;
}

interface FaceLocation {
  rightEye: PositionObj;
  leftEye: PositionObj;
}

interface PositionObj {
  value: Position;
}

interface Position {
  x: number;
  y: number;
}

interface MatchSet {
  m: Match[];
}

export interface Match {
  rank: number;
  caseID: number;
  score: number;
}
