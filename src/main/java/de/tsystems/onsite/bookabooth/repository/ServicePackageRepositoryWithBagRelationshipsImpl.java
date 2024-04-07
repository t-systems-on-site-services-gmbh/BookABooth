package de.tsystems.onsite.bookabooth.repository;

import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ServicePackageRepositoryWithBagRelationshipsImpl implements ServicePackageRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String SERVICEPACKAGES_PARAMETER = "servicePackages";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ServicePackage> fetchBagRelationships(Optional<ServicePackage> servicePackage) {
        return servicePackage.map(this::fetchBooths);
    }

    @Override
    public Page<ServicePackage> fetchBagRelationships(Page<ServicePackage> servicePackages) {
        return new PageImpl<>(
            fetchBagRelationships(servicePackages.getContent()),
            servicePackages.getPageable(),
            servicePackages.getTotalElements()
        );
    }

    @Override
    public List<ServicePackage> fetchBagRelationships(List<ServicePackage> servicePackages) {
        return Optional.of(servicePackages).map(this::fetchBooths).orElse(Collections.emptyList());
    }

    ServicePackage fetchBooths(ServicePackage result) {
        return entityManager
            .createQuery(
                "select servicePackage from ServicePackage servicePackage left join fetch servicePackage.booths where servicePackage.id = :id",
                ServicePackage.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ServicePackage> fetchBooths(List<ServicePackage> servicePackages) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, servicePackages.size()).forEach(index -> order.put(servicePackages.get(index).getId(), index));
        List<ServicePackage> result = entityManager
            .createQuery(
                "select servicePackage from ServicePackage servicePackage left join fetch servicePackage.booths where servicePackage in :servicePackages",
                ServicePackage.class
            )
            .setParameter(SERVICEPACKAGES_PARAMETER, servicePackages)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
