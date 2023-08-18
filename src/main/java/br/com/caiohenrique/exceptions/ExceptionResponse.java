package br.com.caiohenrique.exceptions;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ExceptionResponse
 */
public class ExceptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalDateTime timestamp;
    private String message;
    private String details;

    // Constructor
    public ExceptionResponse(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

}
