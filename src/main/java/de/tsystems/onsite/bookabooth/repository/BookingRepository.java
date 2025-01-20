package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCompanyIdOrderByReceivedDesc(Long companyId);

    Boolean existsByBooth(Booth booth);

    Optional<Booking> findByBoothIdAndStatusNot(long boothId, BookingStatus bookingStatus);
}
