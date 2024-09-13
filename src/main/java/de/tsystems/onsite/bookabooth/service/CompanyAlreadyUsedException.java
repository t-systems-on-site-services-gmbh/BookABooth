package de.tsystems.onsite.bookabooth.service;

public class CompanyAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompanyAlreadyUsedException() {
        super("Company name already used!");
    }
}
