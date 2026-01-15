import { SearchRequestCandidateResponse } from './search-request-candidate.response';

export class SearchRequestResponse {
  requestId: number;
  dateCreated: Date;
  createdBy: string;
  dateModified: Date;
  modifiedBy: string;
  maxCandidates: number;
  minScore: number;
  transformation: string;
  bilddaten: any;
  geschlecht: string;
  staatsangehoerigkeit: string;
  bemerkung: string;
  candidates: SearchRequestCandidateResponse[];

  constructor() {
    this.candidates = [];
  }
}