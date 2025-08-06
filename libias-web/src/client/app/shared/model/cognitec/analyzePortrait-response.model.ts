export interface AnalyzePortraitResponse {
  val: AnalyzePortraitResult;
}

interface AnalyzePortraitResult {
  foundFace: boolean;
  portraitCharacteristics: PortraitCharacteristics;
}

interface PortraitCharacteristics {
  rightEye: Position;
  leftEye: Position;
  faceCenter: Position;
}

interface Position {
  x: number;
  y: number;
}
