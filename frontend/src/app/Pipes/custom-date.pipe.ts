import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'customDate' })
export class CustomDatePipe implements PipeTransform {
  transform(value: any): Date | null {
    if (!value) {
      return null;
    }

    const strValue = value.toString();

    const parts = strValue.split(',').map(Number);
    if (parts.length < 6 || parts.some(isNaN)) {
      return null;
    }

    return new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]);
  }
}
