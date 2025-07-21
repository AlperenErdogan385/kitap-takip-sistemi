package com.library.booktrackingsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//AuthRequest'in amacı kimlik doğrulaması için gerekli olan kullanıcı adı ve şifre bilgilerini taşımaktır.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
