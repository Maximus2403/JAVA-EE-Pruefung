package kundenverwaltung.user;

public enum UserRole {
    ADMIN,          // Vollzugriff: Benutzer verwalten, alles lesen/schreiben
    SACHBEARBEITER, // Kunden und Projekte anlegen, bearbeiten, löschen; CSV-Export
    READONLY        // Nur Kundenliste lesen und suchen
}
