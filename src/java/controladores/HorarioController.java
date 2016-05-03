package controladores;

import controladores.util.JsfUtil;
import entidades.Horario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
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
import modelos.HorarioJpaController;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import vista.Event;
import vista.Index;
import vista.Sesion;

@ManagedBean(name = "horarioController")
@SessionScoped
public class HorarioController implements Serializable {

    @ManagedProperty("#{sesion}")
    private Sesion sesion;
    @ManagedProperty("#{index}")
    private Index index;
    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    @ManagedProperty("#{planController}")
    private PlanController planController;
    @ManagedProperty("#{asignaturaController}")
    private AsignaturaController asignaturaController;
    private Horario primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private HorarioJpaController jpaController = null;
    private List<Horario> consultaTabla;
    private List<Horario> filtro;
    private Horario selected;
    private Date inicio;
    private Date fin;
    private ScheduleModel eventModel = new DefaultScheduleModel();
    private Event event = new Event();

    public HorarioController() {
        setColumnTemplate("plan cohorte grupo asignatura docente");
        createDynamicColumns();
    }

    public List<Horario> getConsultaHorarioPrograma() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.idPlan:PLAN");
            query.setParameter("PLAN", getPlanController().selected);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
        }
        return consultaTabla;
    }

    public List<Horario> getConsultaAsignaturaDocente() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.uLogin=:DOCENTE");
            query.setParameter("DOCENTE", getUsuarioController().getSelected());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
        }
        return consultaTabla;
    }

    public List<Horario> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.estado=:ESTADO ORDER BY h.uLogin.uLogin");
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

    public void addEvent() {
        Calendar c = Calendar.getInstance();
        event.setTitle(getAsignaturaController().getSelected().getNombre()
                + " - " + getUsuarioController().getSelected().getNombreLogin());
        if (event.getId() == null) {
            getEventModel().addEvent(event);
        } else {
            getEventModel().updateEvent(event);
        }

        event = new Event();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (Event) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new Event("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        FacesContext.getCurrentInstance().addMessage("Horario Registrado", message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        FacesContext.getCurrentInstance().addMessage("Horario ", message);
    }

    private HorarioJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new HorarioJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    public String update(ActionEvent ae) {
        try {
            getJpaController().edit(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("HorarioUpdated"));
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("HorarioCreated"));
            selected = new Horario();
            return "Create";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findHorario(getPrimaryKey().getIdHorario());
            getIndex().setIndex(1);
        } else {
            Logger.getLogger(HorarioController.class.getName()).log(Level.INFO, "Parametros nulos");
        }
    }

    public void desactivar(ActionEvent actionEvent) {
        try {
            if (primaryKey != null) {
                selected = getJpaController().findHorario(primaryKey.getIdHorario());
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
    public Horario getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Horario primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(HorarioJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Horario getSelected() {
        if (selected == null) {
            selected = new Horario();
        }
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Horario selected) {
        this.selected = selected;
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
     * @return the filtro
     */
    public List<Horario> getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(List<Horario> filtro) {
        this.filtro = filtro;
    }

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public PlanController getPlanController() {
        return planController;
    }

    public void setPlanController(PlanController planController) {
        this.planController = planController;
    }

    /**
     * @return the eventModel
     */
    public ScheduleModel getEventModel() {
        return eventModel;
    }

    /**
     * @param eventModel the eventModel to set
     */
    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the asignaturaController
     */
    public AsignaturaController getAsignaturaController() {
        return asignaturaController;
    }

    /**
     * @param asignaturaController the asignaturaController to set
     */
    public void setAsignaturaController(AsignaturaController asignaturaController) {
        this.asignaturaController = asignaturaController;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    @FacesConverter(forClass = Horario.class)
    public static class HorarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HorarioController controller = (HorarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "horarioController");
            return controller.getJpaController().findHorario(Integer.parseInt(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Horario) {
                Horario o = (Horario) object;
                return "" + o.getIdHorario();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Horario.class.getName());
            }
        }

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
