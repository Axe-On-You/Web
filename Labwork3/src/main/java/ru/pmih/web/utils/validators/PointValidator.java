package ru.pmih.web.utils.validators;

import jakarta.enterprise.context.ApplicationScoped;
import ru.pmih.web.dto.PointDTO;
import ru.pmih.web.entity.PointEntity;

@ApplicationScoped
public class PointValidator extends AbstractValidator<PointEntity> {
    public PointValidator() {
        super(pointEntity -> (
                (1 <= pointEntity.getR() && pointEntity.getR() <= 3) &&
                        (-4 <= pointEntity.getX() && pointEntity.getX() <= 2) &&
                        (-3 < pointEntity.getY() && pointEntity.getY() < 3)
        ));
    }

    /**
     * Проверка принадлежности точки к площади согласно варианту
     * Область:
     * - Четверть круга в левой нижней части (x <= 0, y <= 0), радиус R
     * - Треугольник в правой верхней части (x >= 0, y >= 0), вершины:  (0,0), (R,0), (0,R)
     * - Прямоугольник в левой верхней части (x <= 0, y >= 0), от (-R/2,0) до (0,R)
     *
     * @param point координаты и радиус
     * @return true если внутри, false если вне площади
     */
    public boolean checkArea(PointDTO point) {
        Float x = point.getX(), y = point.getY(), r = point.getR();

        // Левая нижняя часть — четверть круга радиуса R
        if (x <= 0 && y <= 0) {
            return x * x + y * y <= r * r;
        }
        // Правая верхняя часть — треугольник (0,0), (R,0), (0,R)
        else if (x >= 0 && y >= 0) {
            return y <= -x + r; // уравнение линии от (R,0) до (0,R): y = -x + R
        }
        // Левая верхняя часть — прямоугольник от (-R/2, 0) до (0, R)
        else if (x <= 0 && y >= 0) {
            return x >= -r / 2 && y <= r;
        }
        // Правая нижняя часть — пусто
        else {
            return false;
        }
    }
}