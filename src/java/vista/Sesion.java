package vista;

import controladores.AsignaturaController;
import controladores.HorarioController;
import controladores.PerfilesController;
import controladores.PlanController;
import controladores.UsuarioController;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.SesionJpaController;

@ManagedBean(name = "sesion")
@SessionScoped
public class Sesion implements Serializable {

    @ManagedProperty("#{asignaturaController}")
    private AsignaturaController asignaturaController;
    @ManagedProperty("#{horarioController}")
    private HorarioController horarioController;
    @ManagedProperty("#{perfilesController}")
    private PerfilesController perfilesController;
    @ManagedProperty("#{planController}")
    private PlanController planController;
    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    private SesionJpaController jpaController;
    private Date fechaInicial;

    public Sesion() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 4);
        fechaInicial = c.getTime();
    }

    public void iniciarSesion() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.uLogin=:LOGIN AND u.uPassword=:CONTRASENA AND u.uActivo=:ESTADO");
            query.setParameter("LOGIN", getUsuarioController().getUsuario().getULogin());
            query.setParameter("CONTRASENA", getUsuarioController().getUsuario().getUPassword());
            query.setParameter("ESTADO", 1);
            if (!query.getResultList().isEmpty()) {
                Usuarios get = (Usuarios) query.getSingleResult();
                getUsuarioController().setUsuario(get);
            } else {
                getUsuarioController().setUsuario(new Usuarios());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Datos invalidos"));
            }
        } catch (Exception e) {
            getUsuarioController().setUsuario(new Usuarios());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "La operacion no se pudo completar"));
            Logger.getLogger(Sesion.class.getName()).log(Level.WARNING, e.toString());
        }
    }

    public String cerrarSesion() {
        setAsignaturaController(new AsignaturaController());
        setHorarioController(new HorarioController());
        setPerfilesController(new PerfilesController());
        setPlanController(new PlanController());
        setUsuarioController(new UsuarioController());
        return "success";
    }

    public SesionJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new SesionJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    /**
     * @return the usuarioController
     */
    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    /**
     * @param usuarioController the usuarioController to set
     */
    public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
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
     * @return the horarioController
     */
    public HorarioController getHorarioController() {
        return horarioController;
    }

    /**
     * @param horarioController the horarioController to set
     */
    public void setHorarioController(HorarioController horarioController) {
        this.horarioController = horarioController;
    }

    /**
     * @return the PerfilesController
     */
    public PerfilesController getPerfilesController() {
        return perfilesController;
    }

    /**
     * @param PerfilesController the PerfilesController to set
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
}
