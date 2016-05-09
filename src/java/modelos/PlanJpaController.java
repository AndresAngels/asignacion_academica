package modelos;

import entidades.Horario;
import entidades.Plan;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelos.exceptions.NonexistentEntityException;
import modelos.exceptions.PreexistingEntityException;

/**
 *
 * @author Aaron
 */
public class PlanJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public PlanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Plan plan) throws PreexistingEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(plan);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPlan(plan.getIdPlan()) != null) {
                throw new PreexistingEntityException("Plan " + plan + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Plan plan) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            plan = em.merge(plan);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = plan.getIdPlan();
                if (findPlan(id) == null) {
                    throw new NonexistentEntityException("The plan with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Plan> findPlanEntities() {
        return findPlanEntities(true, -1, -1);
    }

    public List<Plan> findPlanEntities(int maxResults, int firstResult) {
        return findPlanEntities(false, maxResults, firstResult);
    }

    private List<Plan> findPlanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Plan.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Plan findPlan(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Plan.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Plan> rt = cq.from(Plan.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
