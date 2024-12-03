package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDTO {

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9!#$&'*+=?^_`{|}~.-]+@?[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    private String login;

    @NotNull
    @Size(min = 1, max = 100)
    private String companyName;

    @NotNull
    @Size(min = 5, max = 254)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @NotNull
    @Size(min = 4, max = 50)
    private String password;

    @NotNull
    private Boolean termsAccepted;

    public @NotNull @Size(min = 1, max = 50) @Pattern(
        regexp = "^[a-zA-Z0-9!#$&'*+=?^_`{|}~.-]+@?[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$"
    ) String getLogin() {
        return login;
    }

    public void setLogin(
        @NotNull @Size(min = 1, max = 50) @Pattern(
            regexp = "^[a-zA-Z0-9!#$&'*+=?^_`{|}~.-]+@?[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$"
        ) String login
    ) {
        this.login = login;
    }

    public @NotNull @Size(min = 1, max = 100) String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NotNull @Size(min = 1, max = 100) String companyName) {
        this.companyName = companyName;
    }

    public @NotNull @Size(min = 5, max = 254) @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @Size(min = 5, max = 254) @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") String email) {
        this.email = email;
    }

    public @NotNull Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(@NotNull Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public @NotNull @Size(min = 4, max = 50) String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @Size(min = 4, max = 50) String password) {
        this.password = password;
    }
}
