package de.tsystems.onsite.bookabooth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Booth.
 */
@Entity
@Table(name = "booth")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Booth implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "title", length = 200, nullable = false, unique = true)
    private String title;

    @Column(name = "ceiling_height", precision = 21, scale = 2)
    private BigDecimal ceilingHeight;

    @NotNull
    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "booths" }, allowSetters = true)
    private Location location;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "booths")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "booths" }, allowSetters = true)
    private Set<ServicePackage> servicePackages = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booth id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Booth title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCeilingHeight() {
        return this.ceilingHeight;
    }

    public Booth ceilingHeight(BigDecimal ceilingHeight) {
        this.setCeilingHeight(ceilingHeight);
        return this;
    }

    public void setCeilingHeight(BigDecimal ceilingHeight) {
        this.ceilingHeight = ceilingHeight;
    }

    public Boolean getAvailable() {
        return this.available;
    }

    public Booth available(Boolean available) {
        this.setAvailable(available);
        return this;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Booth location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<ServicePackage> getServicePackages() {
        return this.servicePackages;
    }

    public void setServicePackages(Set<ServicePackage> servicePackages) {
        if (this.servicePackages != null) {
            this.servicePackages.forEach(i -> i.removeBooth(this));
        }
        if (servicePackages != null) {
            servicePackages.forEach(i -> i.addBooth(this));
        }
        this.servicePackages = servicePackages;
    }

    public Booth servicePackages(Set<ServicePackage> servicePackages) {
        this.setServicePackages(servicePackages);
        return this;
    }

    public Booth addServicePackage(ServicePackage servicePackage) {
        this.servicePackages.add(servicePackage);
        servicePackage.getBooths().add(this);
        return this;
    }

    public Booth removeServicePackage(ServicePackage servicePackage) {
        this.servicePackages.remove(servicePackage);
        servicePackage.getBooths().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booth)) {
            return false;
        }
        return getId() != null && getId().equals(((Booth) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booth{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", ceilingHeight=" + getCeilingHeight() +
            ", available='" + getAvailable() + "'" +
            "}";
    }
}
