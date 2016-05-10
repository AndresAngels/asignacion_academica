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
import controladores.AsignaturaController;
import controladores.HorarioController;
import controladores.UsuarioController;
import controladores.util.JsfUtil;
import entidades.Asignatura;
import entidades.Horario;
import entidades.Usuarios;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
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
    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    @ManagedProperty("#{asignaturaController}")
    private AsignaturaController asignaturaController;
    @ManagedProperty("#{horarioController}")
    private HorarioController horarioController;
    private Date inicio = null;
    private Date fin = null;

    public void pdfCruds(int tipo) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document(PageSize.LETTER);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            if (!document.isOpen()) {
                document.open();
            }
            String fileName = null;
            switch (tipo) {
                case 0:
                    fileName = "Usuarios";
                    reporteUsuarios(document);
                    break;
                case 1:
                    fileName = "Asignaturas Docente";
                    reporteAsignaturasDocente(document);
                    break;
                case 2:
                    fileName = "Asignaturas por Doncente";
                    reporteHorarioAsignaturaDocente(document);
                    break;
                case 3:
                    fileName = "Horarios Plan";
                    reporteHorarioPlan(document);
                    break;
                case 4:
                    fileName = "Horarios General";
                    reporteHorarioGeneral(document);
                    break;
                case 5:
                    fileName = "Horarios por Cohorte";
                    reporteHorarioCohorte(document);
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

    public Document reporteUsuarios(Document document) {
        try {
            String titulo = "Usuarios";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(3);// Numero de campos de la tabla
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Nombres", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Apellidos", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Perfiles", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Usuarios m : getUsuarioController().getConsultaTabla()) {
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

    public Document reporteAsignaturasDocente(Document document) {
        try {
            String titulo = "Asignaturas Docente";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(2);// Numero de campos de la tabla
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Código Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Asignatura m : getAsignaturaController().getConsultaTabla()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioAsignaturaDocente(Document document) {
        try {
            String titulo = "Asignaturas por Docente";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(9);
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Cohorte", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Código Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Docente", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Intensidad", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Día", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Entrada", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Salida", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Horario m : getHorarioController().getConsultaAsignaturaDocente()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getCodasignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getULogin().getNombre(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getIntensidad(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getDia(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHEntrada(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHSalida(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioPlan(Document document) {
        try {
            String titulo = "Horarios Programa";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(9);
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Cohorte", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Código Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Docente", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Intensidad", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Día", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Entrada", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Salida", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Horario m : getHorarioController().getConsultaHorarioPrograma()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getCodasignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getULogin().getNombre(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getIntensidad(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getDia(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHEntrada(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHSalida(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioGeneral(Document document) {
        try {
            String titulo = "Asignaturas por Docente";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(9);
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Cohorte", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Código Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Docente", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Intensidad", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Día", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Entrada", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Salida", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Horario m : getHorarioController().getConsultaTabla()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getCodasignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getULogin().getNombre(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getIntensidad(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getDia(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHEntrada(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHSalida(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
    }

    public Document reporteHorarioCohorte(Document document) {
        try {
            String titulo = "Asignaturas por Docente";
            Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable tabla = new PdfPTable(9);
            tabla.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Cohorte", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Código Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Asignatura", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Docente", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Intensidad", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Día", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Entrada", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Salida", FontFactory.getFont("arial", 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
            for (Horario m : getHorarioController().getConsultaHorarioCohorte()) {
                tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getCodasignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getNombreAsignatura(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getULogin().getNombre(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getIntensidad(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getDia(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHEntrada(), FontFactory.getFont(ARIAL, 10))));
                tabla.addCell(new PdfPCell(new Phrase(m.getHSalida(), FontFactory.getFont(ARIAL, 10))));
            }
            document.add(tabla);
        } catch (DocumentException ex) {
            JsfUtil.addErrorMessage(ex, ex.toString());
        }
        return document;
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

    public HorarioController getHorarioController() {
        return horarioController;
    }

    /**
     * @param horarioController the usuarioController to set
     */
    public void setHorarioController(HorarioController horarioController) {
        this.horarioController = horarioController;
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
}
