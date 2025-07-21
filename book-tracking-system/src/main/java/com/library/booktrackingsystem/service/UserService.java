package com.library.booktrackingsystem.service;

import com.library.booktrackingsystem.entity.User;
import com.library.booktrackingsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections; // Collections.singletonList için
import java.util.HashSet;     // HashSet için
import java.util.Arrays;      // Arrays.asList için

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Yeni kullanıcı kaydı metodu (ROLE_USER rolünü atar)
    public User registerNewUser(User user) {
        // Kullanıcı adı zaten mevcut mu kontrolü
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Kullanıcı adı zaten kullanılıyor.");
        }
        // Şifre kaydetmeden önce şifrelenir
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Varsayılan olarak ROLE_USER rolünü ata
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));
        return userRepository.save(user); // Kullanıcıyı veritabanına kaydeder
    }

    // Admin kullanıcı oluşturmak için özel bir metot
    // Bu metot, uygulama başlangıcında veya özel bir Admin kaydında kullanılabilir.
    public User createAdminUser(String username, String password) {
        // Eğer kullanıcı zaten varsa, tekrar oluşturmamıza gerek yoktur
        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("Admin kullanıcı '" + username + "' zaten mevcut.");
            return userRepository.findByUsername(username).get();
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        // Admin kullanıcısına hem USER hem de ADMIN rolünü ata
        admin.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        userRepository.save(admin);
        System.out.println("Admin kullanıcı oluşturuldu: " + username);
        return admin;
    }


    @Override
    //Kullanıcı adına göre kullanıcı detaylarını yükler
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "kullanıcı adına sahip kullanıcı bulunamadı"));
    }
}