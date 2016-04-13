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

    public Menu() {
    }

    private void llenarMenu() {
        model = new DefaultMenuModel();
        DefaultMenuItem item = new DefaultMenuItem();

        // Formularios de ingreso
        DefaultSubMenu submenu = new DefaultSubMenu("Registro de Informacion");
        item.setValue("Registrar Usuarios");
        item.setUrl("/administrador/registrodocentes.xhtml");
        submenu.addElement(item);

        item = new DefaultMenuItem();
        item.setValue("Registrar Asignaturas");
        item.setUrl("/administrador/registroasignaturas.xhtml");
        submenu.addElement(item);

        item = new DefaultMenuItem();
        item.setValue("Registrar Horarios");
        item.setUrl("/administrador/registrohorario.xhtml");
        submenu.addElement(item);

        model.addElement(submenu);

        // Reportes
        submenu = new DefaultSubMenu("Reportes");

        item = new DefaultMenuItem();
        item.setValue("Asignaturas por Docente");
        item.setUrl("/administrador/reportedocente.xhtml");
        submenu.addElement(item);

        item = new DefaultMenuItem();
        item.setValue("Horario por Programa");
        item.setUrl("/administrador/reporteprograma.xhtml");
        submenu.addElement(item);

        item = new DefaultMenuItem();
        item.setValue("Horario general");
        item.setUrl("/administrador/reportegeneral.xhtml");
        submenu.addElement(item);

        model.addElement(submenu);
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
