package validadores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("asignatura")
public class AsignaturaValidator extends Validador implements Validator {

    public static final String PATTERN = "[0-9]{6}[M]";
    public static final String MENSAJE = ": No es un codigo de asignatura valido";

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        validar(PATTERN, MENSAJE, uic, o);
    }
}
