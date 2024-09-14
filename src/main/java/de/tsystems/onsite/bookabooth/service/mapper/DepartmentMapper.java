package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.Department;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.DepartmentDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Department} and its DTO {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {
    @Mapping(target = "companies", source = "companies", qualifiedByName = "companyIdSet")
    DepartmentDTO toDto(Department s);

    @Mapping(target = "removeCompany", ignore = true)
    Department toEntity(DepartmentDTO departmentDTO);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("companyIdSet")
    default Set<CompanyDTO> toDtoCompanyIdSet(Set<Company> company) {
        return company.stream().map(this::toDtoCompanyId).collect(Collectors.toSet());
    }
}
