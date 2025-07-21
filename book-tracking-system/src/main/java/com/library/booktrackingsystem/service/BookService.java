package com.library.booktrackingsystem.service;

import com.library.booktrackingsystem.entity.Book;
import com.library.booktrackingsystem.entity.User;
import com.library.booktrackingsystem.exception.DuplicateResourceException;
import com.library.booktrackingsystem.exception.ResourceNotFoundException;
import com.library.booktrackingsystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page; // Page import'u
import org.springframework.data.domain.Pageable; // Pageable import'u


import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Tüm kitapları listele (Şu an tüm kitapları listeler, kullanıcıya özel değil)
    // Eğer sadece kullanıcının kendi kitaplarını listelemek istiyorsak bundan sonraki metodu kullanmalıyız
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Belirli bir kullanıcıya ait tüm kitapları listele
    public List<Book> getAllBooksByUser(User user) {
        return bookRepository.findByUser(user);
    }

    // Belirli bir kullanıcıya ait tüm kitapları sayfalama ve sıralama destekli listelemeyi sağlayan metot
    public Page<Book> getAllBooksByUser(User user, Pageable pageable) {
        return bookRepository.findByUser(user, pageable);
    }

    // ID'ye göre kitap getir
    public Book getBookByIdAndUser(Long id, User user) {
        return bookRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Bu kullanıcı için" + id + " numaralı id'ye sahip kitap bulunamadı."));
    }

    // Yeni kitap ekle (Mevcut kullanıcıyı atar)
    public Book addBook(Book book, User user) {
        // ISBN'nin benzersizliğini kontrol et (globale benzersizlik)
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("ISBN'si" + book.getIsbn() + " olan kitap zaten mevcut.");
        }
        book.setUser(user); // Kitabı mevcut kullanıcıya ata
        return bookRepository.save(book);
    }

    // Kitap güncelle
    public Book updateBook(Long id, Book bookDetails, User user) {
        Book book = bookRepository.findByIdAndUser(id, user) // Kullanıcıya ait kitabı bul
                .orElseThrow(() -> new ResourceNotFoundException("Bu kullanıcı için" + id + " numaralı id'ye sahip kitap bulunamadı."));

        // Güncelleme sırasında ISBN'nin başka bir kitapta zaten kullanılıp kullanılmadığını kontrol et
        Optional<Book> existingBookWithIsbn = bookRepository.findByIsbn(bookDetails.getIsbn());
        if (existingBookWithIsbn.isPresent() && !existingBookWithIsbn.get().getId().equals(id)) {
            throw new DuplicateResourceException("ISBN'si " + bookDetails.getIsbn() + " olan kitap zaten mevcut.");
        }

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublicationYear(bookDetails.getPublicationYear());
        return bookRepository.save(book);
    }

    // Kitap sil
    public void deleteBook(Long id, User user) {
        Book book = bookRepository.findByIdAndUser(id, user) // Kullanıcıya ait kitabı bul
                .orElseThrow(() -> new ResourceNotFoundException("Bu kullanıcı için" + id + " numaralı id'ye sahip kitap bulunamadı."));
        bookRepository.delete(book);
    }
}