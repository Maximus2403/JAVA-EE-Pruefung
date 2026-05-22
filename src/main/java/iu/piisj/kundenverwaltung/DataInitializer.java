package iu.piisj.kundenverwaltung;

import iu.piisj.kundenverwaltung.customer.Customer;
import iu.piisj.kundenverwaltung.project.Project;
import iu.piisj.kundenverwaltung.repository.CustomerRepository;
import iu.piisj.kundenverwaltung.repository.UserRepository;
import iu.piisj.kundenverwaltung.user.User;
import iu.piisj.kundenverwaltung.user.UserRole;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Befüllt die Datenbank beim ersten Start mit Testdaten (Aufgabe e).
 * Wird nur ausgeführt, wenn noch keine Benutzer vorhanden sind.
 */
@ApplicationScoped
public class DataInitializer {

    @Inject
    private UserRepository userRepository;

    @Inject
    private CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        // Nur initialisieren wenn die DB noch leer ist
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        seedUsers();
        seedCustomers();
    }

    private void seedUsers() {
        String pw = BCrypt.hashpw("passwort123", BCrypt.gensalt());

        userRepository.save(new User("admin",         "admin@firma.de",
                "Anna",  "Admin",       pw, UserRole.ADMIN));
        userRepository.save(new User("sachbearbeiter","sb@firma.de",
                "Stefan","Bearbeiter",  pw, UserRole.SACHBEARBEITER));
        userRepository.save(new User("leser",         "leser@firma.de",
                "Lisa",  "Leser",       pw, UserRole.READONLY));
    }

    private void seedCustomers() {
        // Kunde 1 — mit zwei Projekten (demonstriert COULD C-2 Projektbaum)
        Customer mueller = new Customer("KD-0001", "Max", "Müller",
                "max.mueller@example.com", "0211-111111", "Müller GmbH");
        customerRepository.save(mueller);

        Project crm = new Project("CRM-Einführung",
                "Einführung eines neuen CRM-Systems", "IN_BEARBEITUNG", mueller);
        Project crmPhase1 = new Project("Phase 1 – Analyse",
                "Anforderungsanalyse und IST-Aufnahme", "ABGESCHLOSSEN", mueller);
        crmPhase1.setParentProject(crm);
        Project crmPhase2 = new Project("Phase 2 – Implementierung",
                "Technische Umsetzung und Datenmigration", "OFFEN", mueller);
        crmPhase2.setParentProject(crm);

        // Projekte über CustomerRepository speichern (cascade=ALL)
        mueller.getProjects().add(crm);
        mueller.getProjects().add(crmPhase1);
        mueller.getProjects().add(crmPhase2);
        customerRepository.save(mueller);

        // Kunde 2
        Customer schmidt = new Customer("KD-0002", "Sabine", "Schmidt",
                "s.schmidt@techcorp.de", "089-222222", "TechCorp AG");
        customerRepository.save(schmidt);

        // Kunde 3
        Customer wagner = new Customer("KD-0003", "Thomas", "Wagner",
                "t.wagner@logistik.de", "0201-333333", "Wagner Logistik");
        customerRepository.save(wagner);

        // Kunde 4
        Customer becker = new Customer("KD-0004", "Maria", "Becker",
                "m.becker@freelance.de", "030-444444", null);
        customerRepository.save(becker);

        // Kunde 5
        Customer hoffmann = new Customer("KD-0005", "Klaus", "Hoffmann",
                "k.hoffmann@handel.de", "0711-555555", "Hoffmann Handel KG");
        customerRepository.save(hoffmann);
    }
}
