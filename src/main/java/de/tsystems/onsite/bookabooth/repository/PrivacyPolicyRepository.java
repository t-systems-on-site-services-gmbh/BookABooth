package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.PrivacyPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyPolicyRepository extends JpaRepository<PrivacyPolicy, Long> {
    @Query("SELECT p FROM PrivacyPolicy p ORDER BY p.id DESC LIMIT 1")
    Optional<PrivacyPolicy> findLatestPrivacyPolicyById();
}
