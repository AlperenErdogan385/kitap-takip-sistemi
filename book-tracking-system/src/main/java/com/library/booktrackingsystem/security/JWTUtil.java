package com.library.booktrackingsystem.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtil {

    // JWT imzalama anahtarını application.properties dosyasından alırız.
    @Value("${jwt.secret}")
    private String secret;

    // JWT token ömrünü application.properties dosyasından alırız.
    @Value("${jwt.expiration}")
    private long expiration;

    // Token'dan kullanıcı adını ayıklama
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Token'dan belirli bir claim'i (talep) ayıklama
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Token'daki tüm claim'leri ayıklama
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey()) // İmzalama anahtarını kullan
                .build()
                .parseClaimsJws(token) // Token'ı ayrıştır
                .getBody(); // Claim'leri al
    }

    // Token'ın süresinin dolup dolmadığını kontrol etme
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Token'dan son kullanma tarihini ayıklama
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Kullanıcı detaylarına göre token doğrulaması
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Token oluşturma
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Token'ı gerekli claim'lerle ve imzalama anahtarıyla oluşturma
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims) // Claim'leri ayarla
                .setSubject(userName) // Kullanıcı adını konu olarak ayarla
                .setIssuedAt(new Date(System.currentTimeMillis())) // Oluşturulma tarihi
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Son kullanma tarihi
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // İmzalama algoritması ve anahtar
                .compact(); // Token'ı sıkıştır ve string olarak döndür
    }

    // İmzalama anahtarı base64encoded secret'tan alınır
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
