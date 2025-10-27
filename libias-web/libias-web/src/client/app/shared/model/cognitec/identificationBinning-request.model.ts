import { ImageType } from './imageType.enum';

export class IdentificationBinningRequest {
  img: string;
  imgType : ImageType;
  maxMatches: number;
  minScore: number;
}
