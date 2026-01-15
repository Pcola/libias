import { Injectable } from '@angular/core';
import { PersonResponse } from '../model/person/index';
import { TableData } from '../model/case/table-data.model';

@Injectable()
export class CompareDataHolderService {

  leftImagePickResult: HTMLImageElement = undefined;
  rightImagePickResult: HTMLImageElement = undefined;

  leftPersonInfo: PersonResponse = undefined;
  rightPersonInfo: PersonResponse = undefined;
  
  leftTableData: TableData[] = [];
  rightTableData: TableData[] = [];

  constructor() {
  }

  getLeftImg(): HTMLImageElement | string {
    return this.leftImagePickResult ? this.leftImagePickResult : '';
  }

  getRightImg(): HTMLImageElement | string {
    return this.rightImagePickResult ? this.rightImagePickResult : '';
  }

  hold(image: HTMLImageElement, isLeftImg: boolean): void {
    if (isLeftImg) {
      this.leftImagePickResult = image;
    } else {
      this.rightImagePickResult = image;
    }
  }

  holdPersonInfo(person: PersonResponse, isLeftImg: boolean): void {
    if (isLeftImg) {
      this.leftPersonInfo = person;
    } else {
      this.rightPersonInfo = person;
    }
  }

  getPersonInfo(isLeftImg: boolean): PersonResponse {
    return isLeftImg ? this.leftPersonInfo : this.rightPersonInfo;
  }

  holdTableData(data: TableData[], isLeftImg: boolean): void {
    if (isLeftImg) {
      this.leftTableData = data;
    } else {
      this.rightTableData = data;
    }
  }

  getTableData(isLeftImg: boolean): TableData[] {
    return isLeftImg ? this.leftTableData : this.rightTableData;
  }

  clear(): void {
    this.leftImagePickResult = undefined;
    this.rightImagePickResult = undefined;
    this.leftPersonInfo = undefined;
    this.rightPersonInfo = undefined;
    this.leftTableData = [];
    this.rightTableData = [];
  }
}