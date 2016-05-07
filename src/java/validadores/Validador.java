package validadores;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.validator.ValidatorException;

public class Validador {

    public void validar(String patternString, String mensaje, UIComponent uic, Object o) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher((CharSequence) o);
        HtmlInputText htmlInputText = (HtmlInputText) uic;
        String label;
        if (htmlInputText.getLabel() == null || "".equals(htmlInputText.getLabel().trim())) {
            label = htmlInputText.getId();
        } else {
            label = htmlInputText.getLabel();
        }
        if (!matcher.matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    label + mensaje,
                    label + mensaje));
        }
    }
}
