package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.enumeration.ContactResponsibility;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.Contact} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContactDTO implements Serializable {

    private Long id;

    @Size(max = 200)
    private String firstName;

    @Size(max = 200)
    private String lastName;

    private String mail;

    @Size(max = 20)
    private String phone;

    private ContactResponsibility responsibility;

    @Lob
    private String note;

    private Set<CompanyDTO> companies = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactResponsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(ContactResponsibility responsibility) {
        this.responsibility = responsibility;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<CompanyDTO> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<CompanyDTO> companies) {
        this.companies = companies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactDTO)) {
            return false;
        }

        ContactDTO contactDTO = (ContactDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contactDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", mail='" + getMail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", responsibility='" + getResponsibility() + "'" +
            ", note='" + getNote() + "'" +
            ", companies=" + getCompanies() +
            "}";
    }
}
