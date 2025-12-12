package ru.pmih.web.utils.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("yRangeValidator")
public class YRangeValidator implements Validator<Float> {
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Float aFloat) throws ValidatorException {
        if (-5 < aFloat && aFloat < 3) return;
        throw new ValidatorException(new FacesMessage("Ошибка валидации", "Y должен быть от -5 до 3 НЕ включительно"));
    }
}
