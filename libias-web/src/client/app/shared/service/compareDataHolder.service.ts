import { Injectable } from '@angular/core';

@Injectable()
export class CompareDataHolderService {

  leftImagePickResult: HTMLImageElement = undefined;
  rightImagePickResult: HTMLImageElement = undefined;

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

  clear(): void {
    this.leftImagePickResult = undefined;
    this.rightImagePickResult = undefined;
  }
}