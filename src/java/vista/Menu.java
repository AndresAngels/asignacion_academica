package vista;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@ManagedBean(name = "menu")
@SessionScoped
public class Menu implements Serializable {

    @ManagedProperty("#{sesion}")
    Sesion sesion;
    private MenuModel model;

    private void llenarMenu() {
        model = new DefaultMenuModel();

        // Formularios de ingreso
        DefaultSubMenu submenu = new DefaultSubMenu("Registro de Informacion");
        submenu.addElement(crearItem("Registrar Usuarios", "/administrador/registrousuarios.xhtml"));

        submenu.addElement(crearItem("Registrar Asignaturas", "/administrador/registroasignaturas.xhtml"));

        submenu.addElement(crearItem("Registrar Horarios", "/coordinador/registrohorario.xhtml"));

        model.addElement(submenu);

        // Reportes
        submenu = new DefaultSubMenu("Reportes");

        submenu.addElement(crearItem("Generar reportes", "/secretario/reportes.xhtml"));

        model.addElement(submenu);
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

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
