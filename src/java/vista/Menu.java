package vista;

import controladores.UsuariosController;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@ManagedBean(name = "menu")
@SessionScoped
public class Menu implements Serializable {

    @ManagedProperty("#{usuariosController}")
    private UsuariosController usuariosController;
    private MenuModel model;

    private void llenarMenu() {
        model = new DefaultMenuModel();

        // Formularios de ingreso
        DefaultSubMenu submenu;
        String perfil = getUsuariosController().getUsuario().getCodigoPerfil().getCodigoPerfil();
        if ("1".equals(perfil) || "3".equals(perfil)) {
            submenu = new DefaultSubMenu("Registro de Informacion");

            submenu.addElement(crearItem("Registrar Usuarios", "/administrador/registrousuarios.xhtml"));

            submenu.addElement(crearItem("Registrar Asignaturas", "/administrador/registroasignaturas.xhtml"));

            submenu.addElement(crearItem("Registrar Horarios", "/coordinador/registrohorario.xhtml"));
            model.addElement(submenu);
        }

        if ("2".equals(perfil)) {
            submenu = new DefaultSubMenu("Ver horario");
            submenu.addElement(crearItem("Ver Horarios", "/coordinador/registrohorario.xhtml"));
            model.addElement(submenu);
        }
        submenu = new DefaultSubMenu("Reportes");

        submenu.addElement(crearItem("Generar reportes", "/secretario/reportes.xhtml"));

        model.addElement(submenu);
    }

    public boolean esVisible() {
        String value = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        String perfil = "";
        if (getUsuariosController().getUsuario().getCodigoPerfil() != null) {
            perfil = getUsuariosController().getUsuario().getCodigoPerfil().getCodigoPerfil();
        }
        if ("/coordinador/registrohorario.xhtml".equals(value)
                || "/secretario/reportes.xhtml".equals(value)
                || "/index.xhtml".equals(value)
                || "/".equals(value)) {
            return true;
        }
        if (("/administrador/registrousuarios.xhtml".equals(value)
                || "/administrador/registroasignaturas.xhtml".equals(value))
                && ("1".equals(perfil) || "3".equals(perfil))) {
            return true;
        }
        return false;
    }

    public DefaultMenuItem crearItem(String valor, String url) {
        DefaultMenuItem item = new DefaultMenuItem();
        item.setValue(valor);
        item.setUrl(url);
        return item;
    }

    public MenuModel getModel() {
        llenarMenu();
        return model;
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

}
