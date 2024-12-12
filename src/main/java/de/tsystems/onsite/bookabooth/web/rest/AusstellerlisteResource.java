package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.service.BookingService;
import de.tsystems.onsite.bookabooth.service.BoothService;
import de.tsystems.onsite.bookabooth.service.CompanyService;
import de.tsystems.onsite.bookabooth.service.dto.AusstellerlisteDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ausstellerliste")
public class AusstellerlisteResource {

    private final Logger log = LoggerFactory.getLogger(AusstellerlisteResource.class);
    private final BoothService boothService;
    private final CompanyService companyService;
    private final BookingService bookingService;

    public AusstellerlisteResource(CompanyService companyService, BoothService boothService, BookingService bookingService) {
        this.companyService = companyService;
        this.boothService = boothService;
        this.bookingService = bookingService;
    }

    @GetMapping("")
    public List<AusstellerlisteDTO> getAllAusstellerlisteCompanies() {
        log.debug("REST request to get all companies on the waiting list");

        Map<Long, String> boothTitlesByCompanyId = boothService
            .findAll()
            .stream()
            .collect(Collectors.groupingBy(BoothDTO::getId, Collectors.mapping(BoothDTO::getTitle, Collectors.joining(", "))));
        List<Long> confirmedCompanyIds = bookingService
            .findAll()
            .stream()
            .filter(booking -> BookingStatus.CONFIRMED.equals(booking.getStatus()))
            .map(booking -> booking.getCompany().getId())
            .collect(Collectors.toList());
        return companyService
            .findAll()
            .stream()
            .filter(company -> Boolean.TRUE.equals(company.getExhibitorList()))
            .filter(company -> confirmedCompanyIds.contains(company.getId()))
            .sorted((c1, c2) -> Comparator.nullsFirst(String::compareTo).compare(c1.getName(), c2.getName()))
            .map(
                company ->
                    new AusstellerlisteDTO(
                        company.getId(),
                        company.getName(),
                        company.getLogo(),
                        company.getDescription(),
                        company.getExhibitorList(),
                        boothTitlesByCompanyId.getOrDefault(company.getId(), "")
                    )
            )
            .collect(Collectors.toList());
    }
}
