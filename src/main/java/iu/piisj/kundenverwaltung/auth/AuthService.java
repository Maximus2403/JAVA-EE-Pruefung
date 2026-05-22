package iu.piisj.kundenverwaltung.auth;

import iu.piisj.kundenverwaltung.repository.UserRepository;
import iu.piisj.kundenverwaltung.user.User;
import iu.piisj.kundenverwaltung.user.UserRole;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Geschäftslogik für Registrierung und Authentifizierung.
 * Passwort-Hashing via BCrypt (identisch zum Referenzprojekt).
 */
@RequestScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    public RegistrationResult register(String username, String email,
                                       String firstName, String lastName,
                                       String plainPassword) {

        if (userRepository.existsByUsername(username)) {
            return RegistrationResult.USERNAME_EXISTS;
        }
        if (userRepository.existsByEmail(email)) {
            return RegistrationResult.EMAIL_EXISTS;
        }

        String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        // Neue Benutzer erhalten standardmäßig READONLY (Sachbearbeiter-Rolle vergibt Admin)
        User user = new User(username, email, firstName, lastName, passwordHash, UserRole.READONLY);
        userRepository.save(user);
        return RegistrationResult.SUCCESS;
    }

    public Optional<User> authenticate(String username, String plainPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
