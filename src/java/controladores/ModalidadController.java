package controladores;

import static controladores.Controller.ACTIVO;
import static controladores.Controller.CONSULTA;
import controladores.util.JsfUtil;
import entidades.Modalidades;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.ModalidadesJpaController;

/**
 *
 * @author Aaron
 */
@ManagedBean(name = "modalidadController")
@SessionScoped
public class ModalidadController extends Controller {

    List<Modalidades> consultaTabla;
    ModalidadesJpaController jpaController;
    private Modalidades selected;

    public List<Modalidades> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT m FROM Modalidades m WHERE m.idEstado=:ESTADO ORDER BY m.descripcionModalidad");
            query.setParameter("ESTADO", ACTIVO);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    private ModalidadesJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new ModalidadesJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    /**
     * @return the selected
     */
    public Modalidades getSelected() {
        if (selected == null) {
            selected = new Modalidades();
        }
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Modalidades selected) {
        this.selected = selected;
    }
}
