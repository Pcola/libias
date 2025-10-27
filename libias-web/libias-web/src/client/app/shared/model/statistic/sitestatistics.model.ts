export class SiteStatistics {
  siteId: string;
  siteName: string;
  priorityId: number;
  statusId1: number;
  statusId2: number;
  statusId3: number;
  statusId4: number;
  statusId5: number;
  statusId6: number;
  statusId7: number;
  statusId8: number;
  statusId9: number;
  statusId10: number;

  constructor(
    siteId: string, siteName: string, priorityId: number, statusId1: number, statusId2: number, statusId3: number, statusId4: number,
    statusId5: number, statusId6: number, statusId7: number, statusId8: number, statusId9: number, statusId10: number) {
    this.siteId = siteId;
    this.siteName = siteName;
    this.priorityId = priorityId;
    this.statusId1 = statusId1;
    this.statusId2 = statusId2;
    this.statusId3 = statusId3;
    this.statusId4 = statusId4;
    this.statusId5 = statusId5;
    this.statusId6 = statusId6;
    this.statusId7 = statusId7;
    this.statusId8 = statusId8;
    this.statusId9 = statusId9;
    this.statusId10 = statusId10;
  }

}
