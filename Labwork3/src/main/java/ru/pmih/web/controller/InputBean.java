package ru.pmih.web.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.*;
import ru.pmih.web.dto.PointDTO;
import ru.pmih.web.entity.PointEntity;
import ru.pmih.web.managers.PointsBean;
import ru.pmih.web.utils.exceptions.ValidationError;
import ru.pmih.web.utils.validators.PointValidator;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Бин, отвечающий за данные в форме каждого пользователя
 */
@Data
@Named("inputBean")
@ViewScoped
public class InputBean implements Serializable {
    @Inject
    PointsBean pointsBean;
    @Inject
    PointValidator pointValidator;

    private Float x;
    private Float y;
    private List<String> selectedRValues;

    private Float maxR;

    @PostConstruct
    public void init() {
        selectedRValues = new ArrayList<>();
    }

    /**
     * Очистка полей ввода
     */
    public void clear() {
        x = null;
        y = null;
        selectedRValues.clear();
    }

    /**
     * Метод, который вызывает ajax-событие изменения радиуса
     */
    public void onRChange() {
        List<Float> floatSelectedR = selectedRValues.stream().map(Float::parseFloat).toList();
        this.maxR = floatSelectedR.stream().max(Float::compare).orElse(null);

        String rJson = new Gson().toJson(selectedRValues);

        PrimeFaces.current().ajax().addCallbackParam("selectedRJson", rJson);
    }

    /**
     * Отправка формы, проверка факта попадания точек и обновление кэша
     */
    public void check() {
        if (selectedRValues.isEmpty()) throw new ValidatorException(new FacesMessage("Ошибка валидации", "Валидация провалилась! "));

        List<PointDTO> pointDTOs = new CopyOnWriteArrayList<>();
        selectedRValues.forEach((r) -> pointDTOs.add(new PointDTO(x, y, Float.parseFloat(r))));
        addPoints(pointDTOs);
    }

    /**
     * Инкапсулированный метод для добавления точек через контроллер
     * @param pointDTOs массив из дто
     */
    private void addPoints(List<PointDTO> pointDTOs) {
        for (int i = 0; i < pointDTOs.size(); i++) {
            PointDTO currentPointDTO = pointDTOs.get(i);
            currentPointDTO.setHit(pointValidator.checkArea(currentPointDTO));
            pointDTOs.set(i, currentPointDTO);
        }

        List<PointEntity> pointEntities = new CopyOnWriteArrayList<>();
        pointDTOs.forEach(pointDTO -> pointEntities.add(new PointEntity(pointDTO)));
        try {
            pointsBean.addAll(pointEntities);
        } catch (ValidationError validationError) {
            FacesMessage facesMessage = new FacesMessage("Ошибка валидации", validationError.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        } finally {
            String pointsJsonRaw = new Gson().toJson(pointDTOs);
            PrimeFaces.current().ajax().addCallbackParam("pointsJsonRaw", pointsJsonRaw);
        }
    }

    public void addPointViaCanvas() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String x = facesContext.getExternalContext().getRequestParameterMap().get("x");
        String y = facesContext.getExternalContext().getRequestParameterMap().get("y");
        List<String> rList = (List<String>) new Gson().fromJson(facesContext.getExternalContext().getRequestParameterMap().get("rList"), List.class);

        if (rList == null || rList.isEmpty()) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Выберите хотя бы один радиус");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return;
        }

        List<PointDTO> pointDTOs = new CopyOnWriteArrayList<>();
        rList.forEach((r) -> pointDTOs.add(new PointDTO(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(r))));
        addPoints(pointDTOs);
    }

    public void addFacesMessage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String summary = facesContext.getExternalContext().getRequestParameterMap().get("summary");
        String detail = facesContext.getExternalContext().getRequestParameterMap().get("detail");
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public List<PointDTO> getReversedPoints() {
        List<PointDTO> points = new ArrayList<>(pointsBean.getAll().stream().map(PointDTO::new).toList());
        Collections.reverse(points);
        return points;
    }

    public String getAllPointsAsJson() {
        return new Gson().toJson(getReversedPoints());
    }
}
