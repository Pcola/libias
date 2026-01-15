export class SearchRequestRequest {
  requestId: number;
  maxCandidates: number;
  minScore: number;
  transformation: string;
  bilddaten: any;
  geschlecht: string;
  staatsangehoerigkeit: string;
  bemerkung: string;
  minAge: number;

  constructor() {}
}