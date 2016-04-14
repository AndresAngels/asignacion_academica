package controladores;

import controladores.util.JsfUtil;
import entidades.Asignatura;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.AsignaturaJpaController;
import vista.Index;
import vista.Sesion;

@ManagedBean
@SessionScoped
public class AsignaturaController implements Serializable {

    @ManagedProperty("#{sesion}")
    private Sesion sesion;
    @ManagedProperty("#{index}")
    private Index index;
    private Asignatura primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private AsignaturaJpaController jpaController = null;
    private List<Asignatura> consultaTabla;
    private List<Asignatura> filtro;
    private Asignatura selected;

    public AsignaturaController() {
        setColumnTemplate("Codigo Nombre");
        createDynamicColumns();
    }

    public List<Asignatura> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT a FROM Asignatura a WHERE a.estado=:ESTADO ORDER BY a.codasignatura");
            query.setParameter("ESTADO", 1);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
        }
        return consultaTabla;
    }

    public void insertar(ActionEvent ae) {
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

    public String update(ActionEvent ae) {
        try {
            getJpaController().edit(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AsignaturaUpdated"));
            getIndex().setIndex(0);
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String create() {
        try {
            getJpaController().create(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AsignaturaCreated"));
            selected = new Asignatura();
            return "Create";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findAsignatura(getPrimaryKey().getCodasignatura());
            getIndex().setIndex(1);
        } else {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.INFO, "Parametros nulos");
        }
    }

    public void desactivar(ActionEvent actionEvent) {
        try {
            if (primaryKey != null) {
                selected = getJpaController().findAsignatura(primaryKey.getCodasignatura());
                if (selected != null) {
                    selected.setEstado(2);
                    update(actionEvent);
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
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
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

    //Implementando Tabla Dinamica
    public static class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

    private List<ColumnModel> columns = new ArrayList<ColumnModel>();
    private String columnTemplate;

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
    }

    public String getColumnTemplate() {
        return columnTemplate;
    }

    public void setColumnTemplate(String columnTemplate) {
        this.columnTemplate = columnTemplate;
    }

    public void createDynamicColumns() {
        String[] columnKeys = columnTemplate.split(" ");
        columns.clear();

        for (String columnKey : columnKeys) {
            String key = columnKey.trim();
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
            columns.add(new ColumnModel(key, columnKey));
        }
    }
}
