package de.tsystems.onsite.bookabooth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.tsystems.onsite.bookabooth.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Company.
 */
@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Company implements Serializable {

    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 200)
    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "billing_address_row1")
    private String billingAddressRow1;

    @Column(name = "billing_address_row2")
    private String billingAddressRow2;

    @Column(name = "billing_address_row3")
    private String billingAddressRow3;

    @Column(name = "billing_address_row4")
    private String billingAddressRow4;

    @Column(name = "billing_zip_code")
    private String billingZipCode;

    @Column(name = "billing_city")
    private String billingCity;

    @Column(name = "comment", length = 1024)
    private String comment;

    @Column(name = "logo")
    private String logo;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "waiting_list")
    private Boolean waitingList;

    @Column(name = "exhibitor_list")
    private Boolean exhibitorList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "company", "booth" }, allowSetters = true)
    private Set<Booking> bookings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Company id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Company name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingAddressRow1() {
        return this.billingAddressRow1;
    }

    public Company billingAddressRow1(String billingAddressRow1) {
        this.setBillingAddressRow1(billingAddressRow1);
        return this;
    }

    public void setBillingAddressRow1(String billingAddressRow1) {
        this.billingAddressRow1 = billingAddressRow1;
    }

    public String getBillingAddressRow2() {
        return this.billingAddressRow2;
    }

    public Company billingAddressRow2(String billingAddressRow2) {
        this.setBillingAddressRow2(billingAddressRow2);
        return this;
    }

    public void setBillingAddressRow2(String billingAddressRow2) {
        this.billingAddressRow2 = billingAddressRow2;
    }

    public String getBillingAddressRow3() {
        return this.billingAddressRow3;
    }

    public Company billingAddressRow3(String billingAddressRow3) {
        this.setBillingAddressRow3(billingAddressRow3);
        return this;
    }

    public void setBillingAddressRow3(String billingAddressRow3) {
        this.billingAddressRow3 = billingAddressRow3;
    }

    public String getBillingAddressRow4() {
        return this.billingAddressRow4;
    }

    public Company billingAddressRow4(String billingAddressRow4) {
        this.setBillingAddressRow4(billingAddressRow4);
        return this;
    }

    public void setBillingAddressRow4(String billingAddressRow4) {
        this.billingAddressRow4 = billingAddressRow4;
    }

    public String getBillingZipCode() {
        return this.billingZipCode;
    }

    public Company billingZipCode(String billingZipCode) {
        this.setBillingZipCode(billingZipCode);
        return this;
    }

    public void setBillingZipCode(String billingZipCode) {
        this.billingZipCode = billingZipCode;
    }

    public String getBillingCity() {
        return this.billingCity;
    }

    public Company billingCity(String billingCity) {
        this.setBillingCity(billingCity);
        return this;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public boolean hasBillingAddress() {
        return (
            StringUtils.isNotEmpty(this.billingAddressRow1) &&
            StringUtils.isNotEmpty(this.billingZipCode) &&
            StringUtils.isNotEmpty(this.billingCity)
        );
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLogo() {
        return this.logo;
    }

    public Company logo(String logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return this.description;
    }

    public Company description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getWaitingList() {
        return this.waitingList;
    }

    public Company waitingList(Boolean waitingList) {
        this.setWaitingList(waitingList);
        return this;
    }

    public void setWaitingList(Boolean waitingList) {
        this.waitingList = waitingList;
    }

    public Boolean getExhibitorList() {
        return this.exhibitorList;
    }

    public Company exhibitorList(Boolean exhibitorList) {
        this.setExhibitorList(exhibitorList);
        return this;
    }

    public void setExhibitorList(Boolean exhibitorList) {
        this.exhibitorList = exhibitorList;
    }

    public Set<Booking> getBookings() {
        return this.bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        if (this.bookings != null) {
            this.bookings.forEach(i -> i.setCompany(null));
        }
        if (bookings != null) {
            bookings.forEach(i -> i.setCompany(this));
        }
        this.bookings = bookings;
    }

    public Company bookings(Set<Booking> bookings) {
        this.setBookings(bookings);
        return this;
    }

    public Company addBookings(Booking booking) {
        this.bookings.add(booking);
        booking.setCompany(this);
        return this;
    }

    public Company removeBookings(Booking booking) {
        this.bookings.remove(booking);
        booking.setCompany(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return getId() != null && getId().equals(((Company) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Company{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", billingAddressRow1='" + getBillingAddressRow1() + "'" +
            ", billingAddressRow2='" + getBillingAddressRow2() + "'" +
            ", billingAddressRow3='" + getBillingAddressRow3() + "'" +
            ", billingAddressRow4='" + getBillingAddressRow4() + "'" +
            ", billingZipCode='" + getBillingZipCode() + "'" +
            ", billingCity='" + getBillingCity() + "'" +
            ", comment='" + getComment() + "'" +
            ", logo='" + getLogo() + "'" +
            ", description='" + getDescription() + "'" +
            ", waitingList='" + getWaitingList() + "'" +
            ", exhibitorList='" + getExhibitorList() + "'" +
            "}";
    }
}
