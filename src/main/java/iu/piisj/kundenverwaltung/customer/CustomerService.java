package iu.piisj.kundenverwaltung.customer;

import iu.piisj.kundenverwaltung.dto.CustomerDTO;
import iu.piisj.kundenverwaltung.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Geschäftslogik für Kundenverwaltung.
 * Trennt die fachliche Logik vom Repository (DB-Zugriff) und vom Controller (View-Logik).
 */
@ApplicationScoped
public class CustomerService {

    @Inject
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return getAllCustomers();
        }
        return customerRepository.search(searchTerm.trim());
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id);
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.delete(id);
    }

    // Erzeugt aus dem DTO eine neue Customer-Entität (analog zu mapDTOToEvent im Referenzprojekt)
    public Customer mapDTOToCustomer(CustomerDTO dto) {
        return new Customer(
                dto.getCustomerNumber(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getCompany()
        );
    }

    // Überträgt geänderte DTO-Werte auf eine bestehende Entität (für Bearbeiten-Funktion)
    public void applyDTOToCustomer(CustomerDTO dto, Customer customer) {
        customer.setCustomerNumber(dto.getCustomerNumber());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setCompany(dto.getCompany());
    }
}
