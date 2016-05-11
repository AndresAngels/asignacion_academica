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

    private static final String CARGA = "La carga del docente supera el horario";
    private static final String LUNES = "Lunes";
    private static final String MARTES = "Martes";
    private static final String MIERCOLES = "Miércoles";
    private static final String JUEVES = "Jueves";
    private static final String VIERNES = "Viernes";
    private static final String SABADO = "Sábado";
    private static final String DOMINGO = "Domingo";
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
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Horario p WHERE p.idPlan=:PLAN");
            query.setParameter("PLAN", selected.getIdPlan());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
            consultaTabla = new ArrayList<Horario>();
        }
        return consultaTabla;
    }

    public List<Horario> getConsultaHorarioCohorte() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Horario p WHERE p.idPlan=:PLAN AND p.cohorte=:COHORTE");
            query.setParameter("PLAN", getPlanController().getSelected());
            query.setParameter("COHORTE", selected.getCohorte());
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return new ArrayList<>();
    }

    public List<Horario> getConsultaAsignaturaDocente() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horario h WHERE h.uLogin=:DOCENTE");
            query.setParameter("DOCENTE", getUsuarioController().getSelected());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
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
            JsfUtil.addErrorMessage(npe, CONSULTA);
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
        if (n == 6) {
            JsfUtil.addErrorMessage("No se pueden establecer clases los domingos");
            return;
        }

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
        if (event.getStartDate().getTime() == event.getEndDate().getTime()) {
            JsfUtil.addErrorMessage("Las horas de entrada y salida deben ser diferentes");
            return;
        }
        if (event.getStartDate().getTime() > event.getEndDate().getTime()) {
            JsfUtil.addErrorMessage("Las horas de salida debe ser mayor a la de entrada");
            return;
        }
        event.setTitle(getAsignaturaController().getSelected().getNombre() + " - "
                + getUsuarioController().getSelected().getNombreLogin());
        String entrada = extraerHora(fechaEntrada);
        String salida = extraerHora(fechaSalida);
        if ("10:30".equals(salida)) {
            JsfUtil.addErrorMessage("No se puede seleccionar salidas a las 10:30 PM");
            return;
        }
        if (validarHorarioNuevo(getUsuarioController().getSelected().getCodigoPerfil().getCodigoPerfil(),
                getConsultaAsignaturaDocente(), selected.getDia(), entrada, salida)) {
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
        if (event.getData() == null) {
            create();
        } else {
            update();
        }
        event.setId("" + selected.getIdHorario());
        event.setData(selected);
        getEventModel().addEvent(event);
        event = new DefaultScheduleEvent();
        selected = new Horario();
        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public void onEventMove(ScheduleEntryMoveEvent eve) {
        this.event = (DefaultScheduleEvent) eve.getScheduleEvent();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(event.getStartDate());
        c1.add(Calendar.DAY_OF_MONTH, eve.getDayDelta());
        c1.add(Calendar.MINUTE, eve.getMinuteDelta());
        event.setStartDate(c1.getTime());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(event.getEndDate());
        c2.add(Calendar.DAY_OF_MONTH, eve.getDayDelta());
        c2.add(Calendar.MINUTE, eve.getMinuteDelta());
        event.setStartDate(c2.getTime());
        addEvent();
    }

    public void onEventResize(ScheduleEntryResizeEvent eve) {
        this.event = (DefaultScheduleEvent) eve.getScheduleEvent();
        Calendar c = Calendar.getInstance();
        c.setTime(event.getEndDate());
        c.add(Calendar.MINUTE, eve.getMinuteDelta());
        event.setStartDate(c.getTime());
        addEvent();
    }

    private boolean validarHorarioNuevo(String perfil,
            List<Horario> consultadoHorarios, String dia, String entrada, String salida) {
        if (validarEntradaSalida(consultadoHorarios, dia, entrada, salida)
                && validarCarga(perfil, consultadoHorarios, entrada, salida)) {
            return false;
        }
        return true;
    }

    private boolean validarEntradaSalida(List<Horario> consultahHorarios,
            String dia, String entrada, String salida) {
        for (Horario h : consultahHorarios) {
            boolean ent = entrada.compareTo(h.getHEntrada()) >= 0 && entrada.compareTo(h.getHSalida()) <= 0;
            boolean sal = salida.compareTo(h.getHEntrada()) >= 0 && salida.compareTo(h.getHSalida()) <= 0;
            if (dia.equals(h.getDia())
                    && (ent || sal)) {
                JsfUtil.addErrorMessage("El docente ya tiene una asignatura en este horario");
                return false;
            }
        }
        return true;
    }

    private boolean validarCarga(String perfil, List<Horario> consultahHorarios, String entrada, String salida) {
        double acumulador;
        String ent = entrada.replace(":", ".");
        ent = ent.replace("30", "5");
        String sal = salida.replace(":", ".");
        sal = sal.replace("30", "5");
        acumulador = Double.parseDouble(sal) - Double.parseDouble(ent);
        if ("4".equals(perfil) && acumulador > 12) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        if ("5".equals(perfil) && acumulador > 8) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        for (Horario h : consultahHorarios) {
            String intensidad = h.getIntensidad().replace(":", ".");
            intensidad = intensidad.replace("30", "5");
            acumulador += Double.parseDouble(intensidad);
        }
        if ("4".equals(perfil) && acumulador > 12) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        if ("5".equals(perfil) && acumulador > 8) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        return true;
    }

    public void cargarEventos() {
        eventModel = new DefaultScheduleModel();
        List<Horario> consulta = getConsultaHorarioCohorte();
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
                    selected.setDia(DOMINGO);
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

    public void desactivar() {
        if (event.getData() != null) {
            selected = (Horario) event.getData();
            selected.setEstado(2);
            update();
        }
    }

    public void createOrUpdate(String opcion) {
        try {
            if ("3".equals(getUsuarioController().getUsuario().getCodigoPerfil().getCodigoPerfil())) {
                selected.setIdPlan(getUsuarioController().getUsuario().getIdPlan());
            }
            if (opcion.equals(CREATE)) {
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorarioCreated"));
            } else {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorarioUpdated"));
            }
            selected = new Horario();
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
                return 6;
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

    public boolean activarCalendario() {
        if (getUsuarioController().getUsuario().getIdPlan() != null) {
            selected.setPlan(getUsuarioController().getUsuario().getIdPlan().getIdPlan());
        }
        if (selected != null && selected.getPlan() != null && !"".equals(selected.getPlan()) && selected.getCohorte() != 0) {
            return true;
        }
        return false;

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
