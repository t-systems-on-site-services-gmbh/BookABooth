package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.*;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserProfileDTO;
import de.tsystems.onsite.bookabooth.service.exception.BadRequestException;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final BoothUserService boothUserService;
    private final WaitinglistService waitinglistService;

    public WaitinglistResource(
        WaitinglistService waitinglistService,
        CompanyService companyService,
        CompanyRepository companyRepository,
        BoothUserService boothUserService
    ) {
        this.waitinglistService = waitinglistService;
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.boothUserService = boothUserService;
    }

    @GetMapping("")
    public List<CompanyDTO> getAllWaitingListCompanies() {
        log.debug("REST request to get all companies on the waiting list");
        return waitinglistService.getCompaniesOnWaitingList();
    }

    @PostMapping("/send-emails")
    public ResponseEntity<Void> sendEmailsToWaitingList() {
        log.debug("REST request to send emails to companies on the waiting list");
        waitinglistService.sendMailToCompaniesOnWaitingList();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
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
        var boothUserDTO = boothUserService.getCurrentBoothUserDTO(authentication);

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
