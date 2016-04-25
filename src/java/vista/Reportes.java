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
import controladores.UsuarioController;
import entidades.Usuarios;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
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
import javax.persistence.Query;
import vista.Sesion;

@ManagedBean(name = "reportes")
@SessionScoped
public class Reportes {

    @ManagedProperty("#{sesion}")
    private Sesion sesion;
    @ManagedProperty("#{usuariosController}")
    private UsuarioController usuarioController;
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
            String fileName = "";
            switch (tipo) {
                case 0: {
                    String titulo = "Usuarios";
                    fileName = titulo;
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
                        tabla.addCell(new PdfPCell(new Phrase(m.getNombre(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getApellido(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getCodigoPerfil().getDescripcionPerfil(), FontFactory.getFont("arial", 10))));
                   }
                    document.add(tabla);
                    break;
                }
                case 1: {
                    String titulo = "Macroprocesos";
                    fileName = titulo;
                    Paragraph paragraph = new Paragraph(titulo, FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    paragraph = new Paragraph(" ", FontFactory.getFont("arial", 14, Font.BOLD));
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    PdfPTable tabla = new PdfPTable(3);
                    tabla.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Sistema de Riesgo", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                    cell = new PdfPCell(new Phrase("Nombre", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                    cell = new PdfPCell(new Phrase("Descripción", FontFactory.getFont("arial", 10, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                    for (Macroproceso m : getMacroprocesoController().getConsultaTabla()) {
                        tabla.addCell(new PdfPCell(new Phrase(m.getIdmapariesgo().getNombre(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getNombre(), FontFactory.getFont("arial", 10))));
                        tabla.addCell(new PdfPCell(new Phrase(m.getDescripcion(), FontFactory.getFont("arial", 10))));
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
            }
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
}
