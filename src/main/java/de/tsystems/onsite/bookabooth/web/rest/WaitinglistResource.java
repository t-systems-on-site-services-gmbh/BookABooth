package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.config.Constants;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.service.CompanyService;
import de.tsystems.onsite.bookabooth.service.MailService;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the waiting list and sending emails.
 */
@RestController
@RequestMapping("/api/waitinglist")
public class WaitinglistResource {

    private final Logger log = LoggerFactory.getLogger(WaitinglistResource.class);

    private final CompanyService companyService;
    private final MailService mailService;

    public WaitinglistResource(CompanyService companyService, MailService mailService) {
        this.companyService = companyService;
        this.mailService = mailService;
    }

    /**
     * {@code GET /waitinglist} : get all companies on the waiting list.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companies in body.
     */
    @GetMapping("")
    public List<CompanyDTO> getAllWaitingListCompanies() {
        log.debug("REST request to get all companies on the waiting list");
        return companyService
            .findAll()
            .stream()
            .sorted(Comparator.comparing(CompanyDTO::isWaitingList).reversed()) // Sortiert nach isWaitingList
            .toList();
    }

    @PostMapping("/send-emails")
    public ResponseEntity<Void> sendEmailsToWaitingList() {
        log.debug("REST request to send emails to companies on the waiting list");

        List<CompanyDTO> waitingListCompanies = companyService.findAll().stream().filter(CompanyDTO::isWaitingList).toList();

        if (waitingListCompanies.isEmpty()) {
            log.info("No companies on the waiting list.");
            return ResponseEntity.ok().build();
        }

        for (CompanyDTO company : waitingListCompanies) {
            String email = company.getEmail();
            String companyName = company.getName();

            User user = new User();
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
            user.setLogin(companyName);
            user.setEmail(email);

            // Send the waiting list email
            mailService.sendWaitingListEmail(user);
        }

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateWaitingListStatus(@PathVariable Long id, @RequestBody CompanyDTO statusDTO) {
        if (statusDTO == null || statusDTO.getWaitingList() == null) {
            log.error("Received null or invalid status in the request body for ID: {}", id);
            return ResponseEntity.badRequest().build();
        }

        Boolean parsedStatus = statusDTO.getWaitingList();
        log.debug("Parsed status (true/false): {}", parsedStatus);

        // Fetch
        Optional<CompanyDTO> companyOptional = companyService.findOne(id);
        if (companyOptional.isEmpty()) {
            log.warn("Company with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        CompanyDTO company = companyOptional.get();
        company.setWaitingList(parsedStatus);
        companyService.save(company);

        return ResponseEntity.ok().build();
    }
}
