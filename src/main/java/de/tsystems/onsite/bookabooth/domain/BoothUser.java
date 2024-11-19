package de.tsystems.onsite.bookabooth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A BoothUser.
 */
@Entity
@Table(name = "booth_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoothUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Lob
    @Column(name = "note")
    private String note;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "verification_code", length = 36)
    private UUID verificationCode;

    @Column(name = "verified")
    private ZonedDateTime verified;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Column(name = "disabled")
    private Boolean disabled;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bookings", "departments", "contacts" }, allowSetters = true)
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BoothUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public BoothUser phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return this.note;
    }

    public BoothUser note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getVerificationCode() {
        return this.verificationCode;
    }

    public BoothUser verificationCode(UUID verificationCode) {
        this.setVerificationCode(verificationCode);
        return this;
    }

    public void setVerificationCode(UUID verificationCode) {
        this.verificationCode = verificationCode;
    }

    public ZonedDateTime getVerified() {
        return this.verified;
    }

    public BoothUser verified(ZonedDateTime verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(ZonedDateTime verified) {
        this.verified = verified;
    }

    public ZonedDateTime getLastLogin() {
        return this.lastLogin;
    }

    public BoothUser lastLogin(ZonedDateTime lastLogin) {
        this.setLastLogin(lastLogin);
        return this;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }

    public BoothUser disabled(Boolean disabled) {
        this.setDisabled(disabled);
        return this;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BoothUser user(User user) {
        this.setUser(user);
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public BoothUser company(Company company) {
        this.setCompany(company);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoothUser)) {
            return false;
        }
        return getId() != null && getId().equals(((BoothUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoothUser{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", note='" + getNote() + "'" +
            ", verificationCode='" + getVerificationCode() + "'" +
            ", verified='" + getVerified() + "'" +
            ", lastLogin='" + getLastLogin() + "'" +
            ", disabled='" + getDisabled() + "'" +
            "}";
    }

    public boolean isVerified() {
        // this.getuser().
        return false; // todo logic here
    }
}
