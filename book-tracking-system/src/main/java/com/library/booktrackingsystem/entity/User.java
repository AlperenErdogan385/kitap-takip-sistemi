package com.library.booktrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // JacksonIgnore için ekledim
import jakarta.persistence.*;
import lombok.AllArgsConstructor; // Lombok
import lombok.Builder;             // Lombok
import lombok.Data;                // Lombok
import lombok.NoArgsConstructor;   // Lombok
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data // Lombok ile getter, setter, equals, hashCode, toString
@NoArgsConstructor // Lombok ile boş constructor
@AllArgsConstructor // Lombok ile tüm argümanlı constructor
@Builder // Lombok ile Builder pattern
@Entity
@Table(name = "kullanicilar")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Kullanıcının rolleri
    @ElementCollection(fetch = FetchType.EAGER) // Rolleri kullanıcı ile birlikte yükle
    @CollectionTable(name = "kullanici_rolleri", joinColumns = @JoinColumn(name = "kullanici_id"))
    @Column(name = "rol") // Rol değerinin saklanacağı sütun adı
    private Set<String> roles = new HashSet<>(); // Varsayılan olarak boş bir set, Lombok ile NoArgsConstructor'da da çalışır

    // Lombok kullandığımız için bazı constructor'lar ve getter/setter'lar otomatik oluşuyor.




    @JsonIgnore // Bu alanı JSON yanıtlarında gizliyor
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Kullanıcının sahip olduğu String rollerini SimpleGrantedAuthority nesnelerine dönüştürür.
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Lombok'tan gelen @Data anotasyonu bunları otomatik olarak sağlar
    // @Override public String getPassword() { return password; }
    // @Override public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}