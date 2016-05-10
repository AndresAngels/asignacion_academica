package controladores;

import controladores.util.JsfUtil;
import entidades.Plan;
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

@ManagedBean(name = "usuarioController")
@SessionScoped
public class UsuarioController extends Controller implements Serializable {

    @ManagedProperty("#{perfilesController}")
    private PerfilesController perfilesController;
    @ManagedProperty("#{planController}")
    private PlanController planController;
    private Usuarios primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private UsuariosJpaController jpaController = null;
    private List<Usuarios> consultaTabla;
    private List<Usuarios> filtro;
    private Usuarios usuario;
    private Usuarios selected;
    private boolean reactivar = false;

    public UsuarioController() {
        usuario = new Usuarios();
        setColumnTemplate("login nombre apellido");
        createDynamicColumns();
    }

    public List<Usuarios> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.uActivo=:ESTADO ORDER BY u.uLogin");
            query.setParameter("ESTADO", 1);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
        }
        return consultaTabla;
    }

    public List<Usuarios> getConsultaDocentes() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.uActivo=:ESTADO AND (u.codigoPerfil.codigoPerfil=:DOCENTE OR u.codigoPerfil.codigoPerfil=:DOCENTEM) ORDER BY u.uLogin");
            query.setParameter("ESTADO", 1);
            query.setParameter("DOCENTE", "4");
            query.setParameter("DOCENTEM", "5");
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
        }
        return new ArrayList<Usuarios>();
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
        try {
            if (opcion == null ? CREATE == null : opcion.equals(CREATE)) {
                selected.setUPassword(selected.getULogin());
                selected.setCodigoPerfil(getPerfilesController().getSelected());
                if ("3".equals(selected.getCodigoPerfil().getCodigoPerfil())) {
                    selected.setIdPlan(getPlanController().getSelected());
                }
                selected.setUActivo((short) 1);
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosCreated"));
            } else {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosUpdated"));
            }
            selected = new Usuarios();
            getPlanController().setSelected(new Plan());
        } catch (PreexistingEntityException | NonexistentEntityException e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findUsuarios(getPrimaryKey().getULogin());
        } else {
            JsfUtil.addErrorMessage("Error al generar las consultas");
        }
    }

    public void desactivar() {
        if (primaryKey != null) {
            selected = getJpaController().findUsuarios(primaryKey.getULogin());
            if (selected != null) {
                selected.setUActivo((short) 2);
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
     * @return the planController
     */
    public PlanController getPlanController() {
        return planController;
    }

    /**
     * @param planController the planController to set
     */
    public void setPlanController(PlanController planController) {
        this.planController = planController;
    }

    @FacesConverter(forClass = Usuarios.class)
    public static class UsuariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
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
                return o.getULogin();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuarios.class.getName());
            }
        }

    }
}
