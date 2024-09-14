package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.ServicePackageDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServicePackage} and its DTO {@link ServicePackageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicePackageMapper extends EntityMapper<ServicePackageDTO, ServicePackage> {
    @Mapping(target = "booths", source = "booths", qualifiedByName = "boothIdSet")
    ServicePackageDTO toDto(ServicePackage s);

    @Mapping(target = "removeBooth", ignore = true)
    ServicePackage toEntity(ServicePackageDTO servicePackageDTO);

    @Named("boothId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BoothDTO toDtoBoothId(Booth booth);

    @Named("boothIdSet")
    default Set<BoothDTO> toDtoBoothIdSet(Set<Booth> booth) {
        return booth.stream().map(this::toDtoBoothId).collect(Collectors.toSet());
    }
}
