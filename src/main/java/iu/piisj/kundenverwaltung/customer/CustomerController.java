package iu.piisj.kundenverwaltung.customer;

import iu.piisj.kundenverwaltung.auth.AuthController;
import iu.piisj.kundenverwaltung.dto.CustomerDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Controller für die Kundenverwaltungs-View.
 * @ViewScoped: Bean lebt so lange die Seite geöffnet ist (analog zu EventController).
 */
@Named
@ViewScoped
public class CustomerController implements Serializable {

    @Inject
    private CustomerService customerService;

    @Inject
    private AuthController authController;

    private List<Customer> customers;
    private CustomerDTO    newCustomer  = new CustomerDTO();
    private Customer       selectedCustomer;   // für Bearbeiten-Dialog
    private String         searchTerm   = "";

    @PostConstruct
    public void init() {
        customers = customerService.getAllCustomers();
    }

    // MUST M-1: Kunden anlegen
    public void saveCustomer() {
        if (!authController.canEdit()) {
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Nicht erlaubt", "Nur Sachbearbeiter und Admins dürfen Kunden anlegen.");
            return;
        }
        Customer entity = customerService.mapDTOToCustomer(newCustomer);
        customerService.saveCustomer(entity);
        newCustomer = new CustomerDTO();        // Formular zurücksetzen
        customers   = customerService.getAllCustomers(); // Liste neu laden
        addMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Kunde wurde gespeichert.");
    }

    // MUST M-1: Kunden bearbeiten (speichert selectedCustomer)
    public void updateCustomer() {
        if (!authController.canEdit()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Nicht erlaubt", "Keine Berechtigung.");
            return;
        }
        customerService.saveCustomer(selectedCustomer);
        customers = customerService.getAllCustomers();
        addMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Kunde wurde aktualisiert.");
    }

    // MUST M-1: Kunden löschen
    public void deleteCustomer(Customer customer) {
        if (!authController.canEdit()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Nicht erlaubt", "Keine Berechtigung.");
            return;
        }
        customerService.deleteCustomer(customer.getId());
        customers = customerService.getAllCustomers();
        addMessage(FacesMessage.SEVERITY_INFO, "Erfolg",
                "Kunde " + customer.getFullName() + " wurde gelöscht.");
    }

    // MUST M-2: Suche
    public void search() {
        customers = customerService.searchCustomers(searchTerm);
    }

    public void resetSearch() {
        searchTerm = "";
        customers  = customerService.getAllCustomers();
    }

    // Hilfsmethode für FacesMessages (identisch zum Referenzprojekt)
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // Getter & Setter
    public List<Customer> getCustomers()       { return customers; }
    public CustomerDTO    getNewCustomer()      { return newCustomer; }
    public Customer       getSelectedCustomer() { return selectedCustomer; }
    public void           setSelectedCustomer(Customer c) { this.selectedCustomer = c; }
    public String         getSearchTerm()       { return searchTerm; }
    public void           setSearchTerm(String s) { this.searchTerm = s; }
}
