package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.security.AccountCredentialsVO;
import br.com.caiohenrique.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("ALL")
@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
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
        return token;
    }

    @Operation(
            summary = "Refresh token for authenticated users. "
    )
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {
        if (checkIsNull(username, refreshToken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request !");

        var token = authService.refreshToken(username, refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request !");
        }
        return token;
    }

    private static boolean checkIsNull(String username, String refreshToken) {
        return username == null || username.isBlank() || refreshToken.isBlank() || refreshToken == null;
    }

    // Método que valida se o conteudo que chega no body é nulo.
    private static boolean checkIsNull(AccountCredentialsVO data) {
        return data == null || data.getUsername() == null || data.getUsername().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }
}
