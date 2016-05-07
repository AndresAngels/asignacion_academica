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
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelos.exceptions.NonexistentEntityException;
import modelos.exceptions.PreexistingEntityException;

/**
 *
 * @author Aaron
 */
public class UsuariosJpaController implements Serializable {

    private EntityManagerFactory emf = null;
    private Usuarios usuarios;

    public UsuariosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usua) throws PreexistingEntityException {
        this.usuarios = usua;
        if (usuarios.getHorarioList() == null) {
            usuarios.setHorarioList(new ArrayList<>());
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
            List<Horario> attachedHorarioList = new ArrayList<>();
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

    public void edit(Usuarios usua) throws NonexistentEntityException {
        this.usuarios = usua;
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getULogin());
            Perfiles codigoPerfilOld = persistentUsuarios.getCodigoPerfil();
            Perfiles codigoPerfilNew = usuarios.getCodigoPerfil();
            Plan idPlanOld = persistentUsuarios.getIdPlan();
            Plan idPlanNew = usuarios.getIdPlan();
            if (codigoPerfilNew != null) {
                codigoPerfilNew = em.getReference(codigoPerfilNew.getClass(), codigoPerfilNew.getCodigoPerfil());
                usuarios.setCodigoPerfil(codigoPerfilNew);
            }
            if (idPlanNew != null) {
                idPlanNew = em.getReference(idPlanNew.getClass(), idPlanNew.getIdPlan());
                usuarios.setIdPlan(idPlanNew);
            }
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
