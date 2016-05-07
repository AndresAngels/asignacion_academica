package controladores;

import controladores.util.JsfUtil;
import entidades.Horario;
import java.io.Serializable;
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
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.HorarioJpaController;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@SessionScoped
public class HorarioController extends Controller implements Serializable {

    private static final String LUNES = "Lunes";
    private static final String MARTES = "Martes";
    private static final String MIERCOLES = "Miércoles";
    private static final String JUEVES = "Jueves";
    private static final String VIERNES = "Viernes";
    private static final String SABADO = "Sábado";
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
    private List<Horario> consultaTabla;

    public List<Horario> getConsultaHorarioPrograma() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Horario p WHERE p.idPlan:PLAN");
            query.setParameter("PLAN", getPlanController().getSelected());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
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
            JsfUtil.addErrorMessage(npe, "Error al generar la consulta");
        }
        return consultaTabla;
    }

    public List<Horario> getConsultaAsignaturaConflicto(String dia, String entrada, String salida) {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.uLogin=:DOCENTE AND (h.hEntrada>=:ENTRADA AND h.hSalida<=:SALIDA)");
            query.setParameter("DOCENTE", getUsuarioController().getSelected());
            query.setParameter("ENTRADA", entrada);
            query.setParameter("SALIDA", salida);
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar la consulta");
            return null;
        }
    }

    public List<Horario> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.estado=:ESTADO ORDER BY h.uLogin.uLogin");
            query.setParameter("ESTADO", 1);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error generando la consultas");
        }
        return consultaTabla;
    }

    public void insertar() {
        selected.setEstado(1);
        create();
    }

    public void addEvent() {
        Calendar fechaEntrada = Calendar.getInstance();
        Calendar fechaSalida = Calendar.getInstance();
        int n = obtenerDia(selected.getDia());

        fechaEntrada.setTime(event.getStartDate());
        fechaEntrada.set(Calendar.YEAR, 2016);
        fechaEntrada.set(Calendar.MONTH, Calendar.JANUARY);
        fechaEntrada.set(Calendar.DAY_OF_MONTH, 4 + n);

        fechaSalida.setTime(event.getEndDate());
        fechaSalida.set(Calendar.YEAR, 2016);
        fechaSalida.set(Calendar.MONTH, Calendar.JANUARY);
        fechaSalida.set(Calendar.DAY_OF_MONTH, 4 + n);

        event.setStartDate(fechaEntrada.getTime());
        event.setEndDate(fechaSalida.getTime());
        event.setTitle(getAsignaturaController().getSelected().getNombre() + " - "
                + getUsuarioController().getSelected().getNombreLogin());
        if (event.getData() == null) {
            String entrada = extraerHora(fechaEntrada);
            String salida = extraerHora(fechaSalida);
            if (!getConsultaAsignaturaConflicto(selected.getDia(), entrada, salida).isEmpty()) {
                JsfUtil.addErrorMessage("El docente ya tiene una asignacion en este horario");
                return;
            }

            Calendar fechaIntencidad = Calendar.getInstance();
            fechaIntencidad.setTime(fechaSalida.getTime());
            fechaIntencidad.add(Calendar.HOUR_OF_DAY, -fechaEntrada.get(Calendar.HOUR_OF_DAY));
            fechaIntencidad.add(Calendar.MINUTE, -fechaEntrada.get(Calendar.MINUTE));
            String intensidad = extraerHora(fechaIntencidad);

            selected.setIntensidad(intensidad);
            selected.setHEntrada(entrada);
            selected.setHSalida(salida);
            selected.setEstado(1);
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

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            int n = obtenerDia(h.getDia());
            start.setTime(new Date());
            start.set(Calendar.YEAR, 2016);
            start.set(Calendar.MONTH, Calendar.JANUARY);
            start.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hEntrada = h.getHEntrada();
            String[] entrada = hEntrada.split(":");
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(entrada[0]));
            start.set(Calendar.MINUTE, Integer.parseInt(entrada[1]));
            evt.setStartDate(start.getTime());

            end.setTime(new Date());
            end.set(Calendar.YEAR, 2016);
            end.set(Calendar.MONTH, Calendar.JANUARY);
            end.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hSalida = h.getHSalida();
            String[] salida = hSalida.split(":");
            end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(salida[0]));
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
                    selected.setDia(LUNES);
                    break;
                case 5:
                    selected.setDia(MARTES);
                    break;
                case 6:
                    selected.setDia(MIERCOLES);
                    break;
                case 7:
                    selected.setDia(JUEVES);
                    break;
                case 8:
                    selected.setDia(VIERNES);
                    break;
                case 9:
                    selected.setDia(SABADO);
                    break;
                default:
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

    public void update() {
        createOrUpdate(UPDATE);
    }

    public void create() {
        createOrUpdate(CREATE);
    }

    public void createOrUpdate(String opcion) {
        try {
            if (opcion == CREATE) {
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorarioCreated"));
                selected = new Horario();
            } else {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorarioUpdated"));
                selected = new Horario();
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findHorario(getPrimaryKey().getIdHorario());

        } else {
            Logger.getLogger(HorarioController.class
                    .getName()).log(Level.INFO, "Parametros nulos");
        }
    }

    public void desactivar() {
        if (primaryKey != null) {
            selected = getJpaController().findHorario(primaryKey.getIdHorario());
            if (selected != null) {
                selected.setEstado(2);
                update();
            }
        }
    }

    public String extraerHora(Calendar objetivo) {
        String minutos;
        if (objetivo.get(Calendar.MINUTE) == 0) {
            minutos = "00";
        } else {
            minutos = "30";
        }
        return objetivo.get(Calendar.HOUR_OF_DAY) + ":" + minutos;
    }

    public int obtenerDia(String dia) {
        switch (dia) {
            case LUNES:
                return 0;
            case MARTES:
                return 1;
            case MIERCOLES:
                return 2;
            case JUEVES:
                return 3;
            case VIERNES:
                return 4;
            case SABADO:
                return 5;
            default:
                return 0;
        }
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
}
