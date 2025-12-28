package ru.pmih.web.dto;

import lombok.*;
import ru.pmih.web.entity.PointEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDTO {
    private Float x;
    private Float y;
    private Float r;
    private boolean hit;

    public PointDTO(Float x, Float y, Float r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = false;
    }

    public PointDTO(PointEntity pointEntity) {
        this.x = pointEntity.getX();
        this.y = pointEntity.getY();
        this.r = pointEntity.getR();
        this.hit = pointEntity.getHit();
    }

}
