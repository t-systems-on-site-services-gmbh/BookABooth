package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.service.dto.ExhibitorDTO;
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
    List<Booking> findByCompanyId(Long id);
    Boolean existsByBooth(Booth booth);
    Optional<Booking> findByBoothIdAndStatusNot(long boothId, BookingStatus bookingStatus);
    List<Booking> findByStatusNot(BookingStatus bookingStatus);
    List<Booking> findByStatus(BookingStatus bookingStatus);
    List<Booking> findByStatusIn(List<BookingStatus> status);

    @Query(
        "SELECT new de.tsystems.onsite.bookabooth.service.dto.ExhibitorDTO(" +
        "c.name, " +
        "c.logo, " +
        "loc.location, " +
        "booth.title, " +
        "c.exhibitorList) " +
        "FROM Booking b " +
        "JOIN b.company c " +
        "JOIN b.booth booth " +
        "JOIN booth.location loc " +
        "WHERE b.status = de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus.CONFIRMED"
    )
    List<ExhibitorDTO> findExhibitorsWithConfirmedBooking();
}
