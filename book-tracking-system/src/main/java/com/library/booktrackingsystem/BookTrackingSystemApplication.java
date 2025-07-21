package com.library.booktrackingsystem;

import com.library.booktrackingsystem.service.UserService; // UserService'i import ediyoruz
import org.springframework.boot.CommandLineRunner; // CommandLineRunner'ı import ediyoruz
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // @Bean anotasyonu için import ediyoruz

@SpringBootApplication
public class BookTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookTrackingSystemApplication.class, args);
	}

	// Uygulama başladığında çalışacak test metodu(Admin kullanıcı oluşturmak için kullanıyoruz)
	@Bean
	public CommandLineRunner run(UserService userService) {
		return args -> {

			userService.createAdminUser("testuser", "Test123!");
		};
	}
}