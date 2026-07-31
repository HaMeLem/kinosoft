# Entwicklungsumgebung einrichten

Dieser Leitfaden beschreibt, wie KinoSoft auf den Rechnern der Schulungsteilnehmer
gebaut und gestartet wird — plattformübergreifend (macOS, Linux, Windows via WSL2)
und mit möglichst wenig globalen Installationen.

Es gibt **drei Wege**, je nach Ziel:

| Weg | Ziel | Muss lokal installiert sein |
|-----|------|-----------------------------|
| **A – Nur ausführen** | Anwendung ansehen/benutzen | **nur Docker** |
| **B – Devcontainer** | Code entwickeln, ohne JDK/Node global zu installieren | **Docker + IDE** |
| **C – Lokal entwickeln** | Code entwickeln mit lokaler Toolchain | **JDK 25 + Node 24** (Maven bringt der Wrapper mit) |

> **Was muss für Weg C lokal vorhanden sein?**
> Maven ist über den Wrapper (`./mvnw`) gekapselt — ein JDK 25 genügt für das Backend. Das
> Angular-Frontend wird Node-nativ gebaut (`npm`), daher wird für Weg C zusätzlich **Node 24**
> benötigt. Beide Versionen sind in der `mise.toml` gepinnt (`mise install` installiert sie);
> im Devcontainer (Weg B) stecken JDK und Node bereits im Container. Wege A und B brauchen
> daher gar keine lokale Toolchain außer Docker.

---

## Voraussetzungen nach Betriebssystem

### Docker-Runtime (für Weg A und B)

Eine der folgenden Container-Laufzeiten genügt — sie stellen alle das `docker`-CLI und
`docker compose` bereit:

| Plattform | Empfehlung | Alternativen |
|-----------|------------|--------------|
| macOS | Docker Desktop | Colima, Rancher Desktop, Podman Desktop |
| Linux | Docker Engine (`docker` + `docker compose`-Plugin) | Podman (mit `podman compose`) |
| Windows | **WSL2** + Docker Desktop (WSL2-Backend) | Docker Engine direkt in der WSL2-Distribution |

**Windows-Hinweis:** Arbeite innerhalb einer WSL2-Distribution (z. B. Ubuntu). Lege das
Repository im Linux-Dateisystem ab (`~/…`, **nicht** unter `/mnt/c/…`) — das ist deutlich
schneller und vermeidet Zeilenende-/Rechteprobleme. Docker Desktop mit aktiviertem
WSL2-Backend stellt `docker`/`docker compose` innerhalb der Distribution bereit.

**Podman statt Docker:** Das Projekt läuft mit Podman, da die Images bewusst rootless-tauglich
sind (beide Container laufen als nicht-privilegierte Nutzer auf unprivilegierten Ports 8080; keine
Host-Bind-Mounts, kein Docker-Socket nötig) und das `docker-compose.yml` reine Standard-Syntax
verwendet. Zwei Punkte sind zu beachten:

- **Befehle:** Bei reinem Podman (ohne `docker`-Alias/Shim) heißt der Compose-Befehl
  `podman compose …` statt `docker compose …` — z. B. `podman compose up --build` (Weg A). Podman
  Desktop bzw. eine Podman Machine stellen oft einen `docker`-Alias bereit; dann funktionieren die
  `docker compose`-Befehle dieses Leitfadens unverändert.
- **Devcontainer (Weg B):** VS Code Dev Containers und die `devcontainer`-CLI erwarten
  standardmäßig die Docker-CLI bzw. den Docker-Socket. Mit Podman muss der Podman-Socket
  bereitgestellt und darauf verwiesen werden (z. B. `DOCKER_HOST` auf den Podman-Socket setzen bzw.
  in den Dev-Containers-Einstellungen den Docker-Pfad auf `podman` umbiegen). Für „nur ausführen"
  (Weg A) ist das nicht nötig.

### JDK 25 (nur für Weg C)

Siehe [Weg C](#weg-c--lokal-entwickeln-ohne-devcontainer).

---

## Weg A – Nur ausführen (nur Docker)

Baut Backend und Frontend **im Container** und startet beides. Es wird **kein** JDK, Node
oder Maven auf dem Host benötigt.

```bash
docker compose up --build
# Mit reinem Podman stattdessen: podman compose up --build
```

- Frontend: <http://localhost:8081>
- Backend-API: <http://localhost:8080/api>

Der erste Lauf dauert länger (Basis-Images, Maven-Abhängigkeiten, npm-Pakete). Danach
greifen die Docker-Layer-Caches. Stoppen mit `Ctrl+C`, aufräumen mit:

```bash
docker compose down
```

> `--build` erzwingt den Bau aus dem Quellcode (Multi-Stage-Dockerfiles
> `backend/Dockerfile` und `frontend/Dockerfile`). Ohne `--build` verwendet
> Docker Compose ein bereits vorhandenes Image gleichen Namens. Die Images werden
> ausschließlich hierüber (`docker compose build`) gebaut — `./mvnw clean package`
> erzeugt keine Images.

---

## Weg B – Entwickeln im Devcontainer (nur Docker + IDE)

Der Dev Container (`.devcontainer/devcontainer.json`) enthält JDK 25 und Node 24. Du
entwickelst darin, ohne JDK/Node lokal zu installieren. Benötigt werden nur Docker und
eine IDE mit Devcontainer-Unterstützung. (Mit Podman ist zusätzliche Konfiguration nötig —
siehe [Podman statt Docker](#docker-runtime-für-weg-a-und-b).)

> **Abhängigkeiten werden automatisch installiert.** Beim ersten Erstellen des Containers
> werden Backend- und Frontend-Abhängigkeiten eingerichtet; Backend und Frontend lassen sich
> danach direkt starten. Host und Container haben getrenntes `node_modules` – ein
> `npm install` auf dem Host ist nicht nötig. Ändert sich `package-lock.json`, im Container
> einmalig `cd frontend && npm ci` ausführen.

### VS Code

1. Erweiterung **Dev Containers** (`ms-vscode-remote.remote-containers`) installieren.
2. Projekt öffnen → Befehlspalette (`F1`) → **Dev Containers: Reopen in Container**.
3. VS Code baut den Container (erster Lauf dauert) und installiert die empfohlenen
   Erweiterungen automatisch.
4. Backend starten: Task **„Backend: run (spring-boot:run)"** oder Debug-Konfiguration
   **„Backend (Spring Boot)"**.
5. Frontend starten: Task **„Frontend: dev server (npm start)"**.
6. Die Ports 8080, 4200 und 8081 werden automatisch weitergeleitet.

### IntelliJ IDEA

1. **Remote Development → Dev Containers → New Dev Container** und auf
   `.devcontainer/devcontainer.json` im Projekt verweisen (lokal oder aus VCS).
2. IntelliJ baut den Container und öffnet das Projekt darin.
3. Die mitgelieferten Run-Konfigurationen (`.run/`) für Backend und Frontend nutzen.

### Devcontainer-CLI (ohne IDE)

```bash
npm i -g @devcontainers/cli
devcontainer up --workspace-folder .
devcontainer exec --workspace-folder . ./mvnw clean package
```

---

## Weg C – Lokal entwickeln (ohne Devcontainer)

**JDK 25** (Backend, via `./mvnw`) und **Node 24** (Frontend, via `npm`) müssen installiert
sein. Am einfachsten über `mise` — die `mise.toml` pinnt beide Versionen.

### Toolchain installieren

**mise (macOS / Linux / WSL2 — empfohlen):** Das Projekt enthält eine `mise.toml` mit den
gepinnten Versionen (JDK 25 Temurin + Node 24).

```bash
# mise installieren (falls noch nicht vorhanden):
#   macOS:      brew install mise
#   Linux/WSL2: siehe https://mise.jdx.dev/getting-started.html
mise install      # installiert die in mise.toml gepinnten Versionen
java -version      # sollte 25 anzeigen
```

Mit eingerichtetem Shell-Hook (`mise activate`, siehe mise-Doku) aktiviert mise die Versionen
automatisch beim Betreten des Projektverzeichnisses. `mise` verwaltet hier Java **und** Node.

**Alternativen:**

- **SDKMAN:** `sdk install java 25.0.3-tem` (Installation via <https://sdkman.io>).
- **Manueller Download:** Eclipse Temurin 25 von <https://adoptium.net> installieren und
  `JAVA_HOME` setzen.
- **macOS (Homebrew):** `brew install --cask temurin@25`.

### Bauen und starten

Backend und Frontend werden mit ihrem jeweiligen Werkzeug gebaut und getestet (ohne Docker;
es entstehen **keine** Images — die kommen über `docker compose build`, siehe Weg A):

```bash
# Backend (Build + Unit-Tests); ./mvnw verify für zusätzliche Integrationstests
cd backend && ./mvnw clean package

# Frontend (Build + Tests)
cd frontend && npm ci && npm run build && npm run test:ci
```

`backend/` ist ein eigenständiges Maven-Projekt mit eigenem Wrapper — die `./mvnw`-Befehle
werden aus dem `backend/`-Verzeichnis ausgeführt.

Für die Entwicklung Backend und Frontend getrennt starten (mit Live-Reload):

```bash
# Terminal 1 – Backend (Port 8080)
cd backend && ./mvnw spring-boot:run

# Terminal 2 – Frontend-Dev-Server (Port 4200, Proxy /api -> :8080)
cd frontend && npm start
```

- UI (Entwicklung): <http://localhost:4200>
- H2-Konsole: <http://localhost:8080/h2-console>
  (JDBC-URL `jdbc:h2:mem:testdb`, Benutzer `sa`, kein Passwort)

### Tests

```bash
# Backend (aus backend/)
cd backend && ./mvnw test     # nur Unit-Tests (schnell)
cd backend && ./mvnw verify   # Unit- + Integrationstests

# Frontend (aus frontend/)
cd frontend && npm run test:ci
```

---

## IDE-Hinweise

### IntelliJ IDEA

- **Einmaliger Maven-Import (wichtig, sonst Compile-Fehler):** Beim Öffnen des
  Repository-Roots liegt `backend/pom.xml` in einem Unterverzeichnis — IntelliJ importiert es
  **nicht zuverlässig automatisch**. Den Import einmalig abschließen: Maven-Werkzeugfenster
  öffnen (*View → Tool Windows → Maven*), bei `backend/pom.xml` per Rechtsklick
  *Add as Maven Project* wählen und anschließend *Reload All Maven Projects* (Icon mit den
  kreisenden Pfeilen) klicken. Erst danach existiert das Modul `backend`, auf das sich die
  Run-Konfiguration `.run/backend.run.xml` bezieht.
  Alternativ direkt `backend/` als eigenes Projekt öffnen (dann liegt die `pom.xml` im Wurzel-
  verzeichnis und wird sofort importiert) und `frontend/` separat — das entspricht den zwei
  eigenständigen Projekten dieses Repos.
- Die Run-Konfigurationen unter `.run/` (`backend`, `frontend`, `application`) werden
  automatisch erkannt. `frontend` läuft pfadbasiert sofort; `backend` bindet erst nach dem
  Maven-Import (s. o.).
- **Project SDK** auf ein JDK 25 setzen: *File → Project Structure → Project → SDK*.
  Fehlt ein JDK 25, kann IntelliJ es unter *Add SDK → Download JDK* (Temurin 25) laden.
- Alternativ den [Devcontainer](#intellij-idea) verwenden — dann ist kein lokales JDK nötig.

### VS Code

- Empfohlene Erweiterungen werden aus `.vscode/extensions.json` vorgeschlagen (Java-Pack,
  Spring Boot, Angular, ESLint, Dev Containers).
- Starten/Debuggen über die Konfiguration in `.vscode/launch.json` und die Tasks in
  `.vscode/tasks.json`.
- Für die Entwicklung ohne lokales JDK den [Devcontainer](#vs-code) nutzen.

### Eclipse / Spring Tool Suite (STS)

1. *File → Import → Existing Maven Projects* und das `backend/`-Verzeichnis wählen (das Backend
   ist ein eigenständiges Maven-Projekt; das Frontend wird separat mit npm gebaut).
2. Unter *Preferences → Java → Installed JREs* ein JDK 25 registrieren und dem Projekt
   zuordnen (Compiler-Level 25).
3. Backend über die Klasse `de.wps.ddd.kino.Application` als *Spring Boot App* /
   *Java Application* starten.
4. Frontend über ein Terminal mit `cd frontend && npm start`.

### Generisches CLI (ohne IDE)

Alle Befehle laufen ohne IDE — siehe [Weg C](#bauen-und-starten). Benötigt werden ein lokales
JDK 25 (Backend via `./mvnw`) und Node 24 (Frontend via `npm`); beides pinnt die `mise.toml`.

---

## Troubleshooting

- **JDK-Version in `mise.toml` nicht auflösbar:** Verfügbare Builds mit
  `mise ls-remote java | grep temurin-25` prüfen und die Version in `mise.toml` anpassen
  (mise akzeptiert Präfixe, z. B. `temurin-25.0.3` → `temurin-25.0.3+9.0.LTS`).
- **`docker compose up --build` schlägt beim ersten Lauf fehl (Netzwerk/Timeouts):**
  Erneut ausführen — Layer-Caches setzen den Build fort. Genügend Speicher/Plattenplatz
  für Docker bereitstellen (Backend-Build zieht Maven-Abhängigkeiten, Frontend npm-Pakete).
- **IntelliJ – `Cannot compile module 'backend' configured for JVM target 5` (fallback SDK 25):**
  Der Maven-Import wurde nicht abgeschlossen, daher baut IntelliJ gegen ein generisches
  Wurzel-Modul ohne Sprachlevel (Default JVM-Ziel 5). Den einmaligen Maven-Import nachholen
  (Maven-Werkzeugfenster → *Add as Maven Project* auf `backend/pom.xml` → *Reload All Maven
  Projects*, siehe [IntelliJ IDEA](#intellij-idea)). Danach existiert das Modul `backend` mit
  Sprachlevel 25 und der Fehler verschwindet.
- **Windows/WSL2 – Ports nicht erreichbar:** Repository im Linux-Dateisystem der
  WSL2-Distribution ablegen (nicht `/mnt/c/…`) und `localhost:8081`/`:4200` aus Windows
  heraus aufrufen (WSL2 leitet `localhost` weiter).
