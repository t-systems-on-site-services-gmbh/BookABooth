package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import de.tsystems.onsite.bookabooth.service.dto.AdminDashboardDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public AdminDashboardResource(
        CompanyRepository companyRepository,
        BoothUserRepository boothUserRepository,
        BookingRepository bookingRepository
    ) {
        this.companyRepository = companyRepository;
        this.boothUserRepository = boothUserRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * {@code GET  /checklist} : get the checklist.
     *
     * @return the {@link List} of entities.
     */
    @GetMapping("/data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminDashboardDTO getChecklist() {
        log.debug("REST request to get checklist");

        // all companies
        List<Company> companies = companyRepository.findAll();

        AdminDashboardDTO adminDashboard = new AdminDashboardDTO();
        List<AdminChecklistDTO> checklist = adminDashboard.getChecklist();

        // helper mappings

        List<BoothUser> users = boothUserRepository.findAll().stream().filter(user -> !user.isAdmin()).collect(Collectors.toList());
        // map company.id to User
        Map<Long, BoothUser> companyBoothUsersMap = users
            .stream()
            .collect(Collectors.toMap(user -> user.getCompany().getId(), user -> user));
        // map company.id to Booking
        Map<Long, Booking> companyBookingsMap = bookingRepository
            .findByStatus(BookingStatus.CONFIRMED)
            .stream()
            .collect(Collectors.toMap(b -> b.getCompany().getId(), booking -> booking));

        // company
        for (Company company : companies) {
            AdminChecklistDTO cl = new AdminChecklistDTO();

            // information from company
            cl.setCompanyId(company.getId());
            cl.setCompanyName(company.getName());
            cl.setAddress(company.getBillingAddress() != null && !company.getBillingAddress().isBlank() ? true : false);
            cl.setLogo(company.getLogo() != null && !company.getLogo().isBlank() ? true : false);
            cl.setCompanyDescription(company.getDescription() != null && !company.getDescription().isBlank() ? true : false);

            // information from BoothUser
            BoothUser companyBoothUser = companyBoothUsersMap.get(company.getId());
            if (companyBoothUser != null) {
                adminDashboard.getCompanies().add(companyBoothUser.getUser().getEmail());
                cl.setPhoneNumber(
                    companyBoothUsersMap.get(company.getId()).getPhone() != null &&
                        !companyBoothUsersMap.get(company.getId()).getPhone().isBlank()
                        ? true
                        : false
                );
            }

            // information from Booking
            if (companyBookingsMap.containsKey(company.getId())) {
                String location = Optional.ofNullable(companyBookingsMap.get(company.getId()))
                    .map(booking -> booking.getBooth())
                    .map(booth -> booth.getLocation())
                    .map(l -> l.getLocation())
                    .orElse("Location fehlt");
                String booth = Optional.ofNullable(companyBookingsMap.get(company.getId()))
                    .map(booking -> booking.getBooth())
                    .map(b -> b.getTitle())
                    .orElse("Booth fehlt");
                cl.setBooth(String.format("%s-%s", location, booth));
            }

            checklist.add(cl);
        }

        checklist.forEach(cl -> {
            if (!cl.isMandatoryComplete()) {
                adminDashboard.getUncompletedProfiles().add(companyBoothUsersMap.get(cl.getCompanyId()).getUser().getEmail());
            }
        });

        adminDashboard
            .getChecklist()
            .sort((o1, o2) -> {
                if (o1.getAddress() && o1.getPhoneNumber()) {
                    return 0;
                } else {
                    return -1;
                }
            });

        return adminDashboard;
    }
}
