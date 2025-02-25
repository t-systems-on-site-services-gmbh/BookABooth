package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserProfileDTO;
import de.tsystems.onsite.bookabooth.service.mapper.CompanyMapper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.Company}.
 */
@Service
@Transactional
public class CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final BoothUserService boothUserService;
    private final BookingRepository bookingRepository;

    public CompanyService(
        CompanyRepository companyRepository,
        CompanyMapper companyMapper,
        BoothUserService boothUserService,
        BookingRepository bookingRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.boothUserService = boothUserService;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    /**
     * Update a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO update(CompanyDTO companyDTO) {
        log.debug("Request to update Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    /**
     * Partially update a company.
     *
     * @param companyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CompanyDTO> partialUpdate(CompanyDTO companyDTO) {
        log.debug("Request to partially update Company : {}", companyDTO);

        return companyRepository
            .findById(companyDTO.getId())
            .map(existingCompany -> {
                companyMapper.partialUpdate(existingCompany, companyDTO);

                return existingCompany;
            })
            .map(companyRepository::save)
            .map(companyMapper::toDto);
    }

    /**
     * Get all the companies.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> findAll() {
        log.debug("Request to get all Companies");
        return companyRepository.findAll().stream().map(companyMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);

        companyRepository.deleteById(id);
    }

    public void addToWaitingList(UserProfileDTO userProfileDTO) {
        Optional.of(companyRepository.findById(userProfileDTO.getCompany().getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(company -> {
                company.setWaitingList(true);
                companyRepository.save(company);
                return userProfileDTO;
            });
    }

    public List<AdminChecklistDTO> getAdminChecklist(List<Company> companies) {
        List<BoothUser> users = boothUserService.getAllUsers().stream().filter(user -> !user.isAdmin()).collect(Collectors.toList());

        // map company.id to User
        Map<Long, BoothUser> usersMap = users.stream().collect(Collectors.toMap(user -> user.getCompany().getId(), user -> user));
        // map company.id to Booking
        Map<Long, Booking> bookingsMap = bookingRepository
            .findByStatus(BookingStatus.CONFIRMED)
            .stream()
            .collect(Collectors.toMap(b -> b.getCompany().getId(), booking -> booking));

        List<AdminChecklistDTO> checklist = new ArrayList<>();
        for (Company company : companies) {
            AdminChecklistDTO cl = new AdminChecklistDTO();
            cl.setCompanyName(company.getName());

            if (bookingsMap.containsKey(company.getId())) {
                String location = Optional.ofNullable(bookingsMap.get(company.getId()))
                    .map(booking -> booking.getBooth())
                    .map(booth -> booth.getLocation())
                    .map(l -> l.getLocation())
                    .orElse("Location fehlt");
                String booth = Optional.ofNullable(bookingsMap.get(company.getId()))
                    .map(booking -> booking.getBooth())
                    .map(b -> b.getTitle())
                    .orElse("Booth fehlt");
                cl.setBooth(String.format("%s-%s", location, booth));
            }

            cl.setAddress(company.getBillingAddress() != null && !company.getBillingAddress().isBlank() ? true : false);
            cl.setLogo(company.getLogo() != null && !company.getLogo().isBlank() ? true : false);

            if (usersMap.containsKey(company.getId())) {
                cl.setPhoneNumber(
                    usersMap.get(company.getId()).getPhone() != null && !usersMap.get(company.getId()).getPhone().isBlank() ? true : false
                );
            }

            cl.setCompanyDescription(company.getDescription() != null && !company.getDescription().isBlank() ? true : false);

            checklist.add(cl);
        }

        return checklist;
    }
}
