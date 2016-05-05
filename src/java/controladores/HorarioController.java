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
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import vista.Index;
import vista.Sesion;

@ManagedBean
@SessionScoped
public class HorarioController implements Serializable {

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
    private Horario selected;
    private ScheduleModel eventModel = new DefaultScheduleModel();
    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    private List consultaTabla;

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
        Calendar c1 = Calendar.getInstance();
        int n = 0;
        switch (selected.getDia()) {
            case "Lunes":
                n = 0;
                break;
            case "Martes":
                n = 1;
                break;
            case "Miercoles":
                n = 2;
                break;
            case "Jueves":
                n = 3;
                break;
            case "Viernes":
                n = 4;
                break;
            case "Sabado":
                n = 5;
                break;
        }
        c1.setTime(event.getStartDate());
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.MONTH, Calendar.JANUARY);
        c1.set(Calendar.DAY_OF_MONTH, 4 + n);
        event.setStartDate(c1.getTime());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(event.getEndDate());
        c2.set(Calendar.YEAR, 2016);
        c2.set(Calendar.MONTH, Calendar.JANUARY);
        c2.set(Calendar.DAY_OF_MONTH, 4 + n);
        event.setEndDate(c2.getTime());
        event.setTitle(getAsignaturaController().getSelected().getNombre() + " - "
                + getUsuarioController().getSelected().getNombreLogin());
        if (event.getId() == null) {
            String minutos1 = "";
            if (c1.get(Calendar.MINUTE) == 0) {
                minutos1 = "00";
            } else {
                minutos1 = "30";
            }
            String minutos2 = "";
            if (c2.get(Calendar.MINUTE) == 0) {
                minutos2 = "00";
            } else {
                minutos2 = "30";
            }
            Calendar intensidad = Calendar.getInstance();
            intensidad.setTime(event.getEndDate());
            intensidad.add(Calendar.HOUR, -c2.get(Calendar.HOUR));
            intensidad.add(Calendar.MINUTE, -c2.get(Calendar.MINUTE));
            String minutosIntensidad = "";
            if (intensidad.get(Calendar.MINUTE) == 0) {
                minutosIntensidad = "00";
            } else {
                minutosIntensidad = "30";
            }

            selected.setIntensidad(intensidad.get(Calendar.HOUR) + ":" + minutosIntensidad);
            selected.setHEntrada(c1.get(Calendar.HOUR) + ":" + minutos1);
            selected.setHSalida(c2.get(Calendar.HOUR) + ":" + minutos2);
            selected.setEstado(1);
            selected.setNombreAsignatura(getAsignaturaController().getSelected().getNombreAsignatura());
            selected.setSalon("");
            selected.setCodasignatura(getAsignaturaController().getSelected());
            selected.setIdPlan(getPlanController().getSelected());
            selected.setULogin(getUsuarioController().getSelected());
            create();
            event.setId("" + selected.getIdHorario());
            event.setData(selected);
            getEventModel().addEvent(event);
            event = new DefaultScheduleEvent();
            selected = new Horario();
        } else {
            getEventModel().updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        FacesContext.getCurrentInstance().addMessage("Horario Registrado", message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        FacesContext.getCurrentInstance().addMessage("Horario ", message);
    }

    public void cargarEventos() {
        Query query;
        query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.estado=:ESTADO");
        query.setParameter("ESTADO", 1);
        eventModel = new DefaultScheduleModel();
        List<Horario> consulta = query.getResultList();
        for (Horario h : consulta) {
            DefaultScheduleEvent evt = new DefaultScheduleEvent();

            int n = 0;
            Calendar start = Calendar.getInstance();
            switch (h.getDia()) {
                case "Lunes":
                    n = 0;
                    break;
                case "Martes":
                    n = 1;
                    break;
                case "Miercoles":
                    n = 2;
                    break;
                case "Jueves":
                    n = 3;
                    break;
                case "Viernes":
                    n = 4;
                    break;
                case "Sabado":
                    n = 5;
                    break;
            }
            start.setTime(new Date());
            start.set(Calendar.YEAR, 2016);
            start.set(Calendar.MONTH, Calendar.JANUARY);
            start.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hEntrada = h.getHEntrada();
            String[] entrada = hEntrada.split(":");
            start.set(Calendar.HOUR, Integer.parseInt(entrada[0]));
            start.set(Calendar.MINUTE, Integer.parseInt(entrada[1]));
            evt.setStartDate(start.getTime());
            Calendar end = Calendar.getInstance();

            end.setTime(new Date());
            end.set(Calendar.YEAR, 2016);
            end.set(Calendar.MONTH, Calendar.JANUARY);
            end.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hSalida = h.getHSalida();
            String[] salida = hSalida.split(":");
            end.set(Calendar.HOUR, Integer.parseInt(salida[0]));
            end.set(Calendar.MINUTE, Integer.parseInt(salida[1]));
            evt.setEndDate(end.getTime());

            evt.setData(h);
            evt.setTitle(h.getCodasignatura().getCodasignatura()
                    + " - " + h.getCodasignatura().getNombreAsignatura()
                    + " - " + h.getULogin().getNombreLogin());
            eventModel.addEvent(evt);
        }
    }

    public void activarDia() {
        if (event.getStartDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(event.getStartDate());
            switch (c.get(Calendar.DAY_OF_MONTH)) {
                case 4:
                    selected.setDia("Lunes");
                    break;
                case 5:
                    selected.setDia("Martes");
                    break;
                case 6:
                    selected.setDia("Miercoles");
                    break;
                case 7:
                    selected.setDia("Jueves");
                    break;
                case 8:
                    selected.setDia("Viernes");
                    break;
                case 9:
                    selected.setDia("Sabado");
                    break;
            }
        }
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
        cargarEventos();
        return eventModel;
    }

    /**
     * @param eventModel the eventModel to set
     */
    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
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
    public DefaultScheduleEvent getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(DefaultScheduleEvent event) {
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
