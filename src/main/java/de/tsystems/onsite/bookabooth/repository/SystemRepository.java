package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.System;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the System entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemRepository extends JpaRepository<System, Long> {
    // find first entry as Optional
    System findFirstByOrderById();
}
