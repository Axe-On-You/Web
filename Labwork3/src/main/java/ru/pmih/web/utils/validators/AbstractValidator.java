package ru.pmih.web.utils.validators;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

/**
 * Абстрактный класс валидатора на предикате
 * @param <T> валидируемый класс
 */
@Getter
@Setter
public abstract class AbstractValidator<T> {
    private Predicate<T> validatePredicate;

    public AbstractValidator(Predicate<T> predicate) {
        validatePredicate = predicate;
    }

    public boolean validate(T obj) {
        return validatePredicate.test(obj);
    }
}