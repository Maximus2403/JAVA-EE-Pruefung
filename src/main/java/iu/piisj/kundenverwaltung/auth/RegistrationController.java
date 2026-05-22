package iu.piisj.kundenverwaltung.auth;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class RegistrationController implements Serializable {

    @Inject
    private AuthService authService;

    @Inject
    private AuthController authController;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;

    public String submitRegistration() {
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Registrierung fehlgeschlagen",
                            "Passwort und Bestätigung stimmen nicht überein."));
            return null;
        }

        RegistrationResult result = authService.register(username, email, firstName, lastName, password);

        switch (result) {
            case USERNAME_EXISTS -> {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Registrierung fehlgeschlagen", "Benutzername ist bereits vergeben."));
                return null;
            }
            case EMAIL_EXISTS -> {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Registrierung fehlgeschlagen", "E-Mail-Adresse ist bereits vergeben."));
                return null;
            }
            default -> {
                // SUCCESS:e" direkt einloggen und weiterleiten
                authController.login(username, password);
                return "customers?faces-redirect=true";
            }
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String f) { this.firstName = f; }

    public String getLastName() { return lastName; }
    public void setLastName(String l) { this.lastName = l; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String c) { this.confirmPassword = c; }
}
