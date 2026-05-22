package iu.piisj.kundenverwaltung.customer;

import iu.piisj.kundenverwaltung.project.Project;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fachlicher Schlüssel (z.B. "KD-0001"), zusätzlich zur technischen id
    @Column(name = "customer_number", nullable = false, unique = true, length = 20)
    private String customerNumber;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 128)
    private String company;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    // Ein Kunde kann mehrere Projekte haben (COULD-Anforderung C-2)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    protected Customer() {
        // Benötigt von JPA
    }

    public Customer(String customerNumber, String firstName, String lastName,
                    String email, String phone, String company) {
        this.customerNumber = customerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.createdAt = LocalDate.now();
    }

    // Hilfsmethode für die View (z.B. in p:dataTable als Spalte nutzbar)
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getId() { return id; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public LocalDate getCreatedAt() { return createdAt; }

    public List<Project> getProjects() { return projects; }
}
