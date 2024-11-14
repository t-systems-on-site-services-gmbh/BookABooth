package de.tsystems.onsite.bookabooth.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidEmailException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;

    public InvalidEmailException() {
        super(HttpStatus.BAD_REQUEST, "Incorrect Email");
    }

    public InvalidEmailException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
