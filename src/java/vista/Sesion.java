package vista;

import controladores.AsignaturaController;
import controladores.HorarioController;
import controladores.PerfilesController;
import controladores.PlanController;
import controladores.UsuarioController;
import controladores.util.JsfUtil;
import entidades.Usuarios;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.uActivo=:ESTADO");
            query.setParameter("ESTADO", 1);
            Usuarios usuario = null;
            for (Usuarios u : (List<Usuarios>) query.getResultList()) {
                boolean perfil = "1".equals(u.getCodigoPerfil().getCodigoPerfil())
                        || "2".equals(u.getCodigoPerfil().getCodigoPerfil())
                        || "3".equals(u.getCodigoPerfil().getCodigoPerfil());
                if (u.getULogin().equals(getUsuarioController().getUsuario().getULogin())
                        && u.getUPassword().equals(getUsuarioController().getUsuario().getUPassword())
                        && perfil) {
                    usuario = u;
                }
            }
            if (usuario == null) {
                getUsuarioController().setUsuario(new Usuarios());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Datos invalidos"));
            } else {
                getUsuarioController().setUsuario(usuario);
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "No se pudo conectar a la base de datos");
            getUsuarioController().setUsuario(new Usuarios());
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
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
