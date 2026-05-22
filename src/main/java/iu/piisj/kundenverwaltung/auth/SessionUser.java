package iu.piisj.kundenverwaltung.auth;

import iu.piisj.kundenverwaltung.user.UserRole;

import java.io.Serializable;

/**
 * Leichtgewichtiges Objekt, das in der HTTP-Session gespeichert wird.
 * Enthält nur die nötigsten Infos — keine vollständige JPA-Entität in der Session.
 */
public class SessionUser implements Serializable {

    private final Long id;
    private final String username;
    private final UserRole role;

    public SessionUser(Long id, String username, UserRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
}
