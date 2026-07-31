export interface Zahlung {
  referenz: string,
  betragInCent: number,
  status: Zahlungsstatus,
}

export type Zahlungsstatus = 'Offen' | 'Eingegangen' | 'Abgebrochen';
