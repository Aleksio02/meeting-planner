package ru.epta.mtplanner.auth.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ru.epta.commons.model.User;

@Component
public class JwtUtils {

    private static final String PRIVATE_KEY_PATH = "jwtRS256.pk8";
    private static final String KEY_TYPE = "RSA";
    private final PrivateKey privateKey;

    public JwtUtils() {
        this.privateKey = loadPrivateKey();
    }

    private PrivateKey loadPrivateKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_PATH)){

            if (inputStream == null) {
                throw new RuntimeException("Key not found in resources");
            }
            byte[] keyBytes = inputStream.readAllBytes();

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(KEY_TYPE);
            return kf.generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
