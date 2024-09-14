package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.service.dto.BookingDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booking} and its DTO {@link BookingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "booth", source = "booth", qualifiedByName = "boothId")
    BookingDTO toDto(Booking s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("boothId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BoothDTO toDtoBoothId(Booth booth);
}
