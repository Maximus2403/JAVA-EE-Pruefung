package kundenverwaltung.repository;

import kundenverwaltung.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@ApplicationScoped
public class UserRepository {

    private final EntityManagerFactory emf = createEntityManagerFactory("kundenverwaltungPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                     .setParameter("username", username)
                     .getResultStream()
                     .findFirst();
        } finally {
            em.close();
        }
    }

    public boolean existsByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                           .setParameter("username", username)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public Optional<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                     .setParameter("email", email)
                     .getResultStream()
                     .findFirst();
        } finally {
            em.close();
        }
    }

    public boolean existsByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                           .setParameter("email", email)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u ORDER BY u.username", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
