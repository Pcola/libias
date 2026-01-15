export class SearchRequestCandidateResponse {
  bildOid: number;
  rank: number;
  score: number;

  constructor(bildOid?: number, rank?: number, score?: number) {
    this.bildOid = bildOid;
    this.rank = rank;
    this.score = score;
  }
}