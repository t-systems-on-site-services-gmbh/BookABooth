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
 * A Location.
 */
@Entity
@Table(name = "location")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Location implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 200)
    @Column(name = "location", length = 200, unique = true)
    private String location;

    @Size(max = 200)
    @Column(name = "url", length = 200, unique = true)
    private String imageUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "location", "servicePackages" }, allowSetters = true)
    private Set<Booth> booths = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Location id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return this.location;
    }

    public Location location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Booth> getBooths() {
        return this.booths;
    }

    public void setBooths(Set<Booth> booths) {
        if (this.booths != null) {
            this.booths.forEach(i -> i.setLocation(null));
        }
        if (booths != null) {
            booths.forEach(i -> i.setLocation(this));
        }
        this.booths = booths;
    }

    public Location booths(Set<Booth> booths) {
        this.setBooths(booths);
        return this;
    }

    public Location addBooths(Booth booth) {
        this.booths.add(booth);
        booth.setLocation(this);
        return this;
    }

    public Location removeBooths(Booth booth) {
        this.booths.remove(booth);
        booth.setLocation(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        return getId() != null && getId().equals(((Location) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", location='" + getLocation() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
