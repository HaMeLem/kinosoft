import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'geldbetrag'
})
export class GeldbetragPipe implements PipeTransform {

  transform(centBetrag: number): string {
    let centBetragString: string = Math.trunc(centBetrag).toString()
    centBetragString = centBetragString.padStart(3, "0");
    return centBetragString.substring(0, centBetragString.length - 2) + "," + centBetragString.substring(centBetragString.length - 2)
  }
}
