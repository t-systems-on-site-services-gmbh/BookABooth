package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.Contact;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.ContactDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Contact} and its DTO {@link ContactDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContactMapper extends EntityMapper<ContactDTO, Contact> {
    @Mapping(target = "companies", source = "companies", qualifiedByName = "companyIdSet")
    ContactDTO toDto(Contact s);

    @Mapping(target = "removeCompany", ignore = true)
    Contact toEntity(ContactDTO contactDTO);

    @Mapping(target = "id", ignore = true)
    void updateContactEntity(ContactDTO contactDTO, @MappingTarget Contact contact);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("companyIdSet")
    default Set<CompanyDTO> toDtoCompanyIdSet(Set<Company> company) {
        return company.stream().map(this::toDtoCompanyId).collect(Collectors.toSet());
    }
}
