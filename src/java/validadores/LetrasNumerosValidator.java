package validadores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("letrasnumeros")
public class LetrasNumerosValidator extends Validador implements Validator {

    public static final String PATTERN = "^([0-9a-záéíóúÁÉÍÓÚ\u00f1\u00d1A-Z,.;]+\\s*)+$";
    public static final String MENSAJE = ": Debe contener solo letras y numeros";

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        validar(PATTERN, MENSAJE, uic, o);
    }
}
