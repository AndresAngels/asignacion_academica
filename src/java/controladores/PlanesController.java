/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.util.JsfUtil;
import entidades.Planes;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.PlanesJpaController;

/**
 *
 * @author AndresAngel
 */
@ManagedBean(name = "planesController")
@SessionScoped
public class PlanesController extends Controller implements Serializable {

    private Planes selected;
    private PlanesJpaController jpaController;
    private List<Planes> consultaTabla;

    public List<Planes> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Planes p WHERE p.idEstado=:ESTADO ORDER BY p.descripcionPlan");
            query.setParameter("ESTADO", ACTIVO);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, CONSULTA);
        }
        return consultaTabla;
    }

    /**
     * @return the jpaController
     */
    public PlanesJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new PlanesJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(PlanesJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Planes getSelected() {
        if (selected == null) {
            selected = new Planes();
        }
        return selected;
    }

    public void setSelected(Planes selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Planes.class)
    public static class PlanesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PlanesController controller = (PlanesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "planesController");
            return controller.getJpaController().findPlanes(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Planes) {
                Planes o = (Planes) object;
                return o.getIdPlan();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Planes.class.getName());
            }
        }

    }

}
