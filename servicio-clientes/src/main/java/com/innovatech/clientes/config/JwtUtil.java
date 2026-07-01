package com.innovatech.clientes.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Llave secreta en duro para la PoC (en prod debería venir de variables de entorno)
    private static final String SECRET_KEY = "InnovaTechSecretKeyForJwtAuthenticationSuperSecure";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    public String generateToken(String email, String dni, String nombre) {
        return Jwts.builder()
                .setSubject(email)
                .claim("dni", dni)
                .claim("nombre", nombre)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
