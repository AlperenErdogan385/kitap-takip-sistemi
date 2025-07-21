package com.library.booktrackingsystem.controller;

import com.library.booktrackingsystem.dto.AuthRequest;
import com.library.booktrackingsystem.dto.AuthResponse;
import com.library.booktrackingsystem.entity.User;
import com.library.booktrackingsystem.security.JWTUtil;
import com.library.booktrackingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Tüm endpoint'ler /api/auth ile başlayacak
public class AuthController {

    @Autowired
    private UserService userService; // Kullanıcı kayıt işlemleri için burayı kullanıyoruz.

    @Autowired
    private JWTUtil jwtUtil; // JWT token işlemleri için burayı kullanıyoruz.

    @Autowired
    private AuthenticationManager authenticationManager; // Spring Security kimlik doğrulama yöneticisi

    // Kullanıcı kayıt endpoint'i
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerNewUser(user);
            // Kayıt başarılıysa, kullanıcıya bir token döndürebiliriz veya sadece başarılı mesajı verebiliriz.
            return ResponseEntity.status(HttpStatus.CREATED).body("Kullanıcı başarıyla kaydedildi: " + registeredUser.getUsername());
        } catch (RuntimeException e) {
            // Kullanıcı adı zaten kullanımda gibi hataları yakalıyoruz
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Kullanıcı giriş endpoint'i
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Spring Security'nin AuthenticationManager'ını kullanarak kimlik doğrulama yapıyoruz.

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            // Kimlik doğrulama başarısız olursa (yanlış kullanıcı adı/şifre)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yanlış kullanıcı adı veya şifre.");
        }

        // Kimlik doğrulama başarılıysa, UserDetails yükleniyor ve JWT token oluşturuluyor.
        final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Token yanıt olarak döndürülüyor.
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
