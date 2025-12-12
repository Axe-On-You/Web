package ru.pmih.web.utils.exceptions;

import ru.pmih.web.entity.PointEntity;

public class ValidationError extends RuntimeException {
    public ValidationError(PointEntity p) {
        super(String.format("Object %s is not valid", p));
    }
}
