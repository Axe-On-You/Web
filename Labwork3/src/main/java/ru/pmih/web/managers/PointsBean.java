package ru.pmih.web.managers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.pmih.web.entity.PointEntity;
import ru.pmih.web.utils.exceptions.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Application-scoped Managed Bean для управления списком результатов
 */
@ApplicationScoped
public class PointsBean {
    private final List<PointEntity> pointsCache = new CopyOnWriteArrayList<>();

    @Inject
    PointsRepository pointsRepository;

    @PostConstruct
    void init() {
        refresh();
    }

    public List<PointEntity> getAll() {
        return Collections.unmodifiableList(pointsCache);
    }

    public synchronized void refresh() {
        List<PointEntity> fresh = pointsRepository.getAllCreatedAtDesc();
        pointsCache.clear();
        pointsCache.addAll(fresh);
    }

    public void add(PointEntity p) throws ValidationError {
        pointsRepository.save(p);
        pointsCache.add(p);
    }

    public void addAll(List<PointEntity> points) throws ValidationError {
        points.forEach(this::add);
    }
}
