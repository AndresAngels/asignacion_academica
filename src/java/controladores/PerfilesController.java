package controladores;

import controladores.util.JsfUtil;
import entidades.Perfiles;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Persistence;
import javax.persistence.Query;
import modelos.PerfilesJpaController;

@ManagedBean(name = "perfilesController")
@SessionScoped
public class PerfilesController {

    private Perfiles selected;
    private PerfilesJpaController jpaController;
    private List<Perfiles> perfiles;

    public List<Perfiles> getConsultaPerfiles() {
        try {
            Query query;
            query = getJpaController().getEntityManager().createQuery("SELECT p FROM Perfiles p ORDER BY p.descripcionPerfil");
            perfiles = query.getResultList();
        } catch (NullPointerException npe) {
            JsfUtil.addErrorMessage(npe, "Error al generar las consultas");
        }
        return perfiles;
    }

    private PerfilesJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new PerfilesJpaController(Persistence.createEntityManagerFactory("asignacion_academicaPU"));
        }
        return jpaController;
    }

    /**
     * @return the selected
     */
    public Perfiles getSelected() {
        if (selected == null) {
            selected = new Perfiles();
        }
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Perfiles selected) {
        this.selected = selected;
    }

    @FacesConverter(forClass = Perfiles.class)
    public static class PerfilesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PerfilesController controller = (PerfilesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "perfilesController");
            return controller.getJpaController().findPerfiles(value);
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Perfiles) {
                Perfiles o = (Perfiles) object;
                return o.getCodigoPerfil();
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Perfiles.class.getName());
            }
        }

    }
}
