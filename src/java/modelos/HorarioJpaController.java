package modelos;

import entidades.Asignatura;
import entidades.Horario;
import entidades.Plan;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelos.exceptions.NonexistentEntityException;

/**
 *
 * @author Aaron
 */
public class HorarioJpaController implements Serializable {

    public HorarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Horario horario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Asignatura codasignatura = horario.getCodasignatura();
            if (codasignatura != null) {
                codasignatura = em.getReference(codasignatura.getClass(), codasignatura.getCodasignatura());
                horario.setCodasignatura(codasignatura);
            }
            Plan idPlan = horario.getIdPlan();
            if (idPlan != null) {
                idPlan = em.getReference(idPlan.getClass(), idPlan.getIdPlan());
                horario.setIdPlan(idPlan);
            }
            Usuarios ULogin = horario.getULogin();
            if (ULogin != null) {
                ULogin = em.getReference(ULogin.getClass(), ULogin.getULogin());
                horario.setULogin(ULogin);
            }
            em.persist(horario);
            if (codasignatura != null) {
                codasignatura.getHorarioList().add(horario);
                codasignatura = em.merge(codasignatura);
            }
            if (idPlan != null) {
                idPlan.getHorarioList().add(horario);
                idPlan = em.merge(idPlan);
            }
            if (ULogin != null) {
                ULogin.getHorarioList().add(horario);
                ULogin = em.merge(ULogin);
            }
            em.getTransaction().commit();
            em.flush();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Horario horario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario persistentHorario = em.find(Horario.class, horario.getIdHorario());
            Asignatura codasignaturaOld = persistentHorario.getCodasignatura();
            Asignatura codasignaturaNew = horario.getCodasignatura();
            Plan idPlanOld = persistentHorario.getIdPlan();
            Plan idPlanNew = horario.getIdPlan();
            Usuarios ULoginOld = persistentHorario.getULogin();
            Usuarios ULoginNew = horario.getULogin();
            if (codasignaturaNew != null) {
                codasignaturaNew = em.getReference(codasignaturaNew.getClass(), codasignaturaNew.getCodasignatura());
                horario.setCodasignatura(codasignaturaNew);
            }
            if (idPlanNew != null) {
                idPlanNew = em.getReference(idPlanNew.getClass(), idPlanNew.getIdPlan());
                horario.setIdPlan(idPlanNew);
            }
            if (ULoginNew != null) {
                ULoginNew = em.getReference(ULoginNew.getClass(), ULoginNew.getULogin());
                horario.setULogin(ULoginNew);
            }
            horario = em.merge(horario);
            if (codasignaturaOld != null && !codasignaturaOld.equals(codasignaturaNew)) {
                codasignaturaOld.getHorarioList().remove(horario);
                codasignaturaOld = em.merge(codasignaturaOld);
            }
            if (codasignaturaNew != null && !codasignaturaNew.equals(codasignaturaOld)) {
                codasignaturaNew.getHorarioList().add(horario);
                codasignaturaNew = em.merge(codasignaturaNew);
            }
            if (idPlanOld != null && !idPlanOld.equals(idPlanNew)) {
                idPlanOld.getHorarioList().remove(horario);
                idPlanOld = em.merge(idPlanOld);
            }
            if (idPlanNew != null && !idPlanNew.equals(idPlanOld)) {
                idPlanNew.getHorarioList().add(horario);
                idPlanNew = em.merge(idPlanNew);
            }
            if (ULoginOld != null && !ULoginOld.equals(ULoginNew)) {
                ULoginOld.getHorarioList().remove(horario);
                ULoginOld = em.merge(ULoginOld);
            }
            if (ULoginNew != null && !ULoginNew.equals(ULoginOld)) {
                ULoginNew.getHorarioList().add(horario);
                ULoginNew = em.merge(ULoginNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = horario.getIdHorario();
                if (findHorario(id) == null) {
                    throw new NonexistentEntityException("The horario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario horario;
            try {
                horario = em.getReference(Horario.class, id);
                horario.getIdHorario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The horario with id " + id + " no longer exists.", enfe);
            }
            Asignatura codasignatura = horario.getCodasignatura();
            if (codasignatura != null) {
                codasignatura.getHorarioList().remove(horario);
                codasignatura = em.merge(codasignatura);
            }
            Plan idPlan = horario.getIdPlan();
            if (idPlan != null) {
                idPlan.getHorarioList().remove(horario);
                idPlan = em.merge(idPlan);
            }
            Usuarios ULogin = horario.getULogin();
            if (ULogin != null) {
                ULogin.getHorarioList().remove(horario);
                ULogin = em.merge(ULogin);
            }
            em.remove(horario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Horario> findHorarioEntities() {
        return findHorarioEntities(true, -1, -1);
    }

    public List<Horario> findHorarioEntities(int maxResults, int firstResult) {
        return findHorarioEntities(false, maxResults, firstResult);
    }

    private List<Horario> findHorarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Horario.class));
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

    public Horario findHorario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Horario.class, id);
        } finally {
            em.close();
        }
    }

    public int getHorarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Horario> rt = cq.from(Horario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
