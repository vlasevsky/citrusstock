package com.citrusmall.citrusstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class CitrusstockApplication {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String password = "12345";
		String encodedPassword = encoder.encode(password);
		System.out.println("Вставить этот пароль в базу, чтобы подошел пароль '" + password + "': " + encodedPassword);

		SpringApplication.run(CitrusstockApplication.class, args);

	}

}
 