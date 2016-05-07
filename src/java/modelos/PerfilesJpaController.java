package modelos;

import entidades.Perfiles;
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
public class PerfilesJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public PerfilesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Perfiles perfiles) throws PreexistingEntityException {
        if (perfiles.getUsuariosList() == null) {
            perfiles.setUsuariosList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuarios> attachedUsuariosList = new ArrayList<>();
            for (Usuarios usuariosListUsuariosToAttach : perfiles.getUsuariosList()) {
                usuariosListUsuariosToAttach = em.getReference(usuariosListUsuariosToAttach.getClass(), usuariosListUsuariosToAttach.getULogin());
                attachedUsuariosList.add(usuariosListUsuariosToAttach);
            }
            perfiles.setUsuariosList(attachedUsuariosList);
            em.persist(perfiles);
            for (Usuarios usuariosListUsuarios : perfiles.getUsuariosList()) {
                Perfiles oldCodigoPerfilOfUsuariosListUsuarios = usuariosListUsuarios.getCodigoPerfil();
                usuariosListUsuarios.setCodigoPerfil(perfiles);
                usuariosListUsuarios = em.merge(usuariosListUsuarios);
                if (oldCodigoPerfilOfUsuariosListUsuarios != null) {
                    oldCodigoPerfilOfUsuariosListUsuarios.getUsuariosList().remove(usuariosListUsuarios);
                    oldCodigoPerfilOfUsuariosListUsuarios = em.merge(oldCodigoPerfilOfUsuariosListUsuarios);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPerfiles(perfiles.getCodigoPerfil()) != null) {
                throw new PreexistingEntityException("Perfiles " + perfiles + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Perfiles perfiles) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Perfiles persistentPerfiles = em.find(Perfiles.class, perfiles.getCodigoPerfil());
            List<Usuarios> usuariosListOld = persistentPerfiles.getUsuariosList();
            List<Usuarios> usuariosListNew = perfiles.getUsuariosList();
            List<Usuarios> attachedUsuariosListNew = new ArrayList<>();
            for (Usuarios usuariosListNewUsuariosToAttach : usuariosListNew) {
                usuariosListNewUsuariosToAttach = em.getReference(usuariosListNewUsuariosToAttach.getClass(), usuariosListNewUsuariosToAttach.getULogin());
                attachedUsuariosListNew.add(usuariosListNewUsuariosToAttach);
            }
            usuariosListNew = attachedUsuariosListNew;
            perfiles.setUsuariosList(usuariosListNew);
            perfiles = em.merge(perfiles);
            for (Usuarios usuariosListNewUsuarios : usuariosListNew) {
                if (!usuariosListOld.contains(usuariosListNewUsuarios)) {
                    Perfiles oldCodigoPerfilOfUsuariosListNewUsuarios = usuariosListNewUsuarios.getCodigoPerfil();
                    usuariosListNewUsuarios.setCodigoPerfil(perfiles);
                    usuariosListNewUsuarios = em.merge(usuariosListNewUsuarios);
                    if (oldCodigoPerfilOfUsuariosListNewUsuarios != null && !oldCodigoPerfilOfUsuariosListNewUsuarios.equals(perfiles)) {
                        oldCodigoPerfilOfUsuariosListNewUsuarios.getUsuariosList().remove(usuariosListNewUsuarios);
                        oldCodigoPerfilOfUsuariosListNewUsuarios = em.merge(oldCodigoPerfilOfUsuariosListNewUsuarios);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = perfiles.getCodigoPerfil();
                if (findPerfiles(id) == null) {
                    throw new NonexistentEntityException("The perfiles with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Perfiles> findPerfilesEntities() {
        return findPerfilesEntities(true, -1, -1);
    }

    public List<Perfiles> findPerfilesEntities(int maxResults, int firstResult) {
        return findPerfilesEntities(false, maxResults, firstResult);
    }

    private List<Perfiles> findPerfilesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Perfiles.class));
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

    public Perfiles findPerfiles(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Perfiles.class, id);
        } finally {
            em.close();
        }
    }

    public int getPerfilesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Perfiles> rt = cq.from(Perfiles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
