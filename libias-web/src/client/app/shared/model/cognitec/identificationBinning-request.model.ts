import { ImageType } from './imageType.enum';

export class IdentificationBinningRequest {
  img: string;
  requestId: number;
  imgType : ImageType;
  maxMatches: number;
  minScore: number;
}
