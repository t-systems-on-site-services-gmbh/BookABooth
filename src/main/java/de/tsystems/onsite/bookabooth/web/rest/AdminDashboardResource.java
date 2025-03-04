package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.AdminDashboardService;
import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import de.tsystems.onsite.bookabooth.service.dto.AdminDashboardDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dashboard")
public class AdminDashboardResource {

    private final Logger log = LoggerFactory.getLogger(AdminDashboardResource.class);
    private CompanyRepository companyRepository;
    private BoothUserRepository boothUserRepository;
    private BookingRepository bookingRepository;
    private AdminDashboardService AdminDashboardService;

    public AdminDashboardResource(
        CompanyRepository companyRepository,
        BoothUserRepository boothUserRepository,
        BookingRepository bookingRepository,
        AdminDashboardService AdminDashboardService
    ) {
        this.companyRepository = companyRepository;
        this.boothUserRepository = boothUserRepository;
        this.bookingRepository = bookingRepository;
        this.AdminDashboardService = AdminDashboardService;
    }

    /**
     * {@code GET  /checklist} : get the checklist.
     *
     * @return the {@link List} of entities.
     */
    @GetMapping("/data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminDashboardDTO getChecklist() {
        return AdminDashboardService.getData();
    }
}
