/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controladores.AsignaturasController;
import controladores.HorariosController;
import controladores.UsuariosController;
import entidades.Asignaturas;
import entidades.Horarios;
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

    @ManagedProperty("#{usuariosController}")
    private UsuariosController usuariosController;
    @ManagedProperty("#{asignaturasController}")
    private AsignaturasController asignaturasController;
    @ManagedProperty("#{horariosController}")
    private HorariosController horariosControlller;
    @ManagedProperty("#{sesion}")
    private Sesion sesion;

    public void indexLoader() {
        getUsuariosController().setSelected(new Usuarios());
        getAsignaturasController().setSelected(new Asignaturas());
        getHorariosController().setSelected(new Horarios());
        getUsuariosController().setReactivar(false);
    }

    public void limpiarCruds() {
        getUsuariosController().setSelected(new Usuarios());
        getAsignaturasController().setSelected(new Asignaturas());
        getHorariosController().setSelected(new Horarios());
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
     * @return the asignaturasController
     */
    public AsignaturasController getAsignaturasController() {
        return asignaturasController;
    }

    /**
     * @param asignaturasController the asignaturasController to set
     */
    public void setAsignaturasController(AsignaturasController asignaturasController) {
        this.asignaturasController = asignaturasController;
    }

    /**
     * @return the horariosControlller
     */
    public HorariosController getHorariosController() {
        return horariosControlller;
    }

    /**
     * @param horariosControlller the horariosControlller to set
     */
    public void setHorariosControlller(HorariosController horariosControlller) {
        this.horariosControlller = horariosControlller;
    }
}
