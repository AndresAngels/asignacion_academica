package vista;

import controladores.AsignaturasController;
import controladores.Controller;
import controladores.HorariosController;
import controladores.PerfilesController;
import controladores.PlanesController;
import controladores.UsuariosController;
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

    @ManagedProperty("#{asignaturasController}")
    private AsignaturasController asignaturasController;
    @ManagedProperty("#{horariosController}")
    private HorariosController horariosController;
    @ManagedProperty("#{perfilesController}")
    private PerfilesController perfilesController;
    @ManagedProperty("#{planesController}")
    private PlanesController planesController;
    @ManagedProperty("#{usuariosController}")
    private UsuariosController usuariosController;
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
            query = getJpaController().getEntityManager().createQuery("SELECT u FROM Usuarios u WHERE u.loginUsuario=:LOGIN AND u.idEstado=:ESTADO");
            query.setParameter("ESTADO", Controller.ACTIVO);
            query.setParameter("LOGIN", getUsuariosController().getUsuario().getLoginUsuario());
            Usuarios usuario = null;
            for (Usuarios u : (List<Usuarios>) query.getResultList()) {
                boolean perfil = "1".equals(u.getCodigoPerfil().getCodigoPerfil())
                        || "2".equals(u.getCodigoPerfil().getCodigoPerfil())
                        || "3".equals(u.getCodigoPerfil().getCodigoPerfil());
                if (u.getLoginUsuario().equals(getUsuariosController().getUsuario().getLoginUsuario())
                        && u.getPasswordUsuario().equals(getUsuariosController().getUsuario().getPasswordUsuario())
                        && perfil) {
                    usuario = u;
                }
            }
            if (usuario == null) {
                getUsuariosController().setUsuario(new Usuarios());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Datos invalidos"));
            } else {
                getUsuariosController().setUsuario(usuario);
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "No se pudo conectar a la base de datos");
            getUsuariosController().setUsuario(new Usuarios());
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
     * @return the usuariosController
     */
    public UsuariosController getUsuariosController() {
        return usuariosController;
    }

    /**
     * @param usuariosController the usuariosController to set
     */
    public void setUsuariosController(UsuariosController usuariosController) {
        this.usuariosController = usuariosController;
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
     * @return the asignaturasController
     */
    public AsignaturasController getAsignaturasController() {
        return asignaturasController;
    }

    /**
     * @param asignaturasController the asignaturaController to set
     */
    public void setAsignaturasController(AsignaturasController asignaturasController) {
        this.asignaturasController = asignaturasController;
    }

    /**
     * @return the horariosController
     */
    public HorariosController getHorariosController() {
        return horariosController;
    }

    /**
     * @param horariosController the horarioController to set
     */
    public void setHorariosController(HorariosController horariosController) {
        this.horariosController = horariosController;
    }

    /**
     * @return the PerfilesController
     */
    public PerfilesController getPerfilesController() {
        return perfilesController;
    }

    /**
     * @param perfilesController the PerfilesController to set
     */
    public void setPerfilesController(PerfilesController perfilesController) {
        this.perfilesController = perfilesController;
    }

    /**
     * @return the planesController
     */
    public PlanesController getPlanesController() {
        return planesController;
    }

    /**
     * @param planesController the planController to set
     */
    public void setPlanesController(PlanesController planesController) {
        this.planesController = planesController;
    }
}
