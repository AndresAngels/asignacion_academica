/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import entidades.Horario;
import entidades.Plan;
import entidades.Usuarios;
import java.util.Calendar;
import java.util.Date;
import modelos.HorarioJpaController;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author AndresAngel
 */
public class HorarioControllerTest {

    public HorarioControllerTest() {
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
     * Test of activarDia method, of class HorarioController.
     */
    @Test
    public void testActivarDia() {
        System.out.println("activarDia");
        HorarioController instance = new HorarioController();
        instance.setEvent(new DefaultScheduleEvent());
        Calendar fechaEntrada = Calendar.getInstance();
        fechaEntrada.setTime(new Date());
        fechaEntrada.set(Calendar.YEAR, 2016);
        fechaEntrada.set(Calendar.MONTH, Calendar.JANUARY);
        fechaEntrada.set(Calendar.DAY_OF_MONTH, 4);
        instance.setSelected(new Horario());
        instance.getEvent().setStartDate(fechaEntrada.getTime());
        instance.activarDia();
        String result = instance.getSelected().getDia();
        assertEquals("Lunes", result);
    }

    /**
     * Test of extraerHora method, of class HorarioController.
     */
    @Test
    public void testExtraerHora() {
        System.out.println("extraerHora");
        Calendar objetivo = Calendar.getInstance();
        objetivo.set(Calendar.HOUR_OF_DAY, 7);
        objetivo.set(Calendar.MINUTE, 30);
        HorarioController instance = new HorarioController();
        String expResult = "7:30";
        String result = instance.extraerHora(objetivo);
        assertEquals(expResult, result);
    }

    /**
     * Test of obtenerDia method, of class HorarioController.
     */
    @Test
    public void testObtenerDia() {
        System.out.println("obtenerDia");
        String dia = "";
        HorarioController instance = new HorarioController();
        int expResult = 0;
        int result = instance.obtenerDia("Lunes");
        assertEquals(expResult, result);
    }

    /**
     * Test of activarCalendario method, of class HorarioController.
     */
    @Test
    public void testActivarCalendario() {
        System.out.println("activarCalendario");
        HorarioController instance = new HorarioController();
        instance.setUsuarioController(new UsuarioController());
        instance.getUsuarioController().setUsuario(new Usuarios());
        instance.getUsuarioController().getUsuario().setIdPlan(new Plan("1"));
        instance.setSelected(new Horario());
        instance.getSelected().setIdPlan(new Plan("1"));
        boolean expResult = false;
        boolean result = instance.activarCalendario();
        assertEquals(expResult, result);
    }

}
