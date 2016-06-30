package controladores;

import controladores.util.JsfUtil;
import entidades.Perfiles;
import entidades.Planes;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.UsuariosJpaController;
import modelos.exceptions.NonexistentEntityException;
import modelos.exceptions.PreexistingEntityException;

@ManagedBean(name = "usuariosController", eager = true)
@SessionScoped
public class UsuariosController extends Controller implements Serializable {

    @ManagedProperty("#{perfilesController}")
    private PerfilesController perfilesController;
    @ManagedProperty("#{planController}")
    private PlanesController planesController;
    private Usuarios primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private UsuariosJpaController jpaController = null;
    private List<Usuarios> consultaTabla;
    private List<Usuarios> filtro;
    private List<Perfiles> perfiles;
    private Usuarios usuario;
    private Usuarios selected;
    private boolean reactivar = false;

    public UsuariosController() {
        usuario = new Usuarios();
        setColumnTemplate("login nombre apellido");
        createDynamicColumns();
    }

    public List<Usuarios> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.idEstado=:ESTADO ORDER BY u.loginUsuario");
            query.setParameter("ESTADO", ACTIVO);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    public List<Usuarios> getConsultaDocentes() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.idEstado=:ESTADO AND (u.codigoPerfil.codigoPerfil=:DOCENTE OR u.codigoPerfil.codigoPerfil=:DOCENTEM) ORDER BY u.loginUsuario");
            query.setParameter("ESTADO", ACTIVO);
            query.setParameter("DOCENTE", "4");
            query.setParameter("DOCENTEM", "5");
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return new ArrayList<Usuarios>();
    }

    public List<Perfiles> getConsultaPerfiles() {
        try {
            Query query;
            if ("1".equals(usuario.getCodigoPerfil().getCodigoPerfil())) {
                query = getJpaController().getEntityManager().createQuery("SELECT p FROM Perfiles p ORDER BY p.descripcionPerfil");
                perfiles = query.getResultList();
            } else {
                query = getJpaController().getEntityManager().createQuery("SELECT p FROM Perfiles p WHERE p.codigoPerfil>:PERFIL ORDER BY p.descripcionPerfil");
                query.setParameter("PERFIL", "3");
                perfiles = query.getResultList();
            }
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
        }
        return perfiles;
    }

    private UsuariosJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new UsuariosJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    public void update() {
        createOrUpdate(UPDATE);
    }

    public void create() {
        createOrUpdate(CREATE);
    }

    public void createOrUpdate(String opcion) {
        if (opcion.equals(CREATE)) {
            try {
                selected.setPasswordUsuario(selected.getLoginUsuario());
                selected.setCodigoPerfil(getPerfilesController().getSelected());
                if ("3".equals(selected.getCodigoPerfil().getCodigoPerfil())) {
                    selected.setIdPlan(getPlanesController().getSelected());
                }
                selected.setIdEstado(ACTIVO);
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosCreated"));
            } catch (PreexistingEntityException e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("UsuarioError"));
            }
        } else {
            try {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosUpdated"));
            } catch (NonexistentEntityException e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            }
        }
        selected = new Usuarios();
        getPlanesController().setSelected(new Planes());
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findUsuarios(getPrimaryKey().getLoginUsuario());
            getPerfilesController().setSelected(selected.getCodigoPerfil());
            if ("3".equals(selected.getCodigoPerfil().getCodigoPerfil())) {
                getPlanesController().setSelected(selected.getIdPlan());
            }
        } else {
            JsfUtil.addErrorMessage("Error al generar las consultas");
        }
    }

    public void desactivar() {
        if (primaryKey != null) {
            selected = getJpaController().findUsuarios(primaryKey.getLoginUsuario());
            if (selected != null) {
                selected.setIdEstado(DESACTIVADO);
                update();
            }
        }
    }

    /**
     * @return the usuario
     */
    public Usuarios getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the selected
     */
    public Usuarios getSelected() {
        if (selected == null) {
            selected = new Usuarios();
        }
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Usuarios selected) {
        this.selected = selected;
    }

    /**
     * @return the primaryKey
     */
    public Usuarios getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Usuarios primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the filtro
     */
    public List<Usuarios> getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(List<Usuarios> filtro) {
        this.filtro = filtro;
    }

    /**
     * @return the reactivar
     */
    public boolean isReactivar() {
        return reactivar;
    }

    /**
     * @param reactivar the reactivar to set
     */
    public void setReactivar(boolean reactivar) {
        this.reactivar = reactivar;
    }

    /**
     * @return the perfilesController
     */
    public PerfilesController getPerfilesController() {
        return perfilesController;
    }

    /**
     * @param perfilesController the perfilesController to set
     */
    public void setPerfilesController(PerfilesController perfilesController) {
        this.perfilesController = perfilesController;
    }

    /**
     * @return the planesController
     */
    public PlanesController getPlanesController() {
        return planesController;
    }

    /**
     * @param planesController the planController to set
     */
    public void setPlanesController(PlanesController planesController) {
        this.planesController = planesController;
    }

    @FacesConverter(forClass = Usuarios.class)
    public static class UsuariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuariosController controller = (UsuariosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.getJpaController().findUsuarios(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuarios) {
                Usuarios o = (Usuarios) object;
                return o.getLoginUsuario();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuarios.class.getName());
            }
        }

    }
}
