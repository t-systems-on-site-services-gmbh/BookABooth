package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.Booth} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoothDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String title;

    private BigDecimal ceilingHeight;

    @NotNull
    private Boolean available;

    private LocationDTO location;

    private Set<ServicePackageDTO> servicePackages = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCeilingHeight() {
        return ceilingHeight;
    }

    public void setCeilingHeight(BigDecimal ceilingHeight) {
        this.ceilingHeight = ceilingHeight;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public Set<ServicePackageDTO> getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(Set<ServicePackageDTO> servicePackages) {
        this.servicePackages = servicePackages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoothDTO)) {
            return false;
        }

        BoothDTO boothDTO = (BoothDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, boothDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoothDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", ceilingHeight=" + getCeilingHeight() +
            ", available='" + getAvailable() + "'" +
            ", location=" + getLocation() +
            ", servicePackages=" + getServicePackages() +
            "}";
    }
}
