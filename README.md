# KinoSoft - Eine Fallstudie für Domain-Driven Design (DDD)

KinoSoft ist eine Beispielanwendung, die für die [DDD-Schulung](https://www.wps.de/schulungen/isaqb/ddd)
der [WPS - Workplace Solutions GmbH](https://www.wps.de) entwickelt wurde. Die Anwendung demonstriert die
Umsetzung von DDD-Prinzipien in einer Full-Stack-Webanwendung.

Diese Implementierung ist nur eine von vielen möglichen. Unterschiedliche Annahmen, Randbedingungen, (Geschäfts-)Ziele,
und Designentscheidungen werden zu unterschiedlichen Ergebnissen führen!

## Vorgehen

In der Fallstudie werden diverse **Szenarien** aus dem Betrieb eines kleinen Programmkinos betrachtet, u.a. der
Kartenverkauf, dessen IST-Prozess in einer **Domain Story** mit [egon.io](https://egon.io) erfasst wurde:

![Kartenverkauf](docs/diagrams/Szenario-1-Kartenverkauf.egn.svg)

Aus diesen Szenarien wurden über das **Strategische Design** aus der Gesamtdomäne folgende **Subdomänen**
ermittelt:

- Wochenplanerstellung
- Filmauswahl
- Kartenverkauf
- Einlasskontrolle
- Filmvorführung
- Snackverkauf

Für die Implementierung dieser Beispielanwendung konzentrieren wir uns auf folgende **Bounded Contexts**:

- Filmauswahl
- Kartenverkauf

Diese Aufteilung ermöglicht uns pro Bounded Context ein spezifisches Fachmodell bzw. **Domänenmodel** zu entwickeln.
Die wichtigsten Begriffe der jeweiligen **Ubiquitous Language** sind in einem [Glossar](docs/Glossar.md) definiert.

## Features

Es wird zunächst die Filmauswahl, a.k.a das Kinoprogramm, angezeigt. Diese umfasst folgende Funktionen:

- Anzeige der aktuellen Woche als Kalenderleiste
- Anzeige der Filmvorstellungen des ausgewählten Wochentages

Nicht wundern: das "heutige" Datum ist im Code fest auf den 19.03.2025 gesetzt, da die Beispieldaten statisch hinterlegt
sind. Durch Klick auf eine Vorstellung gelangt man zum Kartenverkauf. Dieser umfasst folgende Funktionen:

- Anzeige der gewählten Vorstellung
- Angabe der gewünschten Anzahl von Kinokarten
- Auswahl eines Blocks zusammenhängender Plätze im Saalplan, mit initialem Vorschlag durch das System
- Zahlungsvorgang (angedeutet)
- Ausstellen der Kinokarten

## Bauen und Starten der Anwendung

Die Anwendung läuft plattformübergreifend (macOS, Linux, Windows via WSL2). Je nach Ziel gibt es drei Wege — eine
ausführliche Anleitung inklusive IDE-Hinweisen (IntelliJ IDEA, VS Code, Eclipse/STS) und Troubleshooting steht in
[docs/ENTWICKLUNGSUMGEBUNG.md](docs/ENTWICKLUNGSUMGEBUNG.md).

| Weg | Ziel | Voraussetzung |
|-----|------|---------------|
| **A – Nur ausführen** | Anwendung ansehen/benutzen | **nur Docker** |
| **B – Devcontainer** | Entwickeln ohne globale Installation | **Docker + IDE** |
| **C – Lokal entwickeln** | Entwickeln mit lokaler Toolchain | **JDK 25 + Node 24** (Maven bringt der `./mvnw`-Wrapper mit) |

### A – Nur ausführen (nur Docker)

Baut Backend und Frontend im Container und startet beides — es wird **kein** JDK, Node oder Maven auf dem Host benötigt:

```bash
docker compose up --build
```

Die Anwendung ist dann verfügbar unter: http://localhost:8081

Statt Docker funktioniert auch Podman (`podman compose up --build`) — die Images sind
rootless-tauglich. Details und der Devcontainer-Hinweis stehen in
[docs/ENTWICKLUNGSUMGEBUNG.md](docs/ENTWICKLUNGSUMGEBUNG.md).

### B – Entwickeln im Devcontainer (nur Docker + IDE)

Das Projekt enthält einen Dev Container (`.devcontainer/`) mit JDK 25 und Node 24. In VS Code über
*Dev Containers: Reopen in Container*, in IntelliJ über *Remote Development → Dev Containers*. Details in
[docs/ENTWICKLUNGSUMGEBUNG.md](docs/ENTWICKLUNGSUMGEBUNG.md).

### C – Lokal entwickeln (JDK 25 + Node 24)

Lokal werden ein **JDK 25** (Backend) und **Node 24** (Frontend) benötigt. Am einfachsten via
[mise](https://mise.jdx.dev) (`brew install mise`, dann `mise install` – das Projekt enthält eine `mise.toml`,
die beide Versionen pinnt), alternativ [SDKMAN](https://sdkman.io) / [Eclipse Temurin](https://adoptium.net) fürs
JDK. Maven kommt über den Wrapper `./mvnw`; das Angular-Frontend wird Node-nativ mit `npm` gebaut. Backend und
Frontend bauen und testen (ohne Docker):

```bash
# Backend (Build + Unit-Tests); ./mvnw verify für Integrationstests
cd backend && ./mvnw clean package

# Frontend (Build + Tests)
cd frontend && npm ci && npm run build && npm run test:ci
```

**Docker-Images werden dabei nicht erzeugt** (das übernimmt `docker compose build`, siehe Weg A). Für die
Entwicklung lassen sich Backend (`cd backend && ./mvnw spring-boot:run`, Port 8080) und Frontend-Dev-Server
(`cd frontend && npm start`, Port 4200) getrennt starten; die UI ist dann verfügbar unter: http://localhost:4200

## Technologien

Das Backend basiert u.a. auf folgenden Technologien:

- Java
- Spring Boot
- JPA/Hibernate
- Lombok
- jMolecules

Das Frontend basiert u.a. auf folgenden Technologien:

- Angular
- TypeScript
- TailwindCSS
- DaisyUI

Die vollständige SBOM (Software Bill of Materials) wird bei Bedarf erzeugt – im Backend mit
`cd backend && ./mvnw cyclonedx:makeAggregateBom` (nach `backend/target/.bom/bom.json` bzw. `bom.xml`), im Frontend mit
`cd frontend && npm run sbom` (nach `frontend/dist/.bom/bom.json`). In der CI werden die SBOMs bei jedem Commit auf
`main` automatisch in der `sbom`-Stage erzeugt und anschließend nach Dependency Track hochgeladen.

## Architektur

Die Anwendung ist als modularer Monolith (Modulith) strukturiert, dessen oberste Module auf den Bounded Contexts
basieren (fachliche Schnitte). Je Bounded Context kann ein eigener, auf die funktionalen und nicht-funktionalen
Anforderungen zurechtgeschnittener, Architekturstil gewählt werden, in diesem Fall eine Schichtenarchitektur und eine
hexagonale Architektur:

- Filmauswahl: eine simple **Schichtenarchitektur** unter direkter Verwendung der Spring Boot Bordmittel:
  RestController, Service, Repository, Entity (DTO, Domain-Entity, JPA-Entity in Einem).
- Kartenverkauf: eine **hexagonale Architektur** (Ports and Adapters) mit DDD-Bausteinen im fachlichen Kern und eigenen
  Modellen in den Adaptern (DTOs, JPA-Entities, und entsprechende Mapper).

Folgende Libraries und Tools helfen sicherzustellen, dass die Architekturregeln eingehalten werden:

- [jMolecules](https://github.com/xmolecules/jmolecules): stellt **Annotationen** wie `@PrimaryAdapter` oder
  `@AgregateRoot`, `@Entity`, und `@ValueObject` bereit, mit denen die entsprechenden Komponenten ausgezeichnet werden.
- [ArchUnit](https://www.archunit.org/): Prüft diverse **Architekturregeln** bzgl. der hexagonalen
  Architektur und der DDD-Mustersprache, z.B. dass ein `@AgregateRoot` zwar ein `@Entity` aber kein anderes
  `@AgregateRoot` enthalten darf.
- [Sonargraph](https://www.hello2morrow.com/products/sonargraph): Von uns regelmäßig in
  unseren [Architektur-Reviews](https://www.wps.de/leistungen/architektur-review) eingesetztes Tool zur explorativen
  Betrachtung der Architektur nach den drei Aspekten **technische Schichtung**, **fachliche Schichtung**,
  und **Mustersprache**.

## Lizenz

Dieses Projekt steht unter der [MIT Lizenz](LICENSE).

Copyright (c) 2025 WPS - Workplace Solutions GmbH

