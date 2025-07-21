package com.library.booktrackingsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//DuplicateResourceException ile üretilmeye çalışılan kaynağın zaten mevcut olduğu belirtilir ve hata mesajı verilir.
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
