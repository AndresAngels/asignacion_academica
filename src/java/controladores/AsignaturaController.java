package controladores;

import controladores.util.JsfUtil;
import entidades.Asignatura;
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
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.AsignaturaJpaController;
import vista.Index;

@ManagedBean
@SessionScoped
public class AsignaturaController extends Controller implements Serializable {

    private static final String BUNDLE = "/Bundle";
    private static final String CREATE = "CREATE";
    private static final String UPDATE = "UPDATE";
    @ManagedProperty("#{index}")
    private Index index;
    private Asignatura primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private AsignaturaJpaController jpaController = null;
    private List<Asignatura> consultaTabla;
    private List<Asignatura> filtro;
    private Asignatura selected;

    public AsignaturaController() {
        setColumnTemplate("codigo nombre");
        createDynamicColumns();
    }

    public List<Asignatura> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT a FROM Asignatura a WHERE a.estado=:ESTADO ORDER BY a.codasignatura");
            query.setParameter("ESTADO", 1);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
        }
        return consultaTabla;
    }

    public void insertar() {
        selected.setEstado(1);
        create();
        getIndex().setIndex(0);
    }

    private AsignaturaJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new AsignaturaJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    public String update() {
        return createOrUpdate(UPDATE);
    }

    public String create() {
        return createOrUpdate(CREATE);
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findAsignatura(getPrimaryKey().getCodasignatura());
            getIndex().setIndex(1);
        } else {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.INFO, "Parametros nulos");
        }
    }

    public void desactivar() {
        try {
            if (primaryKey != null) {
                selected = getJpaController().findAsignatura(primaryKey.getCodasignatura());
                if (selected != null) {
                    selected.setEstado(2);
                    update();
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

    public String createOrUpdate(String opcion) {
        try {
            if (opcion == CREATE) {
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("AsignaturaCreated"));
            } else {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("AsignaturaUpdated"));
            }
            selected = new Asignatura();
            return "Create";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            return null;
        }
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
    public Asignatura getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Asignatura primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(AsignaturaJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Asignatura getSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Asignatura selected) {
        this.selected = selected;
    }

    /**
     * @return the filtro
     */
    public List<Asignatura> getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(List<Asignatura> filtro) {
        this.filtro = filtro;
    }

    @FacesConverter(forClass = Asignatura.class)
    public static class UsuariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AsignaturaController controller = (AsignaturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "asignaturaController");
            return controller.getJpaController().findAsignatura(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Asignatura) {
                Asignatura o = (Asignatura) object;
                return o.getCodasignatura();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Asignatura.class.getName());
            }
        }

    }
}
