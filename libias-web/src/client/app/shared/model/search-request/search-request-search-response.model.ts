import { SearchRequestListItem } from "./search-request-list-item.model";

export class SearchRequestSearchResponse {
  searchRequests: SearchRequestListItem[];
  totalCount: number;
}