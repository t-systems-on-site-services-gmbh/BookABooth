package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BoothUser} and its DTO {@link BoothUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoothUserMapper extends EntityMapper<BoothUserDTO, BoothUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    BoothUserDTO toDto(BoothUser s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);
}
