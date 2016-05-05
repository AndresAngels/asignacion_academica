package modelos;

import entidades.Horario;
import entidades.Perfiles;
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
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException {
        if (usuarios.getHorarioList() == null) {
            usuarios.setHorarioList(new ArrayList<Horario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Perfiles codigoPerfil = usuarios.getCodigoPerfil();
            if (codigoPerfil != null) {
                codigoPerfil = em.getReference(codigoPerfil.getClass(), codigoPerfil.getCodigoPerfil());
                usuarios.setCodigoPerfil(codigoPerfil);
            }
            Plan idPlan = usuarios.getIdPlan();
            if (idPlan != null) {
                idPlan = em.getReference(idPlan.getClass(), idPlan.getIdPlan());
                usuarios.setIdPlan(idPlan);
            }
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : usuarios.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getIdHorario());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            usuarios.setHorarioList(attachedHorarioList);
            em.persist(usuarios);
            if (codigoPerfil != null) {
                codigoPerfil.getUsuariosList().add(usuarios);
                codigoPerfil = em.merge(codigoPerfil);
            }
            if (idPlan != null) {
                idPlan.getUsuariosList().add(usuarios);
                idPlan = em.merge(idPlan);
            }
            for (Horario horarioListHorario : usuarios.getHorarioList()) {
                Usuarios oldULoginOfHorarioListHorario = horarioListHorario.getULogin();
                horarioListHorario.setULogin(usuarios);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldULoginOfHorarioListHorario != null) {
                    oldULoginOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldULoginOfHorarioListHorario = em.merge(oldULoginOfHorarioListHorario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getULogin()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getULogin());
            Perfiles codigoPerfilOld = persistentUsuarios.getCodigoPerfil();
            Perfiles codigoPerfilNew = usuarios.getCodigoPerfil();
            Plan idPlanOld = persistentUsuarios.getIdPlan();
            Plan idPlanNew = usuarios.getIdPlan();
            List<Horario> horarioListOld = persistentUsuarios.getHorarioList();
            List<Horario> horarioListNew = usuarios.getHorarioList();
            List<String> illegalOrphanMessages = null;
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its ULogin field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codigoPerfilNew != null) {
                codigoPerfilNew = em.getReference(codigoPerfilNew.getClass(), codigoPerfilNew.getCodigoPerfil());
                usuarios.setCodigoPerfil(codigoPerfilNew);
            }
            if (idPlanNew != null) {
                idPlanNew = em.getReference(idPlanNew.getClass(), idPlanNew.getIdPlan());
                usuarios.setIdPlan(idPlanNew);
            }
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getIdHorario());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            usuarios.setHorarioList(horarioListNew);
            usuarios = em.merge(usuarios);
            if (codigoPerfilOld != null && !codigoPerfilOld.equals(codigoPerfilNew)) {
                codigoPerfilOld.getUsuariosList().remove(usuarios);
                codigoPerfilOld = em.merge(codigoPerfilOld);
            }
            if (codigoPerfilNew != null && !codigoPerfilNew.equals(codigoPerfilOld)) {
                codigoPerfilNew.getUsuariosList().add(usuarios);
                codigoPerfilNew = em.merge(codigoPerfilNew);
            }
            if (idPlanOld != null && !idPlanOld.equals(idPlanNew)) {
                idPlanOld.getUsuariosList().remove(usuarios);
                idPlanOld = em.merge(idPlanOld);
            }
            if (idPlanNew != null && !idPlanNew.equals(idPlanOld)) {
                idPlanNew.getUsuariosList().add(usuarios);
                idPlanNew = em.merge(idPlanNew);
            }
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Usuarios oldULoginOfHorarioListNewHorario = horarioListNewHorario.getULogin();
                    horarioListNewHorario.setULogin(usuarios);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldULoginOfHorarioListNewHorario != null && !oldULoginOfHorarioListNewHorario.equals(usuarios)) {
                        oldULoginOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldULoginOfHorarioListNewHorario = em.merge(oldULoginOfHorarioListNewHorario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuarios.getULogin();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
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
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getULogin();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Horario> horarioListOrphanCheck = usuarios.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable ULogin field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Perfiles codigoPerfil = usuarios.getCodigoPerfil();
            if (codigoPerfil != null) {
                codigoPerfil.getUsuariosList().remove(usuarios);
                codigoPerfil = em.merge(codigoPerfil);
            }
            Plan idPlan = usuarios.getIdPlan();
            if (idPlan != null) {
                idPlan.getUsuariosList().remove(usuarios);
                idPlan = em.merge(idPlan);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
