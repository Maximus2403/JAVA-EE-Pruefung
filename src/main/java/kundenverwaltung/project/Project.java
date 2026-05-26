package kundenverwaltung.project;

import kundenverwaltung.customer.Customer;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 500)
    private String description;

    // z.B. "OFFEN", "IN_BEARBEITUNG", "ABGESCHLOSSEN"
    @Column(nullable = false, length = 30)
    private String status;

    // Zugehöriger Kunde (Pflicht)
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Self-Referencing: Oberprojekt (null = Wurzel-Projekt)
    @ManyToOne
    @JoinColumn(name = "parent_project_id")
    private Project parentProject;

    // Self-Referencing: Unterprojekte (COULD C-2: Projektbaum)
    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> subProjects = new ArrayList<>();

    protected Project() {
        // Benötigt von JPA
    }

    public Project(String name, String description, String status, Customer customer) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.customer = customer;
    }

    // Hilfsmethode: ist dieses Projekt ein Wurzel-Projekt?
    public boolean isRootProject() {
        return parentProject == null;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Project getParentProject() { return parentProject; }
    public void setParentProject(Project parentProject) { this.parentProject = parentProject; }

    public List<Project> getSubProjects() { return subProjects; }

    public String getStatusLower() {
        return status != null ? status.toLowerCase() : "";
    }
}
