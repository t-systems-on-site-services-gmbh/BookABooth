package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.config.Constants;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.security.SecurityUtils;
import de.tsystems.onsite.bookabooth.service.BoothUserService;
import de.tsystems.onsite.bookabooth.service.CompanyService;
import de.tsystems.onsite.bookabooth.service.MailService;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserProfileDTO;
import de.tsystems.onsite.bookabooth.service.exception.BadRequestException;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.security.auth.login.AccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the waiting list and sending emails.
 */
@RestController
@RequestMapping("/api/waitinglist")
public class WaitinglistResource {

    private final Logger log = LoggerFactory.getLogger(WaitinglistResource.class);

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final MailService mailService;
    private final UserService userService;
    private final BoothUserService boothUserService;

    public WaitinglistResource(
        CompanyService companyService,
        CompanyRepository companyRepository,
        MailService mailService,
        UserService userService,
        BoothUserService boothUserService
    ) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.mailService = mailService;
        this.userService = userService;
        this.boothUserService = boothUserService;
    }

    @GetMapping("")
    public List<CompanyDTO> getAllWaitingListCompanies() {
        log.debug("REST request to get all companies on the waiting list");

        var companies = companyService.findAll();
        try {
            companies.forEach(company -> {
                Long companyId = company.getId();
                List<User> users = userService.findUsersByCompanyId(companyId);

                if ((users == null) || users.isEmpty()) {
                    log.warn("No user IDs found for company ID: {}", companyId);
                    company.setMail("Dieser Firma ist kein Benutzer zugeordnet.");
                } else {
                    log.debug("Setting emails for company ID: {}", company.getId());
                    List<String> emails = users.stream().map(User::getEmail).filter(Objects::nonNull).toList();
                    company.setMail(String.join(", ", emails));
                }
            });
        } catch (Exception e) {
            log.error("Error while setting emails for companies", e);
        }
        return companies
            .stream()
            .sorted((c1, c2) -> {
                Boolean w1 = c1.getWaitingList();
                Boolean w2 = c2.getWaitingList();
                // nulls first, false next, true last
                return Comparator.nullsFirst(Boolean::compareTo).reversed().compare(w1, w2);
            })
            .toList();
    }

    @PostMapping("/send-emails")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> sendEmailsToWaitingList() {
        log.debug("REST request to send emails to companies on the waiting list");

        List<CompanyDTO> waitingListCompanies = companyService
            .findAll()
            .stream()
            .filter(company -> Objects.nonNull(company.getWaitingList()) && company.getWaitingList())
            .toList();

        if (waitingListCompanies.isEmpty()) {
            log.info("No companies on the waiting list.");
            return ResponseEntity.ok().build();
        }

        waitingListCompanies.forEach(company -> {
            Long companyId = company.getId();
            String companyName = company.getName();
            List<User> users = userService.findUsersByCompanyId(companyId);

            if ((users == null) || users.isEmpty()) {
                log.warn("No users found for company ID: {}", companyId);
            } else {
                users.forEach(user -> {
                    if ((user.getEmail() != null) && user.isActivated()) {
                        String userEmail = user.getEmail();

                        user.setLangKey(Constants.DEFAULT_LANGUAGE);
                        user.setLogin(companyName);

                        mailService.sendWaitingListEmail(user);
                    }
                });
            }
        });

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> partialUpdateWaitingListStatus(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        log.debug("REST request to partially update the status of company with ID: {}", id);

        if (companyDTO == null || companyDTO.getWaitingList() == null) {
            log.error("Received null or invalid CompanyDTO in the request body for ID: {}", id);
            throw new BadRequestAlertException("Invalid CompanyDTO", "company", "invalidData");
        }
        if (companyDTO.getId() == null) {
            log.debug("CompanyDTO ID is null, setting it to the path variable ID: {}", id);
            companyDTO.setId(id);
        }

        if (!id.equals(companyDTO.getId())) {
            log.error("Path ID {} does not match CompanyDTO ID {}", id, companyDTO.getId());
            throw new BadRequestAlertException("ID mismatch", "company", "idMismatch");
        }

        if (!companyRepository.existsById(id)) {
            log.warn("Company with ID {} not found", id);
            throw new BadRequestAlertException("Entity not found", "company", "notFound");
        }

        companyService.partialUpdate(companyDTO);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/add-waitinglist")
    public ResponseEntity<Void> addToWaitingList(@RequestBody UserProfileDTO userProfileDTO, Authentication authentication) {
        var boothUserDTO = boothUserService.getCurrentBoothUser(authentication);

        if (!boothUserDTO.getCompany().getId().equals(userProfileDTO.getCompany().getId())) {
            log.warn(
                "User {} not authorized to add company {} to waiting list",
                boothUserDTO.getUser().getLogin(),
                userProfileDTO.getCompany().getId()
            );
            throw new BadRequestException("User not authorized to add company to waiting list");
        }

        var company = companyService
            .findOne(boothUserDTO.getCompany().getId())
            .orElseThrow(() -> new BadRequestException("Company could not be found"));

        companyService.setWaitingList(company, true);
        return ResponseEntity.ok().build();
    }
}
