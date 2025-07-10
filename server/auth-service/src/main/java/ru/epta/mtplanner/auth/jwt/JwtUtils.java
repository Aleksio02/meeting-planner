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

@Component
public class JwtUtils {

    private PrivateKey loadPrivateKey() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jwtRS256.pk8");
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

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(loadPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }
}
