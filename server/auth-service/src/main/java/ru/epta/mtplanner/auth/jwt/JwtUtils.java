package ru.epta.mtplanner.auth.jwt;

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

@Component
public class JwtUtils {

    private static final String privateKeyPath = "jwtRS256.pk8";
    private final PrivateKey privateKey;

    public JwtUtils() {
        this.privateKey = loadPrivateKey();
    }

    private PrivateKey loadPrivateKey() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(privateKeyPath);
            if (inputStream == null) {
                throw new RuntimeException("Key not found in resources");
            }
            byte[] keyBytes = inputStream.readAllBytes();

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    public String generateToken(UUID id, String username, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
