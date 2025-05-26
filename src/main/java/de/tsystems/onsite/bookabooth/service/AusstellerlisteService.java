package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.dto.AusstellerlisteDTO;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AusstellerlisteService {

    private final Logger log = LoggerFactory.getLogger(AusstellerlisteService.class);
    private final BoothRepository boothRepository;
    private final BookingService bookingService;
    private final CompanyService companyService;

    public AusstellerlisteService(BoothRepository boothRepository, BookingService bookingService, CompanyService companyService) {
        this.boothRepository = boothRepository;
        this.bookingService = bookingService;
        this.companyService = companyService;
    }

    public List<AusstellerlisteDTO> getAussteller() {
        // Collect confirmed bookings and map companyID to boothID
        Map<Long, Long> confirmedCompanyAndBoothId = bookingService
            .findAll()
            .stream()
            .filter(booking -> BookingStatus.CONFIRMED.equals(booking.getStatus()))
            .collect(Collectors.toMap(booking -> booking.getCompany().getId(), booking -> booking.getBooth().getId()));

        // Collect booths and map boothID to boothTitle
        Map<Long, String> boothIdAndBoothTitle = boothRepository
            .findAll()
            .stream()
            .collect(
                Collectors.toMap(
                    Booth::getId,
                    booth ->
                        String.format(
                            "%s-%s",
                            Optional.ofNullable(booth.getLocation()).map(location -> location.getLocation()).orElse(""),
                            booth.getTitle()
                        )
                )
            );

        // Filter and map companies to exhibitorListDTO
        return companyService
            .findAll()
            .stream()
            .filter(company -> Boolean.TRUE.equals(company.getExhibitorList()))
            .filter(company -> confirmedCompanyAndBoothId.containsKey(company.getId()))
            .sorted((c1, c2) -> Comparator.nullsFirst(String::compareTo).compare(c1.getName().toUpperCase(), c2.getName().toUpperCase()))
            .map(company -> {
                Long boothId = confirmedCompanyAndBoothId.get(company.getId());
                String boothTitle = boothIdAndBoothTitle.getOrDefault(boothId, "");
                return new AusstellerlisteDTO(company.getId(), company.getName(), company.getLogo(), company.getDescription(), boothTitle);
            })
            .collect(Collectors.toList());
    }
}
