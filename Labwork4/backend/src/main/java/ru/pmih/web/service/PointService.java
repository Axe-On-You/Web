package ru.pmih.web.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.pmih.web.dto.PointDTO;
import ru.pmih.web.entity.PointEntity;
import ru.pmih.web.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PointService {

    @PersistenceContext(unitName = "Points")
    private EntityManager em;

    @Inject
    private AreaCheck areaCheck;

    public PointDTO addPoint(PointDTO dto, Long userId) {
        UserEntity user = em.find(UserEntity.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found");

        boolean hit = areaCheck.check(dto.getX(), dto.getY(), dto.getR());
        dto.setHit(hit);

        PointEntity entity = new PointEntity(dto, user);
        em.persist(entity);

        return new PointDTO(entity);
    }

    public List<PointDTO> getPoints(Long userId) {
        return em.createQuery("SELECT p FROM PointEntity p WHERE p.user.id = :uid ORDER BY p.createdAt DESC", PointEntity.class)
                .setParameter("uid", userId)
                .getResultList()
                .stream()
                .map(PointDTO::new)
                .collect(Collectors.toList());
    }
}