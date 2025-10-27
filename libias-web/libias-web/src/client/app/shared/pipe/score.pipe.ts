import { Pipe, PipeTransform } from '@angular/core';
import { Utils } from '../service/utils.service';

@Pipe({ name: 'score' })
export class ScorePipe implements PipeTransform {

  constructor(private utils: Utils) { }

  transform(value: number): string {
    if (value === undefined || value === null) {
      value = 0.00;
    }
    return this.utils.floorFigure(value * 100.0, 2);
  }

}
