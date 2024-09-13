package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.BoothUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoothUserDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String phone;

    @Lob
    private String note;

    private UUID verificationCode;

    private ZonedDateTime verified;

    private ZonedDateTime lastLogin;

    private Boolean disabled;

    private UserDTO user;

    private CompanyDTO company;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(UUID verificationCode) {
        this.verificationCode = verificationCode;
    }

    public ZonedDateTime getVerified() {
        return verified;
    }

    public void setVerified(ZonedDateTime verified) {
        this.verified = verified;
    }

    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoothUserDTO)) {
            return false;
        }

        BoothUserDTO boothUserDTO = (BoothUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, boothUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoothUserDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", note='" + getNote() + "'" +
            ", verificationCode='" + getVerificationCode() + "'" +
            ", verified='" + getVerified() + "'" +
            ", lastLogin='" + getLastLogin() + "'" +
            ", disabled='" + getDisabled() + "'" +
            ", user=" + getUser() +
            ", company=" + getCompany() +
            "}";
    }
}
