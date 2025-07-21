package com.library.booktrackingsystem.repository;

import com.library.booktrackingsystem.entity.Book;
import com.library.booktrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;     // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Kullanıcıya ait tüm kitapları görmek için bu komutu kullanıyoruz
    List<Book> findByUser(User user);

    //Kullanıcıya ait tüm kitapları sayfalama ve sıralamaya bağlı olarak görmek için aşağıdaki komutları kullanıyoruz
    Page<Book> findByUser(User user, Pageable pageable);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIdAndUser(Long id, User user);
}