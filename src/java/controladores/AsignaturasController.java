package controladores;

import controladores.util.JsfUtil;
import entidades.Asignaturas;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.AsignaturasJpaController;
import modelos.exceptions.NonexistentEntityException;

@ManagedBean(name = "asignaturasController")
@SessionScoped
public class AsignaturasController extends Controller implements Serializable {

    private Asignaturas primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private AsignaturasJpaController jpaController = null;
    private List<Asignaturas> consultaTabla;
    private List<Asignaturas> filtro;
    private Asignaturas selected;

    public AsignaturasController() {
        setColumnTemplate("codigo nombre");
        createDynamicColumns();
    }

    public List<Asignaturas> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT a FROM Asignaturas a WHERE a.idEstado=:ESTADO ORDER BY a.codigoAsignatura");
            query.setParameter("ESTADO", ACTIVO);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    private AsignaturasJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new AsignaturasJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
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
                selected.setIdEstado(ACTIVO);
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("AsignaturasCreated"));
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("AsignaturasError"));
            }
        } else {
            try {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("AsignaturasUpdated"));
            } catch (NonexistentEntityException e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
            }
        }
        selected = new Asignaturas();
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findAsignaturas(getPrimaryKey().getCodigoAsignatura());
        }
    }

    public void desactivar() {
        if (primaryKey != null) {
            selected = getJpaController().findAsignaturas(primaryKey.getCodigoAsignatura());
            if (selected != null) {
                selected.setIdEstado(DESACTIVADO);
                update();
            }
        }
    }

    /**
     * @return the primaryKey
     */
    public Asignaturas getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Asignaturas primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(AsignaturasJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Asignaturas getSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Asignaturas selected) {
        this.selected = selected;
    }

    /**
     * @return the filtro
     */
    public List<Asignaturas> getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(List<Asignaturas> filtro) {
        this.filtro = filtro;
    }

    @FacesConverter(forClass = Asignaturas.class)
    public static class UsuariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AsignaturasController controller = (AsignaturasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "asignaturasController");
            return controller.getJpaController().findAsignaturas(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Asignaturas) {
                Asignaturas o = (Asignaturas) object;
                return o.getCodigoAsignatura();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Asignaturas.class.getName());
            }
        }

    }
}
