package com.unilibre.vinos_licores.app.Ecommerce.security;

import com.unilibre.vinos_licores.app.Ecommerce.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        // Convertir secret string a una SecretKey válida
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("nombre", user.getNombre())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + 7200000)) // 2h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
