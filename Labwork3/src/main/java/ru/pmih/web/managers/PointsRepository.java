package ru.pmih.web.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ru.pmih.web.entity.PointEntity;
import ru.pmih.web.utils.exceptions.ValidationError;
import ru.pmih.web.utils.jpa.PointsPersistence;
import ru.pmih.web.utils.validators.PointValidator;

import java.util.List;


/**
 * Сервис для взаимодействия с БД
 */
@ApplicationScoped
public class PointsRepository implements PointsPersistence {
    @Inject
    PointValidator pointValidator;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void save(PointEntity p) throws ValidationError {
        if (!pointValidator.validate(p)) throw new ValidationError(p);
        if (p.getId() == null) em.persist(p);
        else em.merge(p);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        PointEntity p = em.find(PointEntity.class, id);
        if (p != null) em.remove(p);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PointEntity> getAllCreatedAtDesc() {
        return em.createQuery(
                "SELECT p from PointEntity p ORDER BY p.createdAt DESC",
                PointEntity.class
        ).getResultList();
    }
}
