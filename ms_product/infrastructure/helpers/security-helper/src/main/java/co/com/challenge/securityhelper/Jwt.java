package co.com.challenge.securityhelper;

import co.com.challenge.model.utils.interfaces.JwtInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class Jwt implements JwtInterface {
    private final String claveSuperSecreta = "claveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecretaclaveSecreta";

    @Override
    public String generateToken() {
        var key = Keys.hmacShaKeyFor(claveSuperSecreta.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject("challenge")
                .claim("email", "challenge@correo.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    @Override
    public boolean validateToken(String token) {
        var key = Keys.hmacShaKeyFor(claveSuperSecreta.getBytes(StandardCharsets.UTF_8));
        try {
            var validate = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return validate != null && !validate.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}