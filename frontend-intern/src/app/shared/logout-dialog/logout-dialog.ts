import { Component, output } from '@angular/core';

@Component({
  selector: 'app-logout-dialog',
  imports: [],
  templateUrl: './logout-dialog.html',
  styleUrl: './logout-dialog.css',
})
export class LogoutDialog {
  readonly schliessen = output<void>();
}
