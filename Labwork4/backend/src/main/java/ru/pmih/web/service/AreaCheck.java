package ru.pmih.web.service;

import jakarta.ejb.Stateless;

@Stateless
public class AreaCheck {

    public boolean check(float x, float y, float r) {
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= (r / 2.0) * (r / 2.0);
        }

        if (x <= 0 && y >= 0) {
            return x >= -r && y <= r / 2.0;
        }

        if (x <= 0 && y <= 0) {
            return y >= (-x / 2.0) - (r / 2.0);
        }

        return false;
    }
}