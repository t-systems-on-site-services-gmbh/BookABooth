package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ServicePackage entity.
 *
 * When extending this class, extend ServicePackageRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ServicePackageRepository extends ServicePackageRepositoryWithBagRelationships, JpaRepository<ServicePackage, Long> {
    default Optional<ServicePackage> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<ServicePackage> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<ServicePackage> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
