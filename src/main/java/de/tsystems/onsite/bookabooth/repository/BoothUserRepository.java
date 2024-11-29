package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BoothUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoothUserRepository extends JpaRepository<BoothUser, Long> {
    List<BoothUser> findAllByCompanyId(Long id);
}
