package kundenverwaltung.user;

import kundenverwaltung.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Controller für die Admin-Seite zur Benutzerverwaltung.
 * Nur für ADMIN erreichbar — AuthFilter schützt /admin/*.
 */
@Named
@ViewScoped
public class UserAdminController implements Serializable {

    @Inject
    private UserRepository userRepository;

    private List<User> users;

    @PostConstruct
    public void init() {
        users = userRepository.findAll();
    }

    // Rollenänderung speichern (Inline-Dropdown in der Tabelle)
    public void updateRole(User user) {
        userRepository.save(user);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Gespeichert",
                        "Rolle von " + user.getUsername() + " wurde auf " + user.getRole() + " gesetzt."));
    }

    public List<User> getUsers() { return users; }
}
