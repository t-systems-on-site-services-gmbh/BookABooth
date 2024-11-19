package de.tsystems.onsite.bookabooth.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CompanyAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompanyAlreadyUsedException() {
        super("Company name already used!");
    }
}