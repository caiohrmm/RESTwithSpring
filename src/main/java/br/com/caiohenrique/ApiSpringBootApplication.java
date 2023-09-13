package br.com.caiohenrique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ApiSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiSpringBootApplication.class, args);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String senha = passwordEncoder.encode("admin123");

        System.out.println(senha);

    }

}
