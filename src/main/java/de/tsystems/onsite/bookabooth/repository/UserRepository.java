package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    Optional<User> findOneByActivationKey(String activationKey);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<User> findOneByResetKey(String resetKey);
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u "  +
       "LEFT JOIN FETCH u.boothUser bu "  +
       "LEFT JOIN FETCH bu.company "  +
       "LEFT JOIN FETCH u.authorities "  +
       "WHERE u.login = :login ")
    Optional<User> findOneWithBoothUserCompanyAndAuthoritiesByLogin(@Param("login") String login);
    
    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    long countByAuthoritiesName(String authorityName);

    List<User> findAllByResetDateBefore(Instant minus);
}
