package com.library.booktrackingsystem.controller;

import com.library.booktrackingsystem.entity.Book;
import com.library.booktrackingsystem.entity.User;
import com.library.booktrackingsystem.service.BookService;
import com.library.booktrackingsystem.dto.BookDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kitaplar")
public class BookController {

    @Autowired
    private BookService bookService;

    //  Kullanıcıya ait tüm kitapları listeleme
    @Operation(summary = "Sayfalama ve sıralamayla doğrulanmış kullanıcıya ait tüm kitapları listele", description = "Doğrulanmış kullanıcıya ait tüm kitapları sayfalama ve sıralama yollarını kullanarak döndürür.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitaplar başarıyla listelendi.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class, subTypes = {BookDTO.class}))),
            @ApiResponse(responseCode = "401", description = "Doğrulanmamış- JWT token kayıp ya da geçersiz")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<BookDTO>> getAllBooksForUser(
                                                             @AuthenticationPrincipal User currentUser,
                                                             @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        // BookService'in Pageable parametresi alan metodunu çağırıyoruz
        Page<Book> bookPage = bookService.getAllBooksByUser(currentUser, pageable);

        // Book Page objesini BookDTO Page objesine dönüştürüyoruz
        Page<BookDTO> bookDTOPage = bookPage.map(BookDTO::new);

        return ResponseEntity.ok(bookDTOPage);
    }


    // Tek bir kitabın ID'sinden yararlanarak onu listeleme
    @Operation(summary = "Kitap ID'sini kullanarak doğrulanmış kullanıcının kitabını listele.", description = "ID'sinden yararlanarak tek bir kitabı döndürür ve kitabın doğrulanmış kullanıcıya ait olduğunu kanıtlar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitap başarıyla döndürüldü",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))), // Şema BookDTO olarak güncelleniyor
            @ApiResponse(responseCode = "404", description = "Bu kullanıcı için kitap bulunamadı"),
            @ApiResponse(responseCode = "401", description = "Doğrulanmamış- JWT Token kayıp ya da geçersiz"),
            @ApiResponse(responseCode = "403", description = "İzinsiz kullanıcı")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Book book = bookService.getBookByIdAndUser(id, currentUser);
        return ResponseEntity.ok(new BookDTO(book)); // BookDTO döndürülüyor
    }

    //Kitap ekleme - POST /api/kitaplar
    @Operation(summary = "Doğrulanmış kullanıcı için yeni bir kitap ekle", description = "Yeni bir kitap ekler ve onu doğrulanmış kullanıcıya atar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kitap başarıyla oluşturuldu",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))), // Şemayı BookDTO olarak güncelleyin
            @ApiResponse(responseCode = "400", description = "Geçersiz kitap bilgisi girildi"),
            @ApiResponse(responseCode = "409", description = "Bu ISBN'e sahip bir kitap zaten mevcut"),
            @ApiResponse(responseCode = "401", description = "Doğrulanmamış- JWT Token kayıp ya da geçersiz"),
            @ApiResponse(responseCode = "403", description = "İzinsiz kullanıcı")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody Book book, @AuthenticationPrincipal User currentUser) {
        Book createdBook = bookService.addBook(book, currentUser);
        return new ResponseEntity<>(new BookDTO(createdBook), HttpStatus.CREATED); // BookDTO döndürün
    }

    //Kitap güncelleme - PUT /api/kitaplar/{id}
    @Operation(summary = "Doğrulanmış kullanıcı için halihazırda varolan bir kitabı güncelle", description = "Varolan bir kitabın ID'sini kullanarak onun detaylarını günceller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kitap başarıyla güncellendi",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))), // Şemayı BookDTO olarak güncelleyin
            @ApiResponse(responseCode = "400", description = "Geçersiz kitap bilgisi girildi"),
            @ApiResponse(responseCode = "404", description = "Bu kullanıcı için kitap bulunamadı"),
            @ApiResponse(responseCode = "409", description = "Bu ISBN'e sahip bir kitap halihazırda bulunuyor"),
            @ApiResponse(responseCode = "401", description = "Doğrulanmamış- JWT Token kayıp ya da geçersiz"),
            @ApiResponse(responseCode = "403", description = "İzinsiz kullanıcı")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails, @AuthenticationPrincipal User currentUser) {
        Book updatedBook = bookService.updateBook(id, bookDetails, currentUser);
        return ResponseEntity.ok(new BookDTO(updatedBook)); // BookDTO döndürülüyor
    }

    //Kitap silme - DELETE /api/kitaplar/{id}
    @Operation(summary = "Doğrulanmış kullanıcı için bir kitap sil", description = "Doğrulanmış kullanıcıya ait bir kitabı ID'sinden yararlanarak sistemden siler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Kitap başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Bu kullanıcı için kitap bulunamadı"),
            @ApiResponse(responseCode = "401", description = "Doğrulanmamış-JWT Token kayıp ya da geçersiz"),
            @ApiResponse(responseCode = "403", description = "İzinsiz kullanıcı")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        bookService.deleteBook(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}