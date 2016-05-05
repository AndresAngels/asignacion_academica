package controladores;

import controladores.util.JsfUtil;
import entidades.Perfiles;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.UsuariosJpaController;
import vista.Index;

@ManagedBean(name = "usuarioController")
@SessionScoped
public class UsuarioController extends Controller implements Serializable {

    private static final String BUNDLE = "/Bundle";
    @ManagedProperty("#{perfilesController}")
    private PerfilesController perfilesController;
    @ManagedProperty("#{index}")
    private Index index;
    private Usuarios primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private UsuariosJpaController jpaController = null;
    private List<Usuarios> consultaTabla;
    private List<Usuarios> filtro;
    private List<Perfiles> perfiles;
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
            Logger.getLogger(UsuarioController.class.getName()).log(Level.WARNING, npe.getMessage());
        }
        return consultaTabla;
    }

    public void insertar(ActionEvent ae) {
        selected.setUPassword(selected.getULogin());
        selected.setUActivo((short) 1);
        selected.setCodigoPerfil(getPerfilesController().getSelected());
        create();
        getIndex().setIndex(0);
    }

    private UsuariosJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new UsuariosJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    public String update() {
        try {
            getJpaController().edit(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosUpdated"));
            getIndex().setIndex(0);
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String create() {
        try {
            getJpaController().create(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("UsuariosCreated"));
            selected = new Usuarios();
            return "Create";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findUsuarios(getPrimaryKey().getULogin());
            getIndex().setIndex(1);
        } else {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.INFO, "Parametros nulos");
        }
    }

    public void desactivar() {
        try {
            if (primaryKey != null) {
                selected = getJpaController().findUsuarios(primaryKey.getULogin());
                if (selected != null) {
                    selected.setUActivo((short) 2);
                    update();
                    if (getConsultaTabla().isEmpty()) {
                        getIndex().setIndex(1);
                    } else {
                        getIndex().setIndex(0);
                    }
                }
            } else {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, "Llave Primaria del Registro Vacia o Nula");
                JsfUtil.addErrorMessage("Llave Primaria del Registro Vacia o Nula");
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.WARNING, "Doble intento de eliminar");
        }
    }

    public void mantenerIndex() {
        getIndex().setIndex(1);
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
     * @return the index
     */
    public Index getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Index index) {
        this.index = index;
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
