package br.com.caiohenrique.exceptions.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import br.com.caiohenrique.exceptions.ExceptionResponse;
import br.com.caiohenrique.exceptions.UnsupportedMathOperationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Concentrar tratamento espalhado em todos os controllers.
@RestController
@ControllerAdvice

public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    // Método para tratar excecoes do Java e do REST.


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(UnsupportedMathOperationException.class)
    @ResponseBody
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}

