package validadores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("email")
public class EmailValidator extends Validador implements Validator {

    public static final String PATTERN = "^[^0-9][a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[@][a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,4}$";
    public static final String MENSAJE = ": No es una direccion e-mail valida";

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        validar(PATTERN, MENSAJE, uic, o);
    }
}
