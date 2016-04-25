/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Persistence;
import modelos.PlanJpaController;

/**
 *
 * @author AndresAngel
 */
@ManagedBean(name = "planController")
@SessionScoped
public class PlanController implements Serializable {

    private PlanJpaController jpaController;
    
    /**
     * Creates a new instance of PlanController
     */
    public PlanController() {
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

}
