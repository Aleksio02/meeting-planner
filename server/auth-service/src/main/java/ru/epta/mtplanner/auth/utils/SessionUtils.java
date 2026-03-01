package ru.epta.mtplanner.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.commons.model.TokenPayload;

@Component
public class SessionUtils {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public SessionUtils(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String createSession(UUID userId) {
        String sessionId = generateId();
        TokenPayload tokenPayload = new TokenPayload(userId);
        redisTemplate.opsForValue().set(sessionId, writeAsString(tokenPayload), 3600, TimeUnit.SECONDS);
        return sessionId;
    }

    public TokenPayload getSession(String sessionId) {
        return readAsTokenPayload(redisTemplate.opsForValue().get(sessionId));
    }

    public TokenPayload extendSession(String sessionId) {
        TokenPayload tokenPayload = getSession(sessionId);
        tokenPayload.setExpires(Instant.now().plusSeconds(3600));
        deleteSession(sessionId);
        redisTemplate.opsForValue().set(
            sessionId,
            writeAsString(tokenPayload),
            3600
        );
        return tokenPayload;
    }

    public void deleteSession(String sessionId) {
        redisTemplate.opsForValue().getAndDelete(sessionId);
    }

    private String generateId() {
        byte[] randomBytes = new byte[32]; // 32 байта = 256 бит
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private String writeAsString(TokenPayload tokenPayload) {
        try {
            return objectMapper.writeValueAsString(tokenPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    private TokenPayload readAsTokenPayload(String rawJson) {
        try {
            return objectMapper.readValue(rawJson.replace("\u0000", ""), TokenPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
