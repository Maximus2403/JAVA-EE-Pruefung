package kundenverwaltung.repository;

import kundenverwaltung.customer.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

/**
 * Kapselt alle Datenbankzugriffe für Customer-Entitäten.
 * Best Practice: Alle DB-Operationen sind hier gebündelt (Separation of Concerns).
 */
@ApplicationScoped
public class CustomerRepository {

    private final EntityManagerFactory emf = createEntityManagerFactory("kundenverwaltungPU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Customer> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c ORDER BY c.lastName, c.firstName", Customer.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Customer findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    // Suche nach Name oder Kundennummer (MUST M-2)
    public List<Customer> search(String searchTerm) {
        EntityManager em = getEntityManager();
        try {
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return em.createQuery(
                    "SELECT c FROM Customer c WHERE " +
                    "LOWER(c.firstName) LIKE :term OR " +
                    "LOWER(c.lastName) LIKE :term OR " +
                    "LOWER(c.customerNumber) LIKE :term OR " +
                    "LOWER(c.company) LIKE :term " +
                    "ORDER BY c.lastName, c.firstName",
                    Customer.class)
                .setParameter("term", pattern)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Customer customer) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (customer.getId() == null) {
                em.persist(customer);
            } else {
                em.merge(customer);
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
            Customer customer = em.find(Customer.class, id);
            if (customer != null) {
                em.remove(customer);
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
