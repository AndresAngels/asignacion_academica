package vista;

import controladores.AsignaturasController;
import controladores.HorariosController;
import controladores.UsuariosController;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "acordeon")
@SessionScoped
public class Acordeon implements Serializable {

    @ManagedProperty("#{usuarioController}")
    private UsuariosController usuarioController;
    @ManagedProperty("#{asignaturaController}")
    private AsignaturasController asignaturaController;
    @ManagedProperty("#{horarioController}")
    private HorariosController horarioController;
    private String tituloTabla = "";
    private String tituloFormulario = "";

    public void llenar() {
        String value = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        if ("/administrador/registrousuarios.xhtml".equals(value)) {
            tituloTabla = "Consultar Usuarios";
            if (getUsuarioController().getSelected().getLogin() == null) {
                tituloFormulario = "Grabar Usuario";
            } else {
                tituloFormulario = "Actualizar Usuario";
            }
        } else if ("/administrador/registroasignaturas.xhtml".equals(value)) {
            tituloTabla = "Consultar Asignaturas";
            if (getAsignaturaController().getSelected().getCodigoAsignatura() == null) {
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
    public UsuariosController getUsuarioController() {
        return usuarioController;
    }

    /**
     * @param usuarioController the usuarioController to set
     */
    public void setUsuarioController(UsuariosController usuarioController) {
        this.usuarioController = usuarioController;
    }

    /**
     * @return the asignaturaController
     */
    public AsignaturasController getAsignaturaController() {
        return asignaturaController;
    }

    /**
     * @param asignaturaController the asignaturaController to set
     */
    public void setAsignaturaController(AsignaturasController asignaturaController) {
        this.asignaturaController = asignaturaController;
    }

    /**
     * @return the horarioController
     */
    public HorariosController getHorarioController() {
        return horarioController;
    }

    /**
     * @param horarioController the horarioController to set
     */
    public void setHorarioController(HorariosController horarioController) {
        this.horarioController = horarioController;
    }
}
