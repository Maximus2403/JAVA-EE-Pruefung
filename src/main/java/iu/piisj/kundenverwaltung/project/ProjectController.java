package iu.piisj.kundenverwaltung.project;

import iu.piisj.kundenverwaltung.auth.AuthController;
import iu.piisj.kundenverwaltung.customer.Customer;
import iu.piisj.kundenverwaltung.repository.CustomerRepository;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * SessionScoped: speichert welcher Kunde gerade ausgewählt ist.
 * Wird von customers.xhtml aus aufgerufen — kein Seitenwechsel nötig.
 */
@Named
@SessionScoped
public class ProjectController implements Serializable {

    @Inject private ProjectService     projectService;
    @Inject private CustomerRepository customerRepository;
    @Inject private AuthController     authController;

    private Long          selectedCustomerId;
    private Customer      selectedCustomer;
    private List<Project> rootProjects;
    private List<Project> allProjects;

    private String newName;
    private String newDescription;
    private String newStatus   = "OFFEN";
    private Long   newParentId;

    // Aufgerufen wenn Nutzer auf "Projekte" eines Kunden klickt
    public void selectCustomer(Long customerId) {
        this.selectedCustomerId = customerId;
        this.selectedCustomer   = customerRepository.findById(customerId);
        reload();
        resetForm();
    }

    // Panel schließen
    public void deselectCustomer() {
        this.selectedCustomerId = null;
        this.selectedCustomer   = null;
        this.rootProjects       = null;
        this.allProjects        = null;
    }

    // Toggle: aufklappen wenn neu, zuklappen wenn schon ausgewählt
    public void toggleCustomer(Long customerId) {
        if (isSelected(customerId)) {
            deselectCustomer();
        } else {
            selectCustomer(customerId);
        }
    }
    private void reload() {
        if (selectedCustomerId == null) return;
        rootProjects = projectService.getRootProjects(selectedCustomerId);
        allProjects  = projectService.getAllProjectsForCustomer(selectedCustomerId);
    }

    public void saveProject() {
        if (selectedCustomerId == null || !authController.isCanEdit()) return;
        if (newParentId == null) {
            projectService.createRootProject(newName, newDescription, newStatus, selectedCustomerId);
        } else {
            projectService.createSubProject(newName, newDescription, newStatus,
                                            selectedCustomerId, newParentId);
        }
        resetForm();
        reload();
        addMessage(FacesMessage.SEVERITY_INFO, "Gespeichert", "Projekt wurde angelegt.");
    }

    public void deleteProject(Project project) {
        if (!authController.isCanEdit()) return;
        projectService.deleteProject(project.getId());
        reload();
        addMessage(FacesMessage.SEVERITY_INFO, "Gelöscht", "Projekt gelöscht.");
    }

    public boolean isSelected(Long customerId) {
        return customerId != null && customerId.equals(selectedCustomerId);
    }

    private void resetForm() {
        newName = null; newDescription = null;
        newStatus = "OFFEN"; newParentId = null;
    }

    public void addMessage(FacesMessage.Severity sev, String sum, String det) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, sum, det));
    }

    public Customer      getSelectedCustomer()     { return selectedCustomer; }
    public List<Project> getRootProjects()         { return rootProjects; }
    public List<Project> getAllProjects()           { return allProjects; }
    public String        getNewName()              { return newName; }
    public void          setNewName(String n)      { this.newName = n; }
    public String        getNewDescription()       { return newDescription; }
    public void          setNewDescription(String d){ this.newDescription = d; }
    public String        getNewStatus()            { return newStatus; }
    public void          setNewStatus(String s)    { this.newStatus = s; }
    public Long          getNewParentId()          { return newParentId; }
    public void          setNewParentId(Long p)    { this.newParentId = p; }
    public List<String>  getAvailableStatuses()    { return projectService.getAvailableStatuses(); }
}
