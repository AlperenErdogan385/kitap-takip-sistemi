package com.library.booktrackingsystem.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//AuthResponse ile kimlik doğrulamasından geçip sunucudan istemciye gönderilen JWT taşınır.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String jwtToken;
}
