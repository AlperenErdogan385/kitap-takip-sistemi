package com.library.booktrackingsystem.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Bu import kalsın
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    // UserDetailsService'i burada @Autowired yapmayacağız,
    // çünkü SecurityConfig'ten argüman olarak alacağız.
    // Ancak metodun dışından enjekte edilmesi için bir setter veya constructor'a ihtiyacımız var.
    private UserDetailsService userDetailsService;

    // UserDetailsService'i dışarıdan enjekte etmek için constructor ekleyelim
    public JWTFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Default constructor (Spring bean oluştururken kullanabilir)
    public JWTFilter() {
        // Bu constructor boş kalabilir, veya Spring'in argümanlı constructor'ı tercih etmesini bekleyebiliriz.
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        //Hata yakalama komutları
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                System.out.println("JWT Token adı alınamadı.");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token süresi doldu.");
            } catch (SignatureException e) {
                System.out.println("Geçersiz JWT İmzası.");
            } catch (MalformedJwtException e) {
                System.out.println("DEBUG: MalformedJwtException - Geçersiz JWT Token yapısı. Header: " + authorizationHeader + " | Token: " + jwt + " | Hata: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.out.println("Desteklenmeyen JWT Token.");
            }
        }

        //Kullanıcı doğrulanır ve Spring Security'e eklenir
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            //JWT doğruysa yeni token oluşturulur,token'a isteğin detayları da eklenir
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        //filtre zinciri devam ettirilir
        filterChain.doFilter(request, response);
    }
}