package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BoothUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoothUserRepository extends JpaRepository<BoothUser, Long> {
    Optional<BoothUser> findByUserLogin(String login);
    List<BoothUser> findByCompanyName(String companyName);
    List<BoothUser> findByCompanyId(Long id);
    BoothUser findOneByCompanyId(Long id);
    long countByCompanyId(Long companyId);
}
