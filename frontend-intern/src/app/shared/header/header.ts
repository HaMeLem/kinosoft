import { Component, inject, input, output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private readonly router = inject(Router);

  readonly menueAnzeigen = input(false);
  readonly menueOffen = input(false);

  readonly menueOeffnen = output<void>();
  readonly abmelden = output<void>();

  routeIstAktiv(pfad: string): boolean {
    return this.router.url === pfad || this.router.url.startsWith(`${pfad}/`);
  }
}
