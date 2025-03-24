package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.config.Constants;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WaitinglistService {

    private final Logger log = LoggerFactory.getLogger(WaitinglistService.class);
    private final CompanyService companyService;
    private final UserService userService;
    private final MailService mailService;

    public WaitinglistService(CompanyService companyService, UserService userService, MailService mailService) {
        this.companyService = companyService;
        this.userService = userService;
        this.mailService = mailService;
    }

    public List<CompanyDTO> getCompaniesOnWaitingList() {
        log.debug("Request to get all companies on the waiting list");

        var companies = companyService.findAll();

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

    public void sendMailToCompaniesOnWaitingList() {
        List<CompanyDTO> waitingListCompanies = companyService
            .findAll()
            .stream()
            .filter(company -> Objects.nonNull(company.getWaitingList()) && company.getWaitingList())
            .toList();

        if (waitingListCompanies.isEmpty()) {
            log.info("No companies on the waiting list.");
            return;
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
    }
}
