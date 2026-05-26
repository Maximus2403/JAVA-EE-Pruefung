package kundenverwaltung.dto;

/**
 * Data Transfer Object für das Kundenformular in der View.
 * Entkoppelt das JSF-Formular von der JPA-Entität (analog zu EventDTO im Referenzprojekt).
 */
public class CustomerDTO {

    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;

    public CustomerDTO() {}

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
}
