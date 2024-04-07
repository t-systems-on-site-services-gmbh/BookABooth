package de.tsystems.onsite.bookabooth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 200)
    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "mail")
    private String mail;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "logo")
    private String logo;

    @Column(name = "description")
    private String description;

    @Column(name = "waiting_list")
    private Boolean waitingList;

    @Column(name = "exhibitor_list")
    private Boolean exhibitorList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "company", "booth" }, allowSetters = true)
    private Set<Booking> bookings = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "companies")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "companies" }, allowSetters = true)
    private Set<Department> departments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "companies")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "companies" }, allowSetters = true)
    private Set<Contact> contacts = new HashSet<>();

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

    public String getMail() {
        return this.mail;
    }

    public Company mail(String mail) {
        this.setMail(mail);
        return this;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBillingAddress() {
        return this.billingAddress;
    }

    public Company billingAddress(String billingAddress) {
        this.setBillingAddress(billingAddress);
        return this;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
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

    public Set<Department> getDepartments() {
        return this.departments;
    }

    public void setDepartments(Set<Department> departments) {
        if (this.departments != null) {
            this.departments.forEach(i -> i.removeCompany(this));
        }
        if (departments != null) {
            departments.forEach(i -> i.addCompany(this));
        }
        this.departments = departments;
    }

    public Company departments(Set<Department> departments) {
        this.setDepartments(departments);
        return this;
    }

    public Company addDepartment(Department department) {
        this.departments.add(department);
        department.getCompanies().add(this);
        return this;
    }

    public Company removeDepartment(Department department) {
        this.departments.remove(department);
        department.getCompanies().remove(this);
        return this;
    }

    public Set<Contact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        if (this.contacts != null) {
            this.contacts.forEach(i -> i.removeCompany(this));
        }
        if (contacts != null) {
            contacts.forEach(i -> i.addCompany(this));
        }
        this.contacts = contacts;
    }

    public Company contacts(Set<Contact> contacts) {
        this.setContacts(contacts);
        return this;
    }

    public Company addContact(Contact contact) {
        this.contacts.add(contact);
        contact.getCompanies().add(this);
        return this;
    }

    public Company removeContact(Contact contact) {
        this.contacts.remove(contact);
        contact.getCompanies().remove(this);
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
            ", mail='" + getMail() + "'" +
            ", billingAddress='" + getBillingAddress() + "'" +
            ", logo='" + getLogo() + "'" +
            ", description='" + getDescription() + "'" +
            ", waitingList='" + getWaitingList() + "'" +
            ", exhibitorList='" + getExhibitorList() + "'" +
            "}";
    }
}
