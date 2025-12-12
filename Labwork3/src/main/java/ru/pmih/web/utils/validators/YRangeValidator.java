package ru.pmih.web.utils.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("yRangeValidator")
public class YRangeValidator implements Validator<Number> {
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Number value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", "Y не может быть пустым"));
        }

        double y = value.doubleValue();
        if (y <= -3 || y >= 3) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", "Y должен быть от -3 до 3 НЕ включительно"));
        }
    }
}