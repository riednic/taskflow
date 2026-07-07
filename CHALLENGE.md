# 💻 Coding Challenge — TaskFlow Workflow Engine

---

## 📋 Kontext

Du entwickelst das Backend einer **Workflow-Management-Plattform**. Tasks durchlaufen definierte Zustandsmaschinen, mehrere Benutzer können gleichzeitig arbeiten, und bestimmte Statusübergänge lösen asynchrone Folgeaktionen aus.
Das **Framework und die Bibliotheken sind dir überlassen** – du begründest deine Wahl in der Präsentation.
Der Code soll so aussehen, als würde er in einem echten Kundenprojekt landen. Wir bewerten nicht nur **ob** es funktioniert, sondern vor allem **wie** es gebaut wurde.

> 💡 **Erwarteter Zeitaufwand:** ca. 6–8 Stunden. Die 1-Woche-Frist ist als Planungspuffer gedacht – kein Vollzeiteinsatz erwartet.

---

## 🎯 1. Fachliche Anforderungen (Must-Have)

---

### 👤 Benutzer (Users)

| Feld | Detail |
| --- | --- |
| `id` | Eindeutige ID |
| `name` | Pflichtfeld |
| `email` | Pflichtfeld, eindeutig |
| `password` | Pflichtfeld |
| `role` | `ADMIN`, `MEMBER`, `REVIEWER` |
| `createdAt` | Zeitstempel |

**Operationen:**

- Benutzer anlegen (`POST /users`) – nur `ADMIN`
- Benutzer abrufen (`GET /users`, `GET /users/{id}`) – `ADMIN` und `REVIEWER`

---

### ✅ Aufgaben (Tasks)

| Feld | Detail |
| --- | --- |
| `id` | Eindeutige ID |
| `title` | Pflichtfeld |
| `description` | Optional |
| `status` | `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `CANCELLED`, `REJECTED` |
| `priority` | `LOW`, `MEDIUM`, `HIGH` |
| `assignedTo` | Referenz auf einen User (optional) |
| `version` | Versionsnummer für Optimistic Locking |
| `createdAt` / `updatedAt` | Zeitstempel |

**Operationen:**

- Task erstellen
- Task abrufen (Liste & Einzelabruf)
- Task aktualisieren
- Task löschen – nur `ADMIN`
- Statusübergang auslösen

---

### 🔄 Workflow-Zustandsmaschine

```
TODO → IN_PROGRESS → IN_REVIEW → DONE
 ↓          ↓             ↓
 └──────────┴─────────► CANCELLED
                          ↓
                       REJECTED → IN_PROGRESS
```

**Erlaubte Übergänge im Überblick:**

| Von | Nach | Erlaubt |
| --- | --- | --- |
| `TODO` | `IN_PROGRESS` | ✅ |
| `TODO` | `CANCELLED` | ✅ |
| `IN_PROGRESS` | `IN_REVIEW` | ✅ |
| `IN_PROGRESS` | `CANCELLED` | ✅ |
| `IN_REVIEW` | `DONE` | ✅ |
| `IN_REVIEW` | `REJECTED` | ✅ |
| `IN_REVIEW` | `CANCELLED` | ✅ |
| `REJECTED` | `IN_PROGRESS` | ✅ |
| `DONE` | (beliebig) | ❌ Terminaler Zustand |
| `CANCELLED` | (beliebig) | ❌ Terminaler Zustand |

**Regeln:**

- Ungültige Übergänge werden mit einem sprechenden Fehler abgelehnt
- `DONE` und `CANCELLED` sind **terminale Zustände** – keine weiteren Übergänge möglich
- `REJECTED` ist **kein** terminaler Zustand – ein abgelehnter Task kann via `REJECTED → IN_PROGRESS` erneut bearbeitet werden
- Jeder Übergang wird mit **Zeitstempel + ausführendem User** im Audit-Log persistiert
- Der Transition-Request muss die aktuelle **`version`** des Tasks enthalten – veraltete Version → `409 Conflict`

---

### 🔒 Rollenbasierte Zugriffslogik

- Nur der **zugewiesene User** oder ein `ADMIN` darf einen Task auf `IN_PROGRESS` setzen
- Jeder authentifizierte User (`MEMBER`, `REVIEWER`, `ADMIN`) darf einen Task auf `IN_REVIEW` oder `CANCELLED` setzen, sofern er der **zugewiesene User** ist oder die Rolle `ADMIN` besitzt
- Nur ein `REVIEWER` oder `ADMIN` darf einen Task auf `DONE` oder `REJECTED` setzen
- Regelbruch → `403 Forbidden` mit sprechender Fehlermeldung

---

### 🔍 Filterung & Sortierung

Die Listenabfrage unterstützt:

- Filtern nach `status` und/oder `priority`
- Filtern nach zugewiesenem User
- Sortierung nach `createdAt` oder `priority`

---

### 📊 Statistik-Endpunkt

- Anzahl Tasks pro Status
- Durchschnittliche Zeit (in Stunden) von `TODO` → `DONE`
- Top 3 User nach Anzahl abgeschlossener Tasks
- Die Abfrage muss **performant** sein – kein N+1-Problem

> 📝 **Hinweis:** Der Endpunkt ist auf `REVIEWER` und `ADMIN` beschränkt, da aggregierte Produktivitätsdaten anderer User als sensibel eingestuft werden und `MEMBER` ausschließlich ihre eigenen Tasks verwalten.

---

### ⚡ Optimistic Locking

- Tasks besitzen eine `version`-Nummer
- Beim Update **und** bei Statusübergängen muss die aktuelle Version mitgeschickt werden
- Veraltete Version → `409 Conflict` mit aussagekräftiger Fehlermeldung

> 📝 **Hinweis:** Optimistic Locking gilt ausdrücklich auch für Transitionen, da gleichzeitige Statusänderungen durch mehrere User zu inkonsistenten Zuständen führen können.

---

### 📬 Asynchrone Benachrichtigungen

Wenn ein Task auf `IN_REVIEW` gesetzt wird, soll **asynchron (non-blocking)** eine Benachrichtigung an alle Benutzer mit der Rolle `REVIEWER` ausgelöst werden:

- Implementierung ist frei wählbar (z. B. Event, Coroutine, Message Queue)
- Die Benachrichtigung wird geloggt oder in einer Tabelle persistiert
- **Begründe** deine Entscheidung in der README

---

## 🔐 2. Authentifizierung & Autorisierung (Must-Have)

---

### Login-Endpunkt

#### `POST /auth/login`

```json
// Request
{
  "email": "max.mustermann@example.com",
  "password": "sicher123"
}

// Response 200
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

---

### Geschützte Endpunkte

Alle Endpunkte außer `POST /auth/login` erfordern einen gültigen JWT im Header:

```
Authorization: Bearer <token>
```

---

### JWT-Anforderungen

| Anforderung | Detail |
| --- | --- |
| **Algorithmus** | `HS256` (mindestens) |
| **Pflicht-Claims** | `sub` (User-ID), `role`, `iat`, `exp` |
| **Ablaufzeit** | Konfigurierbar, Standard: 60 Minuten |
| **Signierungsschlüssel** | Über Umgebungsvariable injiziert – **niemals hardcoded** |
| **Validierung** | Signatur, Ablaufzeit und Pflichtfelder müssen geprüft werden |

---

### Rollenbeschreibung

**`MEMBER`**
Kann Tasks erstellen, abrufen und eigene Tasks aktualisieren. Darf Statusübergänge auslösen, sofern der Task ihm zugewiesen ist – ausgenommen die Übergänge nach `DONE` und `REJECTED`. Hat keinen Zugriff auf die User-Verwaltung oder den Statistik-Endpunkt.

**`REVIEWER`**
Hat alle Rechte eines `MEMBER`. Kann zusätzlich alle User abrufen und Statusübergänge nach `DONE` sowie `REJECTED` auslösen. Hat Zugriff auf den Statistik-Endpunkt.

**`ADMIN`**
Hat vollständige Rechte auf alle Ressourcen. Kann User anlegen, Tasks löschen, Statusübergänge unabhängig von der Zuweisung auslösen sowie fremde Tasks aktualisieren.

---

### Einheitliche Fehlerantworten

Das folgende Fehlerdatenmodell ist verbindlich für alle Fehlerfälle:

```json
{
  "error": "INVALID_TRANSITION",
  "message": "Transition from DONE to IN_PROGRESS is not allowed.",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## ⚙️ 3. Technische Anforderungen (Must-Have)

- Sprache: **Kotlin**
- Framework: **frei wählbar** – Wahl wird in der Präsentation begründet
- Klare **Schichtentrennung** (mindestens Presentation / Service / Persistence)
- **Persistenz:** Beliebige Datenbank (In-Memory wie H2 ist erlaubt, muss aber begründet werden)
- **Datenbankmigrationen** über Flyway oder Liquibase – inkl. initialem Admin-Seeding
- **Konfiguration** ausschließlich über Umgebungsvariablen oder Konfigurationsdateien – keine hardcodierten Werte
- **Globales, einheitliches Error Handling**
- **Input-Validierung** auf allen Endpunkten

---

### 🧪 Teststrategie

**Unit Tests:**

- Zustandsmaschinen-Logik (alle erlaubten und ungültigen Übergänge)
- Berechtigungsprüfung (Rollenlogik)
- JWT-Validierung (gültiger Token, abgelaufener Token, manipulierter Token)

**Integrationstests:**

- Mindestens ein vollständiger Happy-Path: Login → Task erstellen → Statusübergang durchführen

---

### 📄 README.md (Pflicht)

- **Architekturübersicht** (auch als ASCII-Diagramm)
- **Setup & Start-Anleitung** (lokal lauffähig in unter 5 Minuten)
- **Beschreibung des initialen Admin-Seedings** – Wie wird der erste Admin-User angelegt und wie werden die Credentials konfiguriert?
- **Begründung** der Framework- und Library-Wahl inkl. Alternativen
- **Begründung** der gewählten Benachrichtigungsimplementierung
- **Bekannte Limitierungen** – Was würdest du bei mehr Zeit anders machen?

---

## ✨ 4. Optionale Erweiterungen (Nice-to-Have)

| Feature | Detail |
| --- | --- |
| 🐳 **Docker / docker-compose** | App lokal per `docker-compose up` startbar |
| 📄 **OpenAPI / Swagger UI** | Automatische API-Dokumentation |
| 📄 **Paginierung** | Listenabfragen mit `page` + `size` paginierbar |
| 🔄 **Token Refresh** | `POST /auth/refresh` via Refresh Token |
| 🚪 **Logout / Token-Blocklist** | `POST /auth/logout` mit In-Memory oder Redis Blocklist |
| 🔑 **RS256 statt HS256** | Asymmetrisches Schlüsselpaar (Public/Private Key) |
| 📬 **Echter Message Broker** | RabbitMQ oder Redis Pub/Sub für Benachrichtigungen |
| 🔁 **Retry-Mechanismus** | Fehlgeschlagene Benachrichtigungen werden wiederholt |
| 📈 **Stats-Caching** | Statistik-Endpunkt mit Invalidierungsstrategie gecacht |

---

## 📦 5. Abgabe

| Was | Detail |
| --- | --- |
| **Format** | Öffentliches Git-Repository (GitHub, GitLab o. ä.) |
| **Deadline** | 1 Woche ab Erhalt dieser Aufgabe |
| **Abgabe per** | Link zum Repository per E-Mail |
| **Branches** | Ein sauberer `main`-Branch – Commit-Historie darf sichtbar sein |

> ⚠️ **Wichtig:** Bitte keine „Quick & Dirty"-Lösung einreichen. Der Code soll so aussehen, als würde er in einem echten Kundenprojekt landen.