package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Location;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Location entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query(
        value = "SELECT l.id, l.location, l.url, COUNT(DISTINCT b.id) amount, COUNT(DISTINCT bk.id) booked " +
        "FROM location l " +
        "LEFT JOIN booth b ON b.location_id = l.id " +
        "LEFT JOIN booking bk ON bk.booth_id = b.id AND bk.status = 'CONFIRMED' " +
        "GROUP BY l.id ",
        nativeQuery = true
    )
    List<Object[]> findAllEnriched();
}
