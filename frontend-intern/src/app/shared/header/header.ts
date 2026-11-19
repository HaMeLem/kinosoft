import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  readonly menueAnzeigen = input(false);

  readonly menueOeffnen = output<void>();
  readonly abmelden = output<void>();
}
