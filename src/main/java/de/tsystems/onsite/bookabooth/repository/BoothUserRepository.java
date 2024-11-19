package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the BoothUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoothUserRepository extends JpaRepository<BoothUser, Long> {
    Optional<BoothUser> findByUserLogin(String login);

    List<BoothUser> findByCompanyName(String companyName);

    Optional<BoothUser> findByUser(User user);
}
