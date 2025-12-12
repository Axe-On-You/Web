package ru.pmih.web.utils.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import ru.pmih.web.dto.PointDTO;
import ru.pmih.web.entity.PointEntity;

@ApplicationScoped
public class PointValidator extends AbstractValidator<PointEntity> {
    public PointValidator() {
        super(pointEntity -> (
                        (1 <= pointEntity.getR() && pointEntity.getR() <= 5) &&
                                (-4 <= pointEntity.getX() && pointEntity.getX() <= 4) &&
                                (-5 < pointEntity.getY() && pointEntity.getY() < 3)
                )
        );
    }

    /**
     * Проверка принадлежности точке к площади согласно варианту
     * @param point координаты и радиус
     * @return true если внутри, false если вне площади
     */
    public boolean checkArea(PointDTO point) {
        Float x = point.getX(), y = point.getY(), r = point.getR();

        if (x >= 0 && y >= 0) {
            return x * x + y * y < (r / 2) * (r / 2);
        }
        else if (x < 0 && y >= 0) {
            return y <= x / 2 + r / 2;
        }
        else if (x < 0 && y < 0) {
            return false;
        }
        else {
            return x <= r / 2 && y >= -r;
        }
    }
}
