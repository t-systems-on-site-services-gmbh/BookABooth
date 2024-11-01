package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.Location} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationDTO implements Serializable {

    private Long id;

    @Size(max = 200)
    private String location;

    @Size(max = 200)
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationDTO)) {
            return false;
        }

        LocationDTO locationDTO = (LocationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + getId() +
            ", location='" + getLocation() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
