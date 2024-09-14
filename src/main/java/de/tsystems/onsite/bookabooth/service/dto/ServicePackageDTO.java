package de.tsystems.onsite.bookabooth.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.ServicePackage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServicePackageDTO implements Serializable {

    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private Set<BoothDTO> booths = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<BoothDTO> getBooths() {
        return booths;
    }

    public void setBooths(Set<BoothDTO> booths) {
        this.booths = booths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicePackageDTO)) {
            return false;
        }

        ServicePackageDTO servicePackageDTO = (ServicePackageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, servicePackageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServicePackageDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", description='" + getDescription() + "'" +
            ", booths=" + getBooths() +
            "}";
    }
}
