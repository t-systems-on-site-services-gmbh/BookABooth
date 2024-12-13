package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.Booking} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingDTO implements Serializable {

    private Long id;

    private ZonedDateTime received;

    private BookingStatus status;

    @NotNull
    private CompanyDTO company;

    @NotNull
    private BoothDTO booth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getReceived() {
        return received;
    }

    public void setReceived(ZonedDateTime received) {
        this.received = received;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public BoothDTO getBooth() {
        return booth;
    }

    public void setBooth(BoothDTO booth) {
        this.booth = booth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingDTO)) {
            return false;
        }

        BookingDTO bookingDTO = (BookingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingDTO{" +
            "id=" + getId() +
            ", received='" + getReceived() + "'" +
            ", status='" + getStatus() + "'" +
            ", company=" + getCompany() +
            ", booth=" + getBooth() +
            "}";
    }
}
