export interface Vorstellung {
  uuid: string,
  beginn: string,
  saal: string,
  film: string,
}

export interface Angebot {
  gesamtpreis: Geldbetrag,
  plaetze: Platz[],
  saalplan: Saalplan,
}

export interface Saalplan {
  plaetze: Platz[][];
}

export interface Platz {
  reihe: number,
  platz: number,
  istFrei: boolean,
}

export interface PlatzId {
  reihe: number,
  platz: number,
}

export interface ZusammenhaengendePlaetze {
  plaetze: PlatzId[],
}

export interface Geldbetrag {
  betrag: number,
  waehrung: Waehrung,
}

export enum Waehrung {
  EUR = 'EUR',
}

export interface Preisanfrage {
  vorstellungId: string,
  plaetze: ZusammenhaengendePlaetze,
}

export interface Kartenbestellung {
  vorstellungId: string,
  plaetze: ZusammenhaengendePlaetze,
}

export interface Verkaufsvorgang {
  auftragsnummer: string,
  vorstellungId: string,
  plaetze: ZusammenhaengendePlaetze,
  gesamtpreis: Geldbetrag,
}

export interface Zahlungsvorgang {
  id: string,
  anlauf: number,
  betrag: Geldbetrag,
  status: string,
}

export interface Zahlungsstatus {
  status: String
}

export interface Kinokarte {
  film: string,
  beginn: string,
  saal: string,
  reihe: number,
  platz: number,
}


