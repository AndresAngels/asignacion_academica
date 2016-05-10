package vista;

import controladores.AsignaturaController;
import controladores.HorarioController;
import controladores.UsuarioController;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "acordeon")
@SessionScoped
public class Acordeon implements Serializable {

    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    @ManagedProperty("#{asignaturaController}")
    private AsignaturaController asignaturaController;
    @ManagedProperty("#{horarioController}")
    private HorarioController horarioController;
    private String tituloTabla = "";
    private String tituloFormulario = "";

    public void llenar() {
        String value = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        if ("/administrador/registrousuarios.xhtml".equals(value)) {
            tituloTabla = "Consultar Usuarios";
            if (getUsuarioController().getSelected().getULogin() == null) {
                tituloFormulario = "Grabar Usuario";
            } else {
                tituloFormulario = "Actualizar Usuario";
            }
        } else if ("/administrador/registroasignaturas.xhtml".equals(value)) {
            tituloTabla = "Consultar Asignaturas";
            if (getAsignaturaController().getSelected().getCodasignatura() == null) {
                tituloFormulario = "Guardar Asignatura";
            } else {
                tituloFormulario = "Actualizar Asignatura";
            }
        }
    }

    /**
     * @return the tituloTabla
     */
    public String getTituloTabla() {
        llenar();
        return tituloTabla;
    }

    /**
     * @param tituloTabla the tituloTabla to set
     */
    public void setTituloTabla(String tituloTabla) {
        llenar();
        this.tituloTabla = tituloTabla;
    }

    /**
     * @return the tituloFormulario
     */
    public String getTituloFormulario() {
        llenar();
        return tituloFormulario;
    }

    /**
     * @param tituloFormulario the tituloFormulario to set
     */
    public void setTituloFormulario(String tituloFormulario) {
        llenar();
        this.tituloFormulario = tituloFormulario;
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
}
