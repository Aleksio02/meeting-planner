package ru.epta.mtplanner.auth.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.auth.model.TokenPayload;

@Component
public class SessionUtils {

    private final RedisTemplate<String, TokenPayload> redisTemplate;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public SessionUtils(RedisTemplate<String, TokenPayload> redisTemplate) {this.redisTemplate = redisTemplate;}

    private String generateId() {
        byte[] randomBytes = new byte[32]; // 32 байта = 256 бит
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public String createSession(UUID userId) {
        String sessionId = generateId();
        TokenPayload tokenPayload = new TokenPayload(userId);
        redisTemplate.opsForValue().set(sessionId, tokenPayload, 3600, TimeUnit.SECONDS);
        return sessionId;
    }

    public TokenPayload getSession(String sessionId) {
        return redisTemplate.opsForValue().get(sessionId);
    }
}
