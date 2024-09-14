package de.tsystems.onsite.bookabooth.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.System} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SystemDTO implements Serializable {

    private Long id;

    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemDTO)) {
            return false;
        }

        SystemDTO systemDTO = (SystemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, systemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemDTO{" +
            "id=" + getId() +
            ", enabled='" + getEnabled() + "'" +
            "}";
    }
}
