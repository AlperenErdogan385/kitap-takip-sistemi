package com.library.booktrackingsystem.dto;

import com.library.booktrackingsystem.entity.Book; // Book entity'sini import ediyoruz

public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private String username; // Book ile ilişkili kullanıcının adını ve özelliklerini göstermek için bunları kullanıyoruz

    // Book entity'sinden BookDTO oluşturan constructor
    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.publicationYear = book.getPublicationYear();
        // Kullanıcı null değilse ve Hibernate proxy'si tetiklenmemişse kullanıcı adını alın
        // Bu kontrol, proxy hatasını önlemeye yardımcı olur
        if (book.getUser() != null) {
            this.username = book.getUser().getUsername();
        }
    }

    // Getterlar
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getUsername() {
        return username;
    }

    // Setterlar
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
