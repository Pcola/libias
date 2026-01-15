export interface GesAnalyseResponse {
  embedding: number[];
  landmarks: number[];
  bbox: number[];
  quality: number;
  roll: number;
}