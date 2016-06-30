package controladores;

import controladores.util.JsfUtil;
import entidades.Horarios;
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
import modelos.HorariosJpaController;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

@ManagedBean(name = "horariosController")
@SessionScoped
public class HorariosController extends Controller implements Serializable {

    private static final String CARGA = "La carga del docente supera el horario";
    private static final String LUNES = "Lunes";
    private static final String MARTES = "Martes";
    private static final String MIERCOLES = "Miércoles";
    private static final String JUEVES = "Jueves";
    private static final String VIERNES = "Viernes";
    private static final String SABADO = "Sábado";
    private static final String DOMINGO = "Domingo";
    @ManagedProperty("#{usuarioController}")
    private UsuariosController usuarioController;
    @ManagedProperty("#{planController}")
    private PlanesController planesController;
    @ManagedProperty("#{asignaturasController}")
    private AsignaturasController asignaturasController;
    @ManagedProperty("#{modalidadController}")
    private ModalidadController modalidadController;
    private Horarios primaryKey;  //usando para el modelo de tabla (con el que se va a buscar)
    private HorariosJpaController jpaController = null;
    private Horarios selected;
    private ScheduleModel eventModel = new DefaultScheduleModel();
    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    private List<Horarios> consultaTabla;

    public List<Horarios> getConsultaHorariosPrograma() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horarios h WHERE h.idPlan=:PLAN");
            query.setParameter("PLAN", selected.getIdPlan());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
            consultaTabla = new ArrayList<Horarios>();
        }
        return consultaTabla;
    }

    public List<Horarios> getConsultaHorariosCohorte() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horarios h WHERE h.idPlan=:PLAN AND h.cohorteHorario=:COHORTE");
            query.setParameter("PLAN", getPlanesController().getSelected());
            query.setParameter("COHORTE", selected.getCohorteHorario());
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return new ArrayList<>();
    }

    public List<Horarios> getConsultaHorariosGrupo() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horarios h WHERE (h.idPlan!=:PLAN OR h.cohorteHorario!=:COHORTE) AND h.codigoAsignatura=:ASIGNATURA AND h.grupoHorario=:GRUPO");
            query.setParameter("PLAN", getPlanesController().getSelected());
            query.setParameter("COHORTE", selected.getCohorteHorario());
            query.setParameter("ASIGNATURA", getAsignaturasController().getSelected());
            query.setParameter("GRUPO", selected.getGrupoHorario());
            return query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return new ArrayList<>();
    }

    public List<Horarios> getConsultaAsignaturaDocente() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horarios h WHERE h.loginUsuario=:DOCENTE");
            query.setParameter("DOCENTE", getUsuarioController().getSelected());
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    public List<Horarios> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT h FROM Horarios h WHERE h.idEstado=:ESTADO ORDER BY h.loginUsuario.loginUsuario");
            query.setParameter("ESTADO", ACTIVO);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    public void insertar() {
        selected.setIdEstado(ACTIVO);
        create();
    }

    public void addEvent() {
        Calendar fechaEntrada = Calendar.getInstance();
        Calendar fechaSalida = Calendar.getInstance();
        int n = obtenerDia(selected.getDiaHorario());
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
        event.setTitle(getAsignaturasController().getSelected().getNombreAsignatura() + " - "
                + getUsuarioController().getSelected().getNombreLogin());
        String entrada = extraerHora(fechaEntrada);
        String salida = extraerHora(fechaSalida);
        if ("10:30".equals(salida)) {
            JsfUtil.addErrorMessage("No se puede seleccionar salidas a las 10:30 PM");
            return;
        }
        if (validarHorariosNuevo(getUsuarioController().getSelected().getCodigoPerfil().getCodigoPerfil(),
                selected.getDiaHorario(), entrada, salida)) {
            return;
        }

        Calendar fechaIntencidad = Calendar.getInstance();
        fechaIntencidad.setTime(fechaSalida.getTime());
        fechaIntencidad.add(Calendar.HOUR_OF_DAY, -fechaEntrada.get(Calendar.HOUR_OF_DAY));
        fechaIntencidad.add(Calendar.MINUTE, -fechaEntrada.get(Calendar.MINUTE));
        String intensidad = extraerHora(fechaIntencidad);

        selected.setIntensidadHorario(intensidad);
        selected.setHEntradaHorario(entrada);
        selected.setHSalidaHorario(salida);
        selected.setIdEstado(ACTIVO);
        selected.setCodigoAsignatura(getAsignaturasController().getSelected());
        selected.setIdPlan(getPlanesController().getSelected());
        selected.setLoginUsuario(getUsuarioController().getSelected());
        selected.setIdModalidad(getModalidadController().getSelected());
        if (event.getData() == null) {
            create();
        } else {
            update();
        }
        event.setId("" + selected.getIdHorario());
        event.setData(selected);
        getEventModel().addEvent(event);
        event = new DefaultScheduleEvent();
        selected = new Horarios();
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

    private boolean validarHorariosNuevo(String perfil, String dia, String entrada, String salida) {
        if (validarEntradaSalida(dia, entrada, salida)
                && validarCarga(perfil, entrada, salida)
                && validarEntradaSalidaCohorte(dia, entrada, salida)
                && validarGrupo()) {
            return false;
        }
        return true;
    }

    private boolean validarEntradaSalida(
            String dia, String entrada, String salida) {
        for (Horarios h : getConsultaAsignaturaDocente()) {
            boolean ent = entrada.compareTo(h.getHEntradaHorario()) >= 0 && entrada.compareTo(h.getHSalidaHorario()) <= 0;
            boolean sal = salida.compareTo(h.getHEntradaHorario()) >= 0 && salida.compareTo(h.getHSalidaHorario()) <= 0;
            if (dia.equals(h.getDiaHorario())
                    && (ent || sal)) {
                JsfUtil.addErrorMessage("El docente ya tiene una asignatura en este horario");
                return false;
            }
        }
        return true;
    }

    private boolean validarCarga(String perfil, String entrada, String salida) {
        double acumulador;
        String ent = entrada.replace(":", ".");
        ent = ent.replace("30", "5");
        String sal = salida.replace(":", ".");
        sal = sal.replace("30", "5");
        acumulador = Double.parseDouble(sal) - Double.parseDouble(ent);
        if ("4".equals(perfil) && acumulador > 20) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        if ("5".equals(perfil) && acumulador > 8) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        for (Horarios h : getConsultaAsignaturaDocente()) {
            String intensidad = h.getIntensidadHorario().replace(":", ".");
            intensidad = intensidad.replace("30", "5");
            acumulador += Double.parseDouble(intensidad);
        }
        if ("4".equals(perfil) && acumulador > 20) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        if ("5".equals(perfil) && acumulador > 8) {
            JsfUtil.addErrorMessage(CARGA);
            return false;
        }
        return true;
    }

    private boolean validarEntradaSalidaCohorte(
            String dia, String entrada, String salida) {
        for (Horarios h : getConsultaHorariosCohorte()) {
            if (dia.equals(h.getDiaHorario())
                    && ((entrada.compareTo(h.getHEntradaHorario()) >= 0 && entrada.compareTo(h.getHSalidaHorario()) <= 0)
                    || (salida.compareTo(h.getHEntradaHorario()) >= 0 && salida.compareTo(h.getHSalidaHorario()) <= 0))) {
                JsfUtil.addErrorMessage("Ya existe una materia en ese horario para este cohorte");
                return false;
            }
        }
        return true;
    }

    private boolean validarGrupo() {
        if (!getConsultaHorariosGrupo().isEmpty()) {
            JsfUtil.addErrorMessage("Otro plan o cohorte ya uso este grupo en la misma asignatura");
            return false;
        }
        return true;
    }

    public void cargarEventos() {
        eventModel = new DefaultScheduleModel();
        List<Horarios> consulta = getConsultaHorariosCohorte();
        for (Horarios h : consulta) {
            DefaultScheduleEvent evt = new DefaultScheduleEvent();

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            int n = obtenerDia(h.getDiaHorario());
            start.setTime(new Date());
            start.set(Calendar.YEAR, 2016);
            start.set(Calendar.MONTH, Calendar.JANUARY);
            start.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hEntrada = h.getHEntradaHorario();
            String[] entrada = hEntrada.split(":");
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(entrada[0]));
            start.set(Calendar.MINUTE, Integer.parseInt(entrada[1]));
            evt.setStartDate(start.getTime());

            end.setTime(new Date());
            end.set(Calendar.YEAR, 2016);
            end.set(Calendar.MONTH, Calendar.JANUARY);
            end.set(Calendar.DAY_OF_MONTH, 4 + n);
            String hSalida = h.getHSalidaHorario();
            String[] salida = hSalida.split(":");
            end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(salida[0]));
            end.set(Calendar.MINUTE, Integer.parseInt(salida[1]));
            evt.setEndDate(end.getTime());

            evt.setData(h);
            evt.setTitle(h.getCodigoAsignatura().getCodigoAsignatura()
                    + " - " + h.getCodigoAsignatura().getNombreAsignatura()
                    + " - Grupo: " + h.getGrupoHorario()
                    + " - " + h.getLoginUsuario().getNombreLogin());
            eventModel.addEvent(evt);
        }
    }

    public void activarDia() {
        if (event.getStartDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(event.getStartDate());
            switch (c.get(Calendar.DAY_OF_MONTH)) {
                case 4:
                    selected.setDiaHorario(LUNES);
                    break;
                case 5:
                    selected.setDiaHorario(MARTES);
                    break;
                case 6:
                    selected.setDiaHorario(MIERCOLES);
                    break;
                case 7:
                    selected.setDiaHorario(JUEVES);
                    break;
                case 8:
                    selected.setDiaHorario(VIERNES);
                    break;
                case 9:
                    selected.setDiaHorario(SABADO);
                    break;
                default:
                    selected.setDiaHorario(DOMINGO);
                    break;
            }
        }
    }

    private HorariosJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new HorariosJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
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
            if ("3".equals(getUsuarioController().getUsuario().getCodigoPerfil().getCodigoPerfil())) {
                selected.setIdPlan(getUsuarioController().getUsuario().getIdPlan());
            }
            if (opcion.equals(CREATE)) {
                getJpaController().create(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorariosCreated"));
            } else {
                getJpaController().edit(selected);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("HorariosUpdated"));
            }
            selected = new Horarios();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
        }
    }

    public void prepararEdicion() {
        if (getPrimaryKey() != null) {
            selected = getJpaController().findHorarios(getPrimaryKey().getIdHorario());

        } else {
            Logger.getLogger(HorariosController.class
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
    public Horarios getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Horarios primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(HorariosJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Horarios getSelected() {
        if (selected == null) {
            selected = new Horarios();
        }
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Horarios selected) {
        this.selected = selected;
    }

    public UsuariosController getUsuarioController() {
        return usuarioController;
    }

    public void setUsuarioController(UsuariosController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public PlanesController getPlanesController() {
        return planesController;
    }

    public void setPlanesController(PlanesController planesController) {
        this.planesController = planesController;
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
    public AsignaturasController getAsignaturasController() {
        return asignaturasController;
    }

    /**
     * @param asignaturaController the asignaturaController to set
     */
    public void setAsignaturasController(AsignaturasController asignaturasController) {
        this.asignaturasController = asignaturasController;
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
            selected.setIdPlan(getUsuarioController().getUsuario().getIdPlan());
        }
        if (selected != null
                && ("1".equals(getUsuarioController().getUsuario().getCodigoPerfil().getCodigoPerfil())
                || "3".equals(getUsuarioController().getUsuario().getCodigoPerfil().getCodigoPerfil()))
                && selected.getIdPlan() != null
                && !selected.getIdPlan().equals("")
                && selected.getCohorteHorario() != 0) {
            return true;
        }
        return false;
    }

    /**
     * @return the modalidadController
     */
    public ModalidadController getModalidadController() {
        return modalidadController;
    }

    /**
     * @param modalidadController the modalidadController to set
     */
    public void setModalidadController(ModalidadController modalidadController) {
        this.modalidadController = modalidadController;
    }

    @FacesConverter(forClass = Horarios.class)
    public static class HorariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HorariosController controller = (HorariosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "horarioController");
            return controller.getJpaController().findHorarios(Integer.parseInt(value));
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
            if (object instanceof Horarios) {
                Horarios o = (Horarios) object;
                return "" + o.getIdHorario();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Horarios.class.getName());
            }
        }

    }
}
