import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-scanner',
  imports: [],
  templateUrl: './scanner.html',
  styleUrl: './scanner.css',
})
export class Scanner {
  readonly abmeldungOffen = signal(false);

  abmeldungAnzeigen(): void {
    this.abmeldungOffen.set(true);
  }

  abmeldungSchliessen(): void {
    this.abmeldungOffen.set(false);
  }
}
