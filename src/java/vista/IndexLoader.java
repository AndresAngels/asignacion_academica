/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controladores.AsignaturaController;
import controladores.HorarioController;
import controladores.UsuarioController;
import entidades.Asignatura;
import entidades.Horario;
import entidades.Usuarios;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Aaron
 */
@ManagedBean(name = "indexLoader")
@SessionScoped
public class IndexLoader implements Serializable {

    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    @ManagedProperty("#{asignaturaController}")
    private AsignaturaController asignaturaController;
    @ManagedProperty("#{horarioController}")
    private HorarioController horarioControlller;
    @ManagedProperty("#{sesion}")
    private Sesion sesion;

    public void indexLoader() {
        getUsuarioController().setSelected(new Usuarios());
        getAsignaturaController().setSelected(new Asignatura());
        getHorarioController().setSelected(new Horario());
        getUsuarioController().setReactivar(false);
    }

    public void limpiarCruds() {
        getUsuarioController().setSelected(new Usuarios());
        getAsignaturaController().setSelected(new Asignatura());
        getHorarioController().setSelected(new Horario());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Actualizacion cancelada", "La actulizacion del registro a sido cancelada"));
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
     * @return the horarioControlller
     */
    public HorarioController getHorarioController() {
        return horarioControlller;
    }

    /**
     * @param horarioControlller the horarioControlller to set
     */
    public void setHorarioControlller(HorarioController horarioControlller) {
        this.horarioControlller = horarioControlller;
    }
}
