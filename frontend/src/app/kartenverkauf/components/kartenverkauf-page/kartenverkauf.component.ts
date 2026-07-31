import {Component, computed, input, signal, viewChild} from '@angular/core';
import {VorstellungComponent} from '../vorstellung/vorstellung.component';
import {Kinokarte, Verkaufsvorgang, Vorstellung, ZusammenhaengendePlaetze} from '../../dtos/kartenverkauf';
import {PlatzanzahlComponent} from '../platzanzahl/platzanzahl.component';
import {SaalplanComponent} from '../saalplan/saalplan.component';
import {ZahlungComponent} from '../zahlung/zahlung.component';
import {KinokarteComponent} from '../kinokarte/kinokarte.component';
import {NavbarComponent} from '../../../common/components/navbar/navbar.component';
import {BadgeComponent} from './badge/badge.component';

@Component({
  selector: 'app-kartenverkauf',
  imports: [
    VorstellungComponent,
    PlatzanzahlComponent,
    SaalplanComponent,
    ZahlungComponent,
    KinokarteComponent,
    NavbarComponent,
    BadgeComponent,
  ],
  templateUrl: './kartenverkauf.component.html',
  styleUrl: './kartenverkauf.component.css',
})
export class KartenverkaufComponent {
  readonly vorstellungId = input<string>();

  readonly gewaehlteVorstellung = signal<Vorstellung | undefined>(undefined);
  readonly gewaehltePlatzanzahl = signal<number | undefined>(undefined);
  readonly gewaehltePlaetze = signal<ZusammenhaengendePlaetze | undefined>(undefined);
  readonly zahlungsbestaetigung = signal<Verkaufsvorgang | undefined>(undefined);
  readonly erhalteneKinokarten = signal<Kinokarte[] | undefined>(undefined);

  readonly platzanzahlComponent = viewChild.required<PlatzanzahlComponent>('platzanzahlComponent');
  readonly saalplanComponent = viewChild.required<SaalplanComponent>('saalplanComponent');

  readonly zeigeVorstellungAktiv = computed(() => this.vorstellungId() !== undefined);
  readonly zeigeVorstellungFertig = computed(() => this.gewaehlteVorstellung() !== undefined);
  readonly zeigePlatzanzahlAktiv = computed(
    () => this.gewaehlteVorstellung() !== undefined && this.gewaehltePlatzanzahl() === undefined,
  );
  readonly zeigePlatzanzahlFertig = computed(() => this.gewaehltePlatzanzahl() !== undefined);
  readonly zeigePlatzwahlAktiv = computed(
    () => this.gewaehltePlatzanzahl() !== undefined && this.gewaehltePlaetze() === undefined,
  );
  readonly zeigePlatzwahlFertig = computed(() => this.gewaehltePlaetze() !== undefined);
  readonly zeigeZahlungAktiv = computed(
    () => this.gewaehltePlaetze() !== undefined && this.zahlungsbestaetigung() === undefined,
  );
  readonly zeigeZahlungFertig = computed(() => this.zahlungsbestaetigung() !== undefined);
  readonly zeigeKinokartenAktiv = computed(
    () => this.zahlungsbestaetigung() !== undefined && this.erhalteneKinokarten() === undefined,
  );
  readonly zeigeKinokartenFertig = computed(() => this.erhalteneKinokarten() !== undefined);

  vorstellungGeladen(vorstellung: Vorstellung) {
    this.gewaehlteVorstellung.set(vorstellung);
  }

  platzanzahlGewaehlt(platzanzahl: number) {
    this.gewaehltePlatzanzahl.set(platzanzahl);
  }

  plaetzeGewaehlt(plaetze: ZusammenhaengendePlaetze) {
    this.gewaehltePlaetze.set(plaetze);
  }

  zahlungBestaetigt(zahlungsbestaetigung: Verkaufsvorgang) {
    this.zahlungsbestaetigung.set(zahlungsbestaetigung);
  }

  kinokartenGedruckt(kinokarten: Kinokarte[]) {
    this.erhalteneKinokarten.set(kinokarten);
  }
}
