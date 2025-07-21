package com.library.booktrackingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "kitaplar")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kitap başlığı boş bırakılamaz")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Yazar adı boş bırakılamaz")
    @Column(nullable = false)
    private String author;

    @NotNull(message = "ISBN null bırakılamaz")
    @NotBlank(message = "ISBN boş bırakılamaz")
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Yayınlanma yılı boş bırakılamaz")
    @Min(value = 1000, message = "Yayınlanma yılı geçerli bir yıl olmalıdır")
    private Integer publicationYear;


    @ManyToOne(fetch = FetchType.LAZY) // Birçok kitap bir kullanıcıya ait olabilir
    @JoinColumn(name = "kullanici_id") // Veritabanındaki foreign key sütun adı
    private User user; // İlişkili User nesnesi

    // Constructors
    public Book() {
    }

    // Kitap için Constructor (User ile birlikte)
    public Book(String title, String author, String isbn, Integer publicationYear, User user) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.user = user;
    }

    public Book(String title, String author, String isbn, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }

    // Getter ve Setterlar
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    //Getter ve Setter (User için)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Kitap{" +
                "id=" + id +
                ", başlık='" + title + '\'' +
                ", yazar='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", yayınlanmaYılı=" + publicationYear +
                ", kullanıcı=" + (user != null ? user.getUsername() : "null") + // User nesnesinin döngüye girmemesi için
                '}';
    }
}