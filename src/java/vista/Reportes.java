package vista;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import controladores.HorarioController;
import controladores.UsuarioController;
import entidades.Horario;
import entidades.Usuarios;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "reportes")
@SessionScoped
public class Reportes {

    @ManagedProperty("#{sesion}")
    private Sesion sesion;
    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;
    @ManagedProperty("#{horarioController}")
    private HorarioController horarioController;
    private String panel = "";
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
            String titulo = "";
            Paragraph paragraph;
            PdfPTable tabla;
            PdfPCell cell;
            String fileName = "Usuarios";
            switch (tipo) {
                case 0:
                    titulo = "Usuarios";
                    fileName = titulo;
                    paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    tabla = new PdfPTable(3);// Numero de campos de la tabla
                    tabla.setWidthPercentage(100);
                    cell = new PdfPCell(new Phrase("Nombres", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                    cell = new PdfPCell(new Phrase("Apellidos", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                    cell = new PdfPCell(new Phrase("Perfiles", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);

                    for (Usuarios m : getUsuarioController().getConsultaTabla()) {
                        tabla.addCell(new PdfPCell(new Phrase(m.getNombre(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getApellido(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getCodigoPerfil().getDescripcionPerfil(), FontFactory.getFont("arial", 10))));
                    }
                    document.add(tabla);
                    break;

                case 1:
                    titulo = "Asignaturas Docente";
                    fileName = titulo;
                    paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    tabla = new PdfPTable(4);// Numero de campos de la tabla
                    tabla.setWidthPercentage(100);
                    cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
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
                    for (Horario m : getHorarioController().getConsultaAsignaturaDocente()) {
                        tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getCodasignatura().getCodasignatura(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getNombreAsignatura(), FontFactory.getFont("arial", 10))));
                    }
                    document.add(tabla);
                    break;

                case 2:
                    titulo = "Horarios Programa";
                    fileName = titulo;
                    paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    tabla = new PdfPTable(9);
                    tabla.setWidthPercentage(100);
                    cell = new PdfPCell(new Phrase("Plán", FontFactory.getFont("arial", 10, Font.BOLD)));
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
                        tabla.addCell(new PdfPCell(new Phrase(m.getIdPlan().getIdPlan(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase("" + m.getCohorte(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getNombreAsignatura(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getULogin().getNombre(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getIntensidad(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getDia(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getHEntrada(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getHSalida(), FontFactory.getFont("arial", 10))));

                    }
                    document.add(tabla);
                    break;
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
        } catch (Exception e) {
            Logger.getLogger(Reportes.class.getName()).log(Level.INFO, e.toString());
        }
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
        fin = c.getTime();
        this.fin = fin;
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
}
