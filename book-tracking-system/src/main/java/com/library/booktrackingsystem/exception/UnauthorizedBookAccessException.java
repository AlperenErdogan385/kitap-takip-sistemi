package com.library.booktrackingsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//UnauthorizedBookAccessException kullanıcının bir kitaba erişmeye çalıştığında bu işlemi yapmaya yetkisinin olmadığını belirtir ve hata mesajı döndürür.
@ResponseStatus(HttpStatus.FORBIDDEN) // Otomatik olarak 403 döndürülmesini sağlar
public class UnauthorizedBookAccessException extends RuntimeException {
    public UnauthorizedBookAccessException(String message) {
        super(message);
    }
}
