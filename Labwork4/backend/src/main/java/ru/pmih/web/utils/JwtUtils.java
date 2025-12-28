package ru.pmih.web.utils;

import jakarta.enterprise.context.ApplicationScoped;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Простая реализация подписи токенов (HMAC SHA256) без лишних библиотек.
 * Токен имеет формат: USER_ID.SIGNATURE
 */
@ApplicationScoped
public class JwtUtils {
    // В реальном проекте это должно быть в конфиге/переменных окружения
    private static final String SECRET = "MY_SUPER_SECRET_KEY_FOR_LAB_4_ITMO_2025";
    private static final String ALGORITHM = "HmacSHA256";

    public String generateToken(Long userId) {
        String payload = userId.toString();
        String signature = sign(payload);
        return Base64.getEncoder().encodeToString((payload + "." + signature).getBytes(StandardCharsets.UTF_8));
    }

    public Long verifyToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\.");
            if (parts.length != 2) return null;

            String payload = parts[0];
            String signature = parts[1];

            if (sign(payload).equals(signature)) {
                return Long.parseLong(payload);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacData);
        } catch (Exception e) {
            throw new RuntimeException("Error signing token", e);
        }
    }
}