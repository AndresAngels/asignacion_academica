/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controladores.UsuarioController;
import entidades.Perfiles;
import entidades.Usuarios;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author AndresAngel
 */
public class MenuTest {

    public MenuTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of crearItem method, of class Menu.
     */
    @Test
    public void testCrearItem() {
        System.out.println("crearItem");
        String valor = "Reportes";
        String url = "/secretario/reportes.xhtml";
        Menu instance = new Menu();
        DefaultMenuItem expResult = new DefaultMenuItem(valor, url);
        DefaultMenuItem result = instance.crearItem(valor, url);
        assertEquals(expResult.getValue(), result.getValue());
    }

    /**
     * Test of getModel method, of class Menu.
     */
    @Test
    public void testGetModel() {
        System.out.println("getModel");
        Menu instance = new Menu();
        UsuarioController usu = new UsuarioController();
        Usuarios usuario = new Usuarios();
        usuario.setCodigoPerfil(new Perfiles("2"));
        usu.setUsuario(usuario);
        instance.setUsuarioController(usu);
        DefaultMenuModel model = new DefaultMenuModel();
        DefaultSubMenu submenu;
        submenu = new DefaultSubMenu("Reportes");
        submenu.addElement(new DefaultMenuItem("Generar Reportes", "/secretario/reportes.xhtml"));
        model.addElement(submenu);
        MenuModel expResult = model;
        MenuModel result = instance.getModel();
        assertEquals(expResult.getElements().size(), result.getElements().size());
    }

}
