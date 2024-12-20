package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.Booth;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booth entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findByAvailable(boolean available);
}
