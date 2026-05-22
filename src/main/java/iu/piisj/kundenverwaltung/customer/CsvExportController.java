package iu.piisj.kundenverwaltung.customer;

import iu.piisj.kundenverwaltung.auth.AuthController;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * COULD C-1: CSV-Export der Kundenliste.
 * Eigener @RequestScoped Controller — hält keinen Zustand, löst nur den Download aus.
 */
@Named
@RequestScoped
public class CsvExportController {

    @Inject
    private CustomerService customerService;

    @Inject
    private AuthController authController;

    public void exportCsv() throws IOException {
        if (!authController.canEdit()) {
            return; // AuthFilter verhindert das bereits auf Seiten-Ebene
        }

        List<Customer> customers = customerService.getAllCustomers();

        FacesContext    fc  = FacesContext.getCurrentInstance();
        ExternalContext ec  = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("text/csv; charset=UTF-8");
        ec.setResponseCharacterEncoding("UTF-8");
        ec.setResponseHeader("Content-Disposition",
                "attachment; filename=\"kunden_" + LocalDate.now() + ".csv\"");

        PrintWriter writer = new PrintWriter(ec.getResponseOutputStream());

        // UTF-8 BOM damit Excel die Datei korrekt öffnet
        writer.print('\uFEFF');

        // Kopfzeile
        writer.println("Kundennummer;Vorname;Nachname;E-Mail;Telefon;Unternehmen;Angelegt am");

        // Datenzeilen
        for (Customer c : customers) {
            writer.printf("%s;%s;%s;%s;%s;%s;%s%n",
                    escapeCsv(c.getCustomerNumber()),
                    escapeCsv(c.getFirstName()),
                    escapeCsv(c.getLastName()),
                    escapeCsv(c.getEmail()),
                    escapeCsv(c.getPhone()),
                    escapeCsv(c.getCompany()),
                    c.getCreatedAt()
            );
        }

        writer.flush();
        fc.responseComplete(); // JSF soll danach nichts mehr rendern
    }

    // Semikolon und Anführungszeichen in Feldinhalten escapen
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
