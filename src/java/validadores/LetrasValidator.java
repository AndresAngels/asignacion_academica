package validadores;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("letras")
public class LetrasValidator implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        Pattern pattern = Pattern.compile("^([a-záéíóúÁÉÍÓÚ\u00f1\u00d1A-Z]+\\s*)+$");
        Matcher matcher = pattern.matcher((CharSequence) o);
        HtmlInputText htmlInputText = (HtmlInputText) uic;
        String label;
        if (htmlInputText.getLabel() == null || htmlInputText.getLabel().trim().equals("")) {
            label = htmlInputText.getId();
        } else {
            label = htmlInputText.getLabel();
        }
        if (!matcher.matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    label + ": Debe contener solo letras y numeros",
                    label + ": Debe contener solo letras y numeros"));
        }
    }
}
