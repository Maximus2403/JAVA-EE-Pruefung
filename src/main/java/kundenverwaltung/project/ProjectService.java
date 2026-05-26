package kundenverwaltung.project;

import kundenverwaltung.customer.Customer;
import kundenverwaltung.repository.CustomerRepository;
import kundenverwaltung.repository.ProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Geschäftslogik für Projekte.
 * Trennt fachliche Regeln vom Repository (DB) und Controller (View).
 */
@ApplicationScoped
public class ProjectService {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private CustomerRepository customerRepository;

    public List<Project> getRootProjects(Long customerId) {
        return projectRepository.findRootsByCustomer(customerId);
    }

    public List<Project> getAllProjectsForCustomer(Long customerId) {
        return projectRepository.findAllByCustomer(customerId);
    }

    public Project getById(Long id) {
        return projectRepository.findById(id);
    }

    // Neues Wurzelprojekt anlegen
    public void createRootProject(String name, String description,
                                   String status, Long customerId) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Kunde nicht gefunden: " + customerId);
        }
        Project project = new Project(name, description, status, customer);
        projectRepository.save(project);
    }

    // Unterprojekt anlegen
    public void createSubProject(String name, String description,
                                  String status, Long customerId, Long parentId) {
        Customer customer = customerRepository.findById(customerId);
        Project parent   = projectRepository.findById(parentId);
        if (customer == null || parent == null) {
            throw new IllegalArgumentException("Kunde oder Oberprojekt nicht gefunden.");
        }
        Project sub = new Project(name, description, status, customer);
        sub.setParentProject(parent);
        projectRepository.save(sub);
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.delete(id);
    }

    public List<String> getAvailableStatuses() {
        return List.of("OFFEN", "IN_BEARBEITUNG", "ABGESCHLOSSEN");
    }
}
