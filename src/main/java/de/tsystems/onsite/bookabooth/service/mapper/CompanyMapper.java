package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper extends EntityMapper<CompanyDTO, Company> {
    CompanyDTO toDto(Company s);

    Company toEntity(CompanyDTO companyDTO, @MappingTarget Company company);
}
