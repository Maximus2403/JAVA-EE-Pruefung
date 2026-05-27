# Kundenverwaltungssystem

Ein webbasiertes Kundenverwaltungssystem als Java-EE-Prototyp, entwickelt im Rahmen des Moduls **DSBPIIJEE01 – Programmierung industrieller Informationssysteme mit Java EE** an der IU Internationalen Hochschule.

---

## Inhaltsverzeichnis

- [Features](#features)
- [Tech-Stack](#tech-stack)
- [Projektstruktur](#projektstruktur)
- [Voraussetzungen](#voraussetzungen)
- [Installation & Start](#installation--start)
- [Testdaten](#testdaten)
- [Architektur](#architektur)

---

## Features

| Priorität | Story | Beschreibung |
|-----------|-------|--------------|
| MUST | M-1 | Kunden anlegen, bearbeiten und löschen (CRUD) |
| MUST | M-2 | Kunden nach Name, Kundennummer oder Unternehmen suchen |
| MUST | M-3 | Rollenbasierter Zugriff (ADMIN / SACHBEARBEITER / READONLY) |
| COULD | C-1 | CSV-Export der Kundenliste |
| COULD | C-2 | Projekte mit Unterprojekten (Baumstruktur) je Kunde verwalten |

---

## Tech-Stack

| Schicht | Technologie | Version |
|---------|-------------|---------|
| View | JSF (Mojarra) + PrimeFaces | 4.0.5 / 14.0.0 |
| Controller | CDI (Weld) | 5.1.2 |
| Persistenz | JPA / Hibernate ORM | 3.1.0 / 6.4.4 |
| Datenbank | PostgreSQL | 16 |
| Server | Apache Tomcat | 10.1.x |
| Build | Maven | 3.x |
| Java | Jakarta EE | 11 / Java 25 |
| Sicherheit | BCrypt (jBCrypt) | 0.4 |

---

## Projektstruktur

```
src/
├── main/
│   ├── java/iu/piisj/kundenverwaltung/
│   │   ├── auth/
│   │   │   ├── AuthController.java       # @SessionScoped — Login-Status & Rollenchecks
│   │   │   ├── AuthFilter.java           # @WebFilter — schützt /mgmt/* und /admin/*
│   │   │   ├── AuthService.java          # BCrypt-Login & Registrierung
│   │   │   ├── LoginController.java      # @ViewScoped — Login-Formular
│   │   │   ├── RegistrationController.java
│   │   │   └── SessionUser.java          # Leichtgewichtiges Session-Objekt
│   │   │
│   │   ├── customer/
│   │   │   ├── Customer.java             # @Entity
│   │   │   ├── CustomerController.java   # @ViewScoped — CRUD + Suche
│   │   │   ├── CustomerService.java      # Geschäftslogik & DTO-Mapping
│   │   │   └── CsvExportController.java  # @RequestScoped — CSV-Download
│   │   │
│   │   ├── project/
│   │   │   ├── Project.java              # @Entity — selbstreferenzierend
│   │   │   ├── ProjectController.java    # @SessionScoped — Projektbaum
│   │   │   ├── ProjectNode.java          # Hilfsobjekt für flache Baumdarstellung
│   │   │   └── ProjectService.java
│   │   │
│   │   ├── repository/
│   │   │   ├── CustomerRepository.java   # DAO — CRUD + Suche
│   │   │   ├── ProjectRepository.java    # DAO — Baum-Queries mit JOIN FETCH
│   │   │   └── UserRepository.java       # DAO — Benutzersuche
│   │   │
│   │   ├── dto/
│   │   │   └── CustomerDTO.java          # Entkoppelt Formular von JPA-Entität
│   │   │
│   │   ├── user/
│   │   │   ├── User.java                 # @Entity
│   │   │   ├── UserAdminController.java  # @ViewScoped — Rollenvergabe
│   │   │   └── UserRole.java             # Enum: ADMIN / SACHBEARBEITER / READONLY
│   │   │
│   │   └── DataInitializer.java          # @ApplicationScoped — Testdaten beim Start
│   │
│   ├── resources/META-INF/
│   │   ├── persistence.xml               # JPA-Konfiguration & DB-Verbindung
│   │   └── beans.xml                     # CDI-Aktivierung
│   │
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── templates/layout.xhtml    # Gemeinsames Facelets-Template
│       │   ├── web.xml                   # Servlet-Konfiguration
│       │   └── faces-config.xml          # JSF-Konfiguration (Locale: de)
│       ├── index.xhtml                   # Startseite
│       ├── login.xhtml
│       ├── register.xhtml
│       ├── customers.xhtml               # Hauptseite — Kundenliste + Projekte
│       ├── error.xhtml
│       ├── mgmt/projects.xhtml           # Geschützt: nur SACHBEARBEITER / ADMIN
│       ├── admin/users.xhtml             # Geschützt: nur ADMIN
│       └── resources/css/style.css
```

---

## Voraussetzungen

- **Java 25** (Temurin oder OpenJDK)
- **Apache Tomcat 10.1.x** — [Download](https://tomcat.apache.org/download-10.cgi)
- **PostgreSQL 16** — lokal installiert und gestartet
- **Maven 3.x**
- **IntelliJ IDEA** (empfohlen) oder eine andere Java-IDE

---

## Installation & Start

### 1. Repository klonen

```bash
git clone https://github.com/Maximus2403/JAVA-EE-Pruefung.git
cd JAVA-EE-Pruefung
```

### 2. PostgreSQL-Datenbank anlegen

```sql
CREATE DATABASE kundenverwaltung;
```

### 3. Datenbankverbindung konfigurieren

In `src/main/resources/META-INF/persistence.xml` die Zugangsdaten anpassen:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:postgresql://localhost:5432/kundenverwaltung"/>
<property name="jakarta.persistence.jdbc.user"     value="postgres"/>
<property name="jakarta.persistence.jdbc.password" value="DEIN_PASSWORT"/>
```

> **Hinweis:** `hibernate.hbm2ddl.auto=update` legt alle Tabellen beim ersten Start automatisch an — kein manuelles SQL nötig.

### 4. Projekt bauen

```bash
mvn clean package
```

### 5. In IntelliJ deployen

1. `Run → Edit Configurations → + → Tomcat Server → Local`
2. **Tomcat home:** Pfad zu deiner Tomcat-Installation (z. B. `/opt/tomcat`)
3. **Deployment-Tab:** `+` → `Artifact` → `kundenverwaltung:war exploded`
4. **Application context:** `/kundenverwaltung`
5. Grünen Play-Button drücken

### 6. Im Browser öffnen

```
http://localhost:8080/kundenverwaltung
```

---

## Testdaten

Der `DataInitializer` befüllt die Datenbank automatisch beim ersten Start:

| Benutzername | Passwort | Rolle | Berechtigungen |
|---|---|---|---|
| `admin` | `passwort123` | ADMIN | Alles inkl. Benutzerverwaltung |
| `sachbearbeiter` | `passwort123` | SACHBEARBEITER | Kunden & Projekte bearbeiten, CSV-Export |
| `leser` | `passwort123` | READONLY | Nur lesen und suchen |

Außerdem werden 5 Beispielkunden (KD-0001 bis KD-0005) und ein Projektbaum mit Ober- und Unterprojekten angelegt.

> Zum Zurücksetzen: Tabellen in PostgreSQL droppen oder `hbm2ddl.auto=create` kurz setzen, danach wieder auf `update` zurückstellen.

---

## Architektur

Eine detaillierte Dokumentation der Architektur — UML-Klassendiagramm, ER-Diagramm und Schichtenarchitektur — findet sich in:

👉 **[ARCHITECTURE.md](ARCHITECTURE.md)**

### Zugriffskontrolle

| Pfad | Erlaubte Rollen |
|------|----------------|
| `/customers.xhtml` | Alle eingeloggten Nutzer |
| `/mgmt/*` | SACHBEARBEITER, ADMIN |
| `/admin/*` | ADMIN |
| `/login.xhtml`, `/register.xhtml` | Öffentlich |

Der `AuthFilter` prüft bei jedem Request per CDI-Injection den `@SessionScoped` `AuthController`. Nicht eingeloggte Nutzer werden automatisch zur Login-Seite weitergeleitet.

---

*IU Internationale Hochschule · DSBPIIJEE01 · SoSe 2026 · Maximilian Wilk*
