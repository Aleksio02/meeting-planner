package ru.epta.mtplanner.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.commons.model.User;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final String PRIVATE_KEY_PATH = "jwtRS256.pk8";
    private static final String PUBLIC_KEY_PATH = "jwtRS256.der";
    private static final String KEY_TYPE = "RSA";
    private static final long TOKEN_VALIDITY_MS = 3600000;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtils() {
        this.privateKey = loadPrivateKey();
        this.publicKey = loadPublicKey();
    }

    private PrivateKey loadPrivateKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("Private key not found in resources");
            }
            byte[] keyBytes = inputStream.readAllBytes();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(KEY_TYPE);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    private PublicKey loadPublicKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PUBLIC_KEY_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("Public key not found in resources");
            }
            byte[] keyBytes = inputStream.readAllBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(KEY_TYPE);
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error loading public key", e);
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
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_MS))
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}