package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.security.AccountCredentialsVO;
import br.com.caiohenrique.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("ALL")
@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/signin")
public class AuthController {

    private AuthService authService;

    @Operation(
            summary = "Authenticates a user and returns a token"
    )

    @PostMapping(value = "/signin")
    public ResponseEntity signIn(@RequestBody AccountCredentialsVO data) {
        if (checkIsNull(data)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request !");

        var token = authService.signIn(data);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request !");
        }

        return null;
    }

    // Método que valida se o conteudo que chega no body é nulo.
    private static boolean checkIsNull(AccountCredentialsVO data) {
        return data == null || data.getUsername() == null || data.getUsername().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }
}
