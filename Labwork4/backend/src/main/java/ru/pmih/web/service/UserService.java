package ru.pmih.web.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.pmih.web.entity.UserEntity;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "Points")
    private EntityManager em;

    public UserEntity register(String username, String password) {
        long count = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        if (count > 0) {
            return null;
        }

        UserEntity user = new UserEntity(username, hashPassword(password));
        em.persist(user);
        return user;
    }

    public UserEntity authenticate(String username, String password) {
        try {
            return em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username AND u.passwordHash = :hash", UserEntity.class)
                    .setParameter("username", username)
                    .setParameter("hash", hashPassword(password))
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity findById(Long id) {
        return em.find(UserEntity.class, id);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hash));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}