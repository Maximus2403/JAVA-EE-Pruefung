package iu.piisj.kundenverwaltung.auth;

import iu.piisj.kundenverwaltung.user.User;
import iu.piisj.kundenverwaltung.user.UserRole;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Optional;

@Named
@SessionScoped
public class AuthController implements Serializable {

    public static final String SESSION_USER_KEY = "sessionUser";

    @Inject
    private AuthService authService;

    private User currentUser;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Wird von LoginController aufgerufen
    public boolean login(String username, String plainPassword) {
        Optional<User> result = authService.authenticate(username, plainPassword);
        if (result.isEmpty()) {
            return false;
        }
        currentUser = result.get();
        // SessionUser für den AuthFilter in der HTTP-Session speichern
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute(SESSION_USER_KEY,
                new SessionUser(currentUser.getId(), currentUser.getUsername(), currentUser.getRole()));
        return true;
    }

    public String logout() {
        currentUser = null;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) session.invalidate();
        return "index?faces-redirect=true";
    }

    // Rollenchecks — von der View per #{authController.admin} etc. nutzbar
    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() == UserRole.ADMIN;
    }

    public boolean isSachbearbeiter() {
        return isLoggedIn() && (currentUser.getRole() == UserRole.SACHBEARBEITER
                             || currentUser.getRole() == UserRole.ADMIN);
    }

    public boolean isReadonly() {
        return isLoggedIn() && currentUser.getRole() == UserRole.READONLY;
    }

    // Darf Kunden anlegen / bearbeiten / löschen / CSV exportieren?
    public boolean isCanEdit() {
        return isSachbearbeiter();
    }

    // Alias für EL-Ausdrücke in XHTML (#{authController.canEdit})
    public boolean canEdit() {
        return isSachbearbeiter();
    }
}
