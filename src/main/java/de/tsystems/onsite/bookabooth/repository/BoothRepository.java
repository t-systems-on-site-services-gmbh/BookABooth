package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booth entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findByAvailable(boolean available);

    @Query(
        "SELECT b FROM Booth b WHERE b.available = true AND b.id NOT IN (SELECT bk.booth.id FROM Booking bk WHERE bk.status IN :excludedStatus)"
    )
    List<Booth> findAvailableBoothsWithoutBookingStatus(@Param("excludedStatus") List<BookingStatus> excludedStatus);
}
