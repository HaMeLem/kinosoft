# Changelog

Alle erwähnenswerten Änderungen an KinoSoft werden hier festgehalten.

Das Format orientiert sich an [Keep a Changelog](https://keepachangelog.com/de/1.1.0/),
die Versionierung folgt [Semantic Versioning](https://semver.org/lang/de/).

## [0.2.0] – 2026-07-31

Zwei neue Bounded Contexts (**Zahlung**, **Wochenplanung**) mit event-getriebener Integration
über Spring Modulith sowie eine Neustrukturierung der Build- und Entwicklungsumgebung.

### Hinzugefügt

- **Spring Modulith** eingeführt: strukturiert die Anwendung in fachliche Module (Bounded Contexts)
  mit geprüften, zyklenfreien Abhängigkeiten
- Bounded Context **Zahlung**: eigenständiger, zustandsbehafteter (simulierter) Zahlungsdienstleister
- Bounded Context **Wochenplanung**: Source of Truth des Wochenplans für Filmauswahl und Kartenverkauf
- Integrationsmuster: Kartenverkauf ↔ Zahlung event-getrieben; Wochenplanung fächert per Benachrichtigungs-Event +
  Query-back an beide Consumer aus
- Aggregat `Verkaufsvorgang`: bündelt Vorstellung, Plätze, Gesamtpreis und Zahlungsvorgang und stellt dazu
  genau einmal Kinokarten aus, gemeldet als `KinokartenVerkauft`
- Architektur-Annotationen `@Gateway` und `@DomainCommand` in `common.architecture`

### Geändert

- Statische `data.sql` entfernt: Filmauswahl und Kartenverkauf werden zur Laufzeit aus der Wochenplanung gefüllt
- Demodaten folgen der aktuellen Woche statt eines fixen Referenzdatums
- Build entflochten: `backend/` eigenständiges Maven-Projekt, Frontend überall Node-nativ mit `npm` gebaut

## [0.1.2] – 2026-06-12

Ausbau der Backend-Testabdeckung und kleinere Korrekturen.

### Hinzugefügt

- Umfassende Testabdeckung für das Backend (`common`, `filmauswahl`, `kartenverkauf`)

### Geändert

- Application-Service-Tests auf `@SpringBootTest` umgestellt; Teststil vereinheitlicht
- Integrationstests (`*IntegrationTest`) per Failsafe in die `verify`-Phase separiert
- Regel „mindestens ein Platz" von `Platzanzahl` nach `Saalplan.sucheZusammenhaengendePlaetze` verschoben
- `throwIf` → `wenn` in den `common.error`-Klassen
- `@AllArgsConstructor` an den Filmauswahl-Entities für den Testdatenaufbau
- `spring-boot-starter-webmvc-test` als Test-Dependency ergänzt

### Behoben

- `AktuelleVorstellungen.holeVorstellung` wirft bei unbekannter `VorstellungId` nun `RessourceNichtGefunden` (HTTP 404)
  statt `NoSuchElementException` (HTTP 500)

## [0.1.1] – 2026-06-01

Versions-Updates und Modernisierung des Codes.

### Hinzugefügt

- `CHANGELOG.md` zur Dokumentation der Änderungen pro Release
- Domain-Exceptions mit `ProblemDetail`-basierter Fehlerbehandlung im Backend
- Glossar der Ubiquitous Language für beide Bounded Contexts in `docs/Glossar.md`

### Geändert

- Frontend auf **Angular 21.2.14** aktualisiert
- Komponenten auf **Signal-API** migriert
- Constructor-Injection durch `inject()`-Funktion ersetzt
- Umstellung auf **Zoneless** Change Detection
- Frontend-Tests: **Karma + Jasmine → Vitest 4.x**
- Frontend-Build: **custom-webpack → `@angular/build` (esbuild)**
- Frontend-SBOM: `@cyclonedx/webpack-plugin` → `@cyclonedx/cyclonedx-npm`
- Node 24.11.0 / npm 11.6.1 in `frontend/pom.xml`
- CI: `test:frontend` nutzt dasselbe Node-Image wie `build:frontend`
- Backend auf **Java 25** angehoben
- Spring Boot auf **4.0.6** aktualisiert
- CORS-Konfiguration zentral in `SecurityConfig` gebündelt
- JPA-Entities: ID-basiertes `equals`/`hashCode` statt Lombok `@Data`

## [0.1.0] – 2025-12-05

Erste Veröffentlichung der DDD-Fallstudie KinoSoft.

### Hinzugefügt

- README mit Domain-Story zum Kartenverkauf-Prozess
- Zwei Bounded Contexts mit unterschiedlichen Architekturstilen:
  **Filmauswahl** (Schichtenarchitektur) und **Kartenverkauf**
  (hexagonale Architektur / Ports and Adapters)
- Filmprogramm mit Kalenderleiste und Vorstellungsliste pro Wochentag
- Kartenverkauf: Wahl der Vorstellung, Anzahl, Plätze, Zahlungsart → Kinokarten
- Ein DB-Schema pro Bounded Context
- Statische Beispieldaten mit fixem Referenzdatum
- Aggregate: `Film` (Filmauswahl) sowie `Vorstellung`, `Saalplan`,
  `Kinokarte` und `Zahlungsvorgang` (Kartenverkauf)
- jMolecules-Annotationen zur Klassifizierung der Bausteine
- ArchUnit-Tests zur automatisierten Prüfung der DDD- und Hexagonal-Regeln
- Backend: Java 21, Spring Boot 3.4.5, Spring Data JPA, H2 (in-memory)
- Frontend: Angular 20, TypeScript 5.9.3, TailwindCSS + DaisyUI
- Node 22.15.0 / npm 11.3.0 in `frontend/pom.xml` gepinnt
- Maven-Multi-Module-Build mit `frontend-maven-plugin`
- Docker-Compose-Setup und Kubernetes-Manifeste (`infrastructure/`)
- GitLab-CI/CD-Pipeline mit Build- und Test-Stages für Backend & Frontend
- SBOM-Generierung via CycloneDX
