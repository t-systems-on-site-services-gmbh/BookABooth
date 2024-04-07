package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ServicePackageRepositoryWithBagRelationships {
    Optional<ServicePackage> fetchBagRelationships(Optional<ServicePackage> servicePackage);

    List<ServicePackage> fetchBagRelationships(List<ServicePackage> servicePackages);

    Page<ServicePackage> fetchBagRelationships(Page<ServicePackage> servicePackages);
}
