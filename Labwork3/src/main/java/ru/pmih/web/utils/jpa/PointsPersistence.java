package ru.pmih.web.utils.jpa;

import ru.pmih.web.entity.PointEntity;
import ru.pmih.web.utils.exceptions.ValidationError;

import java.util.List;

public interface PointsPersistence {
    void save(PointEntity p) throws ValidationError;
    void deleteById(Long id);
    List<PointEntity> getAllCreatedAtDesc();
}
