import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  benutzername = '';
  passwort = '';
  fehlermeldung = '';

  anmelden(): void {
    this.fehlermeldung = '';

    if (!this.benutzername.trim() || !this.passwort.trim()) {
      this.fehlermeldung = 'Bitte Benutzername und Passwort eingeben.';
      return;
    }

    // Die Anmeldung mit Testbenutzern ergänzen wir später.
    this.fehlermeldung = 'Die Demo-Anmeldung ist noch nicht eingerichtet.';
  }
}

