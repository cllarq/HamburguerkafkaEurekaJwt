package com.coding.burguer_gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    // Llave secreta de mínimo 32 caracteres para firmar los tokens con HS256
    private static final String CLAVE_SECRETA = "EstaEsUnaClaveSuperSecretaParaBurguerCloud2026!";
    private static final long EXPIRACION_MS = 3600000; // 1 hora

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token JWT firmado para el usuario
    public String generarToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRACION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    // Valida si el token es auténtico y no ha expirado
    public String validarYExtraerUsuario(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
