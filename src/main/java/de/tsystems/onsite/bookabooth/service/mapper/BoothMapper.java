package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.Location;
import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.LocationDTO;
import de.tsystems.onsite.bookabooth.service.dto.ServicePackageDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booth} and its DTO {@link BoothDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoothMapper extends EntityMapper<BoothDTO, Booth> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationId")
    @Mapping(target = "servicePackages", source = "servicePackages", qualifiedByName = "servicePackageIdSet")
    BoothDTO toDto(Booth s);

    @Mapping(target = "servicePackages", ignore = true)
    @Mapping(target = "removeServicePackage", ignore = true)
    Booth toEntity(BoothDTO boothDTO);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);

    @Named("servicePackageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServicePackageDTO toDtoServicePackageId(ServicePackage servicePackage);

    @Named("servicePackageIdSet")
    default Set<ServicePackageDTO> toDtoServicePackageIdSet(Set<ServicePackage> servicePackage) {
        return servicePackage.stream().map(this::toDtoServicePackageId).collect(Collectors.toSet());
    }
}
