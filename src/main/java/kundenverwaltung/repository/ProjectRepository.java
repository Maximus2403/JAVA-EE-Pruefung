package kundenverwaltung.repository;

import kundenverwaltung.project.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Comparator;
import java.util.List;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

/**
 * Kapselt alle Datenbankzugriffe für Project-Entitäten.
 * Analoges Muster zu CustomerRepository.
 */
@ApplicationScoped
public class ProjectRepository {

    private final EntityManagerFactory emf = createEntityManagerFactory("kundenverwaltungPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Alle Wurzel-Projekte eines Kunden (parentProject = null)
    public List<Project> findRootsByCustomer(Long customerId) {
        EntityManager em = getEntityManager();
        try {
            // Erst alle Projekte des Kunden mit subProjects laden
            List<Project> all = em.createQuery(
                    "SELECT DISTINCT p FROM Project p " +
                    "LEFT JOIN FETCH p.subProjects " +
                    "WHERE p.customer.id = :customerId",
                    Project.class)
                .setParameter("customerId", customerId)
                .getResultList();

            // Nur Wurzelprojekte zurückgeben — subProjects sind bereits initialisiert
            return all.stream()
                    .filter(p -> p.getParentProject() == null)
                    .sorted(Comparator.comparing(Project::getName))
                    .toList();
        } finally {
            em.close();
        }
    }

    // Alle Projekte eines Kunden (für Übersichtstabelle)
    public List<Project> findAllByCustomer(Long customerId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Project p " +
                    "LEFT JOIN FETCH p.parentProject " +
                    "WHERE p.customer.id = :customerId " +
                    "ORDER BY p.name",
                    Project.class)
                .setParameter("customerId", customerId)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public Project findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public void save(Project project) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (project.getId() == null) {
                em.persist(project);
            } else {
                em.merge(project);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Project project = em.find(Project.class, id);
            if (project != null) {
                em.remove(project);
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
