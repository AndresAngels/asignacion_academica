package vista;

import controladores.UsuarioController;
import entidades.Usuarios;
import java.io.Serializable;
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

    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    private SesionJpaController jpaController;
    private String tema = "aristo";
    private String width = "0";
    private String height = "0";
    private GuestPreferences gp = new GuestPreferences();

    public Sesion() {
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
        getUsuarioController().setUsuario(new Usuarios());
        return "success";
    }

    public SesionJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new SesionJpaController(Persistence.createEntityManagerFactory("asignacion_academicaﬁ"));
        }
        return jpaController;
    }

    /**
     * @return the tema
     */
    public String getTema() {
        return tema;
    }

    /**
     * @param tema the tema to set
     */
    public void setTema(String tema) {
        this.tema = tema;
    }

    /**
     * @return the gp
     */
    public GuestPreferences getGp() {
        return gp;
    }

    /**
     * @param gp the gp to set
     */
    public void setGp(GuestPreferences gp) {
        this.gp = gp;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(String height) {
        this.height = height;
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
}
