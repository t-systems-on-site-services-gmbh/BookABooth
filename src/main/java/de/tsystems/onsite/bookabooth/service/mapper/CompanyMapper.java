package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.Contact;
import de.tsystems.onsite.bookabooth.domain.Department;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.ContactDTO;
import de.tsystems.onsite.bookabooth.service.dto.DepartmentDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper extends EntityMapper<CompanyDTO, Company> {
    @Mapping(target = "departments", source = "departments", qualifiedByName = "departmentIdSet")
    @Mapping(target = "contacts", source = "contacts", qualifiedByName = "contactIdSet")
    CompanyDTO toDto(Company s);

    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "removeDepartment", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "removeContact", ignore = true)
    Company toEntity(CompanyDTO companyDTO, @MappingTarget Company company);

    @Named("departmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepartmentDTO toDtoDepartmentId(Department department);

    @Named("departmentIdSet")
    default Set<DepartmentDTO> toDtoDepartmentIdSet(Set<Department> department) {
        return department.stream().map(this::toDtoDepartmentId).collect(Collectors.toSet());
    }

    @Named("contactId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContactDTO toDtoContactId(Contact contact);

    @Named("contactIdSet")
    default Set<ContactDTO> toDtoContactIdSet(Set<Contact> contact) {
        return contact.stream().map(this::toDtoContactId).collect(Collectors.toSet());
    }
}
