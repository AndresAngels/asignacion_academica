/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import entidades.Plan;
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
import modelos.PlanJpaController;

/**
 *
 * @author AndresAngel
 */
@ManagedBean(name = "planController")
@SessionScoped
public class PlanController implements Serializable {

    private PlanJpaController jpaController;
    Plan selected;
    private List<Plan> consultaTabla;

    /**
     * Creates a new instance of PlanController
     */
    public PlanController() {
    }

    public List<Plan> getConsultaTabla() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Plan p WHERE p.estado=:ESTADO ORDER BY p.descripcion");
            query.setParameter("ESTADO", 1);
            consultaTabla = query.getResultList();
        } catch (NullPointerException npe) {
        }
        return consultaTabla;
    }

    /**
     * @return the jpaController
     */
    public PlanJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new PlanJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    /**
     * @param jpaController the jpaController to set
     */
    public void setJpaController(PlanJpaController jpaController) {
        this.jpaController = jpaController;
    }

    /**
     * @return the selected
     */
    public Plan getSelected() {
        if (selected == null) {
            selected = new Plan();
        }
        return selected;
    }

    public void setSelected(Plan selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Plan.class)
    public static class PlanControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PlanController controller = (PlanController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "planController");
            return controller.getJpaController().findPlan(value);
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Plan) {
                Plan o = (Plan) object;
                return o.getIdPlan();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Plan.class.getName());
            }
        }

    }

}
