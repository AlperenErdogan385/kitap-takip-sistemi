package com.library.booktrackingsystem.repository;


import com.library.booktrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Temel CRUD (Create, Read, Update, Delete) operasyonlarını yapabiliriz.
// İlk parametre Entity sınıfı (User), ikinci parametre ID'sinin tipi (Long).
public interface UserRepository extends JpaRepository<User, Long> {
    // Kullanıcı adıyla kullanıcı bulmak için özel bir metod. Spring Data JPA bunu otomatik olarak uygular.
    Optional<User> findByUsername(String username);

    // Kullanıcı adının var olup olmadığını kontrol etmek için bunu kullanıyoruz
    Boolean existsByUsername(String username);
}
