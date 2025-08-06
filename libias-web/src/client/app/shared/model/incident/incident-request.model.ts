export class IncidentRequest {
  caseId: number;
  priorityId: number;
  pkz: number;
  firstName: string;
  lastName: string;
  createdDate: string;
  azrNumber: string;
  dNumber: string;
  nationality: string;
  statusId: number;
  fileNumber: string; // aktenzeichen
  showDoubleEvents: boolean;
  first: number;
  rows: number;
  sort: string;
  order: number;
  referenceType: string;
  workplaceId: string;
}
