/**
 * DTO zum Übertragen der Filmvorführung aus dem Backend.
 * Status und Saal können ggf auch als Enum dargestellt werden.
 */
export interface FilmvorfuehrungDto {
  filmvorfuehrungId: number;
  filmtitel: string;
  geplanterBeginn: string;
  geplantesEnde: string;
  saal: string;
  status: string;
}
