import { Component } from '@angular/core';

@Component({
  selector: 'app-saaluebersicht',
  imports: [],
  templateUrl: './saaluebersicht.html',
  styleUrl: './saaluebersicht.css',
})
export class Saaluebersicht {
  // Vorläufige Saaldaten für die Darstellung der Saalübersicht.
// Später werden die Säle aus den Anwendungsdaten geladen.
  readonly saele = [
    { id: 1, name: 'Saal 1' },
    { id: 2, name: 'Saal 2' },
  ];

}
