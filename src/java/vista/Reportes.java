package vista;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import controladores.AsignaturasController;
import controladores.HorariosController;
import controladores.UsuariosController;
import controladores.util.JsfUtil;
import entidades.Asignaturas;
import entidades.Horarios;
import entidades.Usuarios;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "reportes")
@SessionScoped
public class Reportes {

    private static final String ARIAL = "arial";
    @ManagedProperty("#{sesion}")
    private Sesion sesion;
    @ManagedProperty("#{usuariosController}")
    private UsuariosController usuariosController;
    @ManagedProperty("#{asignaturasController}")
    private AsignaturasController asignaturasController;
    @ManagedProperty("#{horariosController}")
    private HorariosController horariosController;
    private Date inicio = null;
    private Date fin = null;
    private Document document;
    private PdfPTable tabla;

    public void pdfCruds(int tipo) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            document = new Document(PageSize.LETTER);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            if (!document.isOpen()) {
                document.open();
            }
            String fileName = null;
            switch (tipo) {
                case 0:
                    fileName = "Usuarios";
                    reporteUsuarios();
                    break;
                case 1:
                    fileName = "Asignaturas";
                    reporteAsignaturas();
                    break;
                case 2:
                    fileName = "Asignaturas por Docente";
                    reporteHorarioAsignaturaDocente();
                    break;
                case 3:
                    fileName = "Horarios Plan";
                    reporteHorarioPlan();
                    break;
                case 4:
                    fileName = "Horarios General";
                    reporteHorarioGeneral();
                    break;
                case 5:
                    fileName = "Horarios por Cohorte";
                    reporteHorarioCohorte();
                    break;
                default:
            }

            document.close();
            context.getExternalContext().responseReset();
            context.getExternalContext().setResponseContentType("application/pdf");
            context.getExternalContext().setResponseHeader("Expires", "0");
            context.getExternalContext().setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            context.getExternalContext().setResponseHeader("Pragma", "public");
            context.getExternalContext().setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            context.getExternalContext().setResponseContentLength(baos.size());
            OutputStream out = context.getExternalContext().getResponseOutputStream();
            baos.writeTo(out);
            context.getExternalContext().responseFlushBuffer();
            context.responseComplete();
        } catch (DocumentException | IOException e) {
            JsfUtil.addErrorMessage(e, e.toString());
        }
    }

    public Document reporteUsuarios() {
        try {
            String titulo = "Usuarios";
            document = reporteEncabezado(titulo);
            tabla = new PdfPTable(3);// Numero de campos de la tabla
            tabla.setWidthPercentage(100);
            ArrayList<String> nombres = new ArrayList<>();
            nombres.add("Nombres");
            nombres.add("Apellidos");
            nombres.add("Perfiles");
            reporteTablaGeneral(nombres);
            for (Usuarios m : getUsuariosController().getConsultaTabla()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getNombre(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getApellido(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodigoPerfil().getDescripcionPerfil(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteAsignaturas() {
        try {
            String titulo = "Asignaturas";
            document = reporteEncabezado(titulo);
            tabla = new PdfPTable(2);// Numero de campos de la tabla
            tabla.setWidthPercentage(100);
            ArrayList<String> nombres = new ArrayList<>();
            nombres.add("Codigo Asignatura");
            nombres.add("Nombre Asignatura");
            reporteTablaGeneral(nombres);
            for (Asignaturas m : getAsignaturasController().getConsultaTabla()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getCodigoAsignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioAsignaturaDocente() {
        try {
            String titulo = "Asignaturas por Docente";
            document = reporteEncabezado(titulo);
            String docente = getUsuariosController().getSelected().getNombreLogin();
            document = reporteEncabezado(docente);
            reporteTablaHorario();
            reporteLlenarTabla(getHorariosController().getConsultaAsignaturaDocente());
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioPlan() {
        try {
            String titulo = "Horarios Programa";
            document = reporteEncabezado(titulo);
            String programa = getHorariosController().getSelected().getIdPlan().getDescripcionPlan();
            document = reporteEncabezado(programa);
            reporteTablaHorario();
            reporteLlenarTabla(getHorariosController().getConsultaHorariosPrograma());
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioGeneral() {
        try {
            String titulo = "Horario General";
            document = reporteEncabezado(titulo);
            reporteTablaHorario();
            reporteLlenarTabla(getHorariosController().getConsultaTabla());
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioCohorte() {
        try {
            String titulo = "Horario por Cohorte";
            document = reporteEncabezado(titulo);
            String programa = getHorariosController().getSelected().getIdPlan().getDescripcionPlan()
                    + ", Cohorte: "
                    + getHorariosController().getSelected().getCohorteHorario();
            document = reporteEncabezado(programa);
            reporteTablaHorario();
            reporteLlenarTabla(getHorariosController().getConsultaHorariosCohorte());
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteEncabezado(String titulo) {
        try {
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont(ARIAL, 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont(ARIAL, 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
        } catch (DocumentException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;
    }

    public PdfPTable reporteTablaHorario() {
        tabla = new PdfPTable(9);
        tabla.setWidthPercentage(100);
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Plán");
        nombres.add("Cohorte");
        nombres.add("Código Asignatura");
        nombres.add("Nombre Asignatura");
        nombres.add("Docente");
        nombres.add("Intensidad");
        nombres.add("Día");
        nombres.add("Hora Entrada");
        nombres.add("Hora Salida");
        reporteTablaGeneral(nombres);
        return tabla;
    }

    public PdfPTable reporteTablaGeneral(List<String> nombres) {
        for (String n : nombres) {
            PdfPCell cell = new PdfPCell(new Phrase(n, FontFactory.getFont(ARIAL, 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
        }
        return tabla;
    }

    public PdfPTable reporteLlenarTabla(List<Horarios> consulta) {
        for (Horarios m : consulta) {
            tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorteHorario(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getCodigoAsignatura().getCodigoAsignatura(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getCodigoAsignatura().getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getLoginUsuario().getNombre(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getIntensidadHorario(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getDiaHorario(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getHEntradaHorario(), FontFactory.getFont(ARIAL, 10))));
            tabla.addCell(new PdfPCell(new Phrase(m.getHSalidaHorario(), FontFactory.getFont(ARIAL, 10))));
        }
        return tabla;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        Calendar c = Calendar.getInstance();
        c.setTime(fin);
        c.add(Calendar.HOUR, 23);
        c.add(Calendar.MINUTE, 59);
        c.add(Calendar.SECOND, 59);
        this.fin = c.getTime();
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

    public HorariosController getHorariosController() {
        return horariosController;
    }

    /**
     * @param horariosController the horariosController to set
     */
    public void setHorariosController(HorariosController horariosController) {
        this.horariosController = horariosController;
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
}
