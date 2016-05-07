package modelos;

import entidades.Asignatura;
import entidades.Horario;
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
public class AsignaturaJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public AsignaturaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Asignatura asignatura) throws PreexistingEntityException {
        if (asignatura.getHorarioList() == null) {
            asignatura.setHorarioList(new ArrayList<Horario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : asignatura.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getIdHorario());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            asignatura.setHorarioList(attachedHorarioList);
            em.persist(asignatura);
            for (Horario horarioListHorario : asignatura.getHorarioList()) {
                Asignatura oldCodasignaturaOfHorarioListHorario = horarioListHorario.getCodasignatura();
                horarioListHorario.setCodasignatura(asignatura);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldCodasignaturaOfHorarioListHorario != null) {
                    oldCodasignaturaOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldCodasignaturaOfHorarioListHorario = em.merge(oldCodasignaturaOfHorarioListHorario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAsignatura(asignatura.getCodasignatura()) != null) {
                throw new PreexistingEntityException("Asignatura " + asignatura + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Asignatura asignatura) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Asignatura persistentAsignatura = em.find(Asignatura.class, asignatura.getCodasignatura());
            List<Horario> horarioListOld = persistentAsignatura.getHorarioList();
            List<Horario> horarioListNew = asignatura.getHorarioList();
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getIdHorario());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            asignatura.setHorarioList(horarioListNew);
            asignatura = em.merge(asignatura);
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Asignatura oldCodasignaturaOfHorarioListNewHorario = horarioListNewHorario.getCodasignatura();
                    horarioListNewHorario.setCodasignatura(asignatura);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldCodasignaturaOfHorarioListNewHorario != null && !oldCodasignaturaOfHorarioListNewHorario.equals(asignatura)) {
                        oldCodasignaturaOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldCodasignaturaOfHorarioListNewHorario = em.merge(oldCodasignaturaOfHorarioListNewHorario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = asignatura.getCodasignatura();
                if (findAsignatura(id) == null) {
                    throw new NonexistentEntityException("The asignatura with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Asignatura> findAsignaturaEntities() {
        return findAsignaturaEntities(true, -1, -1);
    }

    public List<Asignatura> findAsignaturaEntities(int maxResults, int firstResult) {
        return findAsignaturaEntities(false, maxResults, firstResult);
    }

    private List<Asignatura> findAsignaturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Asignatura.class));
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

    public Asignatura findAsignatura(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Asignatura.class, id);
        } finally {
            em.close();
        }
    }

    public int getAsignaturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Asignatura> rt = cq.from(Asignatura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
