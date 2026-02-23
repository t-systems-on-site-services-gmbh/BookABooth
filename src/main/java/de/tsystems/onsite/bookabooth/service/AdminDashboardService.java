package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminDashboardService {

    private final Logger log = LoggerFactory.getLogger(AdminDashboardService.class);

    private final CompanyRepository companyRepository;
    private final BoothUserRepository boothUserRepository;
    private final BookingRepository bookingRepository;

    public AdminDashboardService(
        CompanyRepository companyRepository,
        BoothUserRepository boothUserRepository,
        BookingRepository bookingRepository
    ) {
        this.companyRepository = companyRepository;
        this.boothUserRepository = boothUserRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDTO getData() {
        log.debug("REST request to get checklist");
        // all companies
        List<Company> companies = companyRepository.findAll();

        AdminDashboardDTO adminDashboard = new AdminDashboardDTO();
        List<AdminChecklistDTO> checklist = adminDashboard.getChecklist();

        // helper mappings

        List<BoothUser> users = boothUserRepository.findAll().stream().filter(user -> !user.isAdmin()).toList();
        // map company.id to User
        Map<Long, BoothUser> companyBoothUsersMap = users
            .stream()
            .filter(user -> user.getCompany() != null)
            .collect(Collectors.toMap(user -> user.getCompany().getId(), user -> user));
        // map company.id to Booking
        Map<Long, Booking> companyBookingsMap = bookingRepository
            .findByStatus(BookingStatus.CONFIRMED)
            .stream()
            .collect(Collectors.toMap(b -> b.getCompany().getId(), booking -> booking));

        // map company.id to List of CANCELED Bookings
        Map<Long, List<Booking>> companyCanceledBookingsMap = bookingRepository
            .findByStatus(BookingStatus.CANCELED)
            .stream()
            .collect(Collectors.groupingBy(booking -> booking.getCompany().getId()));

        // company
        for (Company company : companies) {
            AdminChecklistDTO cl = new AdminChecklistDTO();

            // information from company
            cl.setCompanyId(company.getId());
            cl.setCompanyName(company.getName());
            cl.setAddress(company.hasBillingAddress());
            cl.setLogo(company.getLogo() != null && !company.getLogo().isBlank());
            cl.setCompanyDescription(company.getDescription() != null && !company.getDescription().isBlank());
            cl.setOnExhibitorList(company.getExhibitorList() != null && company.getExhibitorList().booleanValue());

            // information from BoothUser
            BoothUser companyBoothUser = companyBoothUsersMap.get(company.getId());
            if (companyBoothUser != null) {
                cl.setPhoneNumber(
                    companyBoothUsersMap.get(company.getId()).getPhone() != null &&
                    !companyBoothUsersMap.get(company.getId()).getPhone().isBlank()
                );
                cl.setMail(companyBoothUsersMap.get(cl.getCompanyId()).getUser().getEmail());
            }

            // information from Booking
            if (companyBookingsMap.containsKey(company.getId())) {
                String location = Optional.ofNullable(companyBookingsMap.get(company.getId()))
                    .map(Booking::getBooth)
                    .map(Booth::getLocation)
                    .map(Location::getLocation)
                    .orElse("Location fehlt");
                String booth = Optional.ofNullable(companyBookingsMap.get(company.getId()))
                    .map(Booking::getBooth)
                    .map(Booth::getTitle)
                    .orElse("Booth fehlt");
                cl.setBooth(String.format("%s-%s", location, booth));
            }

            // information from CANCELED Bookings
            if (companyCanceledBookingsMap.containsKey(company.getId())) {
                List<Booking> canceledBookings = companyCanceledBookingsMap.get(company.getId());
                String canceledBooths = canceledBookings
                    .stream()
                    .map(booking -> {
                        String location = Optional.ofNullable(booking.getBooth())
                            .map(Booth::getLocation)
                            .map(Location::getLocation)
                            .orElse("Location fehlt");
                        String booth = Optional.ofNullable(booking.getBooth()).map(Booth::getTitle).orElse("Booth fehlt");
                        return String.format("%s-%s", location, booth);
                    })
                    .collect(Collectors.joining(", ")); // Kombiniere alle stornierten Buchungen in einem String
                cl.setCanceledBooth(canceledBooths);
            }

            checklist.add(cl);
        }

        adminDashboard
            .getChecklist()
            .sort((AdminChecklistDTO o1, AdminChecklistDTO o2) -> {
                if (o1.isMandatoryComplete() && !o2.isMandatoryComplete()) {
                    return 1;
                } else if (!o1.isMandatoryComplete() && o2.isMandatoryComplete()) {
                    return -1;
                } else if (o1.isOnExhibitorList() && !o2.isOnExhibitorList()) {
                    return 1;
                } else if (!o1.isOnExhibitorList() && o2.isOnExhibitorList()) {
                    return -1;
                } else {
                    return o1.getCompanyName().toUpperCase().compareTo(o2.getCompanyName().toUpperCase());
                }
            });

        return adminDashboard;
    }
}
