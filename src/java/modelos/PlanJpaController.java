package modelos;

import entidades.Horario;
import entidades.Plan;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelos.exceptions.IllegalOrphanException;
import modelos.exceptions.NonexistentEntityException;
import modelos.exceptions.PreexistingEntityException;

/**
 *
 * @author Aaron
 */
public class PlanJpaController implements Serializable {

    public PlanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Plan plan) throws PreexistingEntityException, Exception {
        if (plan.getHorarioList() == null) {
            plan.setHorarioList(new ArrayList<Horario>());
        }
        if (plan.getUsuariosList() == null) {
            plan.setUsuariosList(new ArrayList<Usuarios>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : plan.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getIdHorario());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            plan.setHorarioList(attachedHorarioList);
            List<Usuarios> attachedUsuariosList = new ArrayList<Usuarios>();
            for (Usuarios usuariosListUsuariosToAttach : plan.getUsuariosList()) {
                usuariosListUsuariosToAttach = em.getReference(usuariosListUsuariosToAttach.getClass(), usuariosListUsuariosToAttach.getULogin());
                attachedUsuariosList.add(usuariosListUsuariosToAttach);
            }
            plan.setUsuariosList(attachedUsuariosList);
            em.persist(plan);
            for (Horario horarioListHorario : plan.getHorarioList()) {
                Plan oldIdPlanOfHorarioListHorario = horarioListHorario.getIdPlan();
                horarioListHorario.setIdPlan(plan);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldIdPlanOfHorarioListHorario != null) {
                    oldIdPlanOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldIdPlanOfHorarioListHorario = em.merge(oldIdPlanOfHorarioListHorario);
                }
            }
            for (Usuarios usuariosListUsuarios : plan.getUsuariosList()) {
                Plan oldIdPlanOfUsuariosListUsuarios = usuariosListUsuarios.getIdPlan();
                usuariosListUsuarios.setIdPlan(plan);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
                if (oldIdPlanOfUsuariosListUsuarios != null) {
                    oldIdPlanOfUsuariosListUsuarios.getUsuariosList().remove(usuariosListUsuarios);
                    oldIdPlanOfUsuariosListUsuarios = em.merge(oldIdPlanOfUsuariosListUsuarios);
                }
            }
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

    public void edit(Plan plan) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Plan persistentPlan = em.find(Plan.class, plan.getIdPlan());
            List<Horario> horarioListOld = persistentPlan.getHorarioList();
            List<Horario> horarioListNew = plan.getHorarioList();
            List<Usuarios> usuariosListOld = persistentPlan.getUsuariosList();
            List<Usuarios> usuariosListNew = plan.getUsuariosList();
            List<String> illegalOrphanMessages = null;
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its idPlan field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getIdHorario());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            plan.setHorarioList(horarioListNew);
            List<Usuarios> attachedUsuariosListNew = new ArrayList<Usuarios>();
            for (Usuarios usuariosListNewUsuariosToAttach : usuariosListNew) {
                usuariosListNewUsuariosToAttach = em.getReference(usuariosListNewUsuariosToAttach.getClass(), usuariosListNewUsuariosToAttach.getULogin());
                attachedUsuariosListNew.add(usuariosListNewUsuariosToAttach);
            }
            usuariosListNew = attachedUsuariosListNew;
            plan.setUsuariosList(usuariosListNew);
            plan = em.merge(plan);
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Plan oldIdPlanOfHorarioListNewHorario = horarioListNewHorario.getIdPlan();
                    horarioListNewHorario.setIdPlan(plan);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldIdPlanOfHorarioListNewHorario != null && !oldIdPlanOfHorarioListNewHorario.equals(plan)) {
                        oldIdPlanOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldIdPlanOfHorarioListNewHorario = em.merge(oldIdPlanOfHorarioListNewHorario);
                    }
                }
            }
            for (Usuarios usuariosListOldUsuarios : usuariosListOld) {
                if (!usuariosListNew.contains(usuariosListOldUsuarios)) {
                    usuariosListOldUsuarios.setIdPlan(null);
                    usuariosListOldUsuarios = em.merge(usuariosListOldUsuarios);
                }
            }
            for (Usuarios usuariosListNewUsuarios : usuariosListNew) {
                if (!usuariosListOld.contains(usuariosListNewUsuarios)) {
                    Plan oldIdPlanOfUsuariosListNewUsuarios = usuariosListNewUsuarios.getIdPlan();
                    usuariosListNewUsuarios.setIdPlan(plan);
                    usuariosListNewUsuarios = em.merge(usuariosListNewUsuarios);
                    if (oldIdPlanOfUsuariosListNewUsuarios != null && !oldIdPlanOfUsuariosListNewUsuarios.equals(plan)) {
                        oldIdPlanOfUsuariosListNewUsuarios.getUsuariosList().remove(usuariosListNewUsuarios);
                        oldIdPlanOfUsuariosListNewUsuarios = em.merge(oldIdPlanOfUsuariosListNewUsuarios);
                    }
                }
            }
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

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Plan plan;
            try {
                plan = em.getReference(Plan.class, id);
                plan.getIdPlan();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The plan with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Horario> horarioListOrphanCheck = plan.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Plan (" + plan + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable idPlan field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Usuarios> usuariosList = plan.getUsuariosList();
            for (Usuarios usuariosListUsuarios : usuariosList) {
                usuariosListUsuarios.setIdPlan(null);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
            }
            em.remove(plan);
            em.getTransaction().commit();
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
