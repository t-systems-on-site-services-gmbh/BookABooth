package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.CompanyService;
import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import de.tsystems.onsite.bookabooth.service.dto.AdminDashboardDTO;
import java.util.List;
import java.util.Map;
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
    private CompanyService companyService;

    public AdminDashboardResource(CompanyRepository companyRepository, CompanyService companyService) {
        this.companyRepository = companyRepository;
        this.companyService = companyService;
    }

    /**
     * {@code GET  /checklist} : get the checklist.
     *
     * @return the {@link List} of entities.
     */
    @GetMapping("/checklist")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminChecklistDTO> getChecklist() {
        log.debug("REST request to get checklist");

        // all companies
        List<Company> companies = companyRepository.findAll();

        List<AdminChecklistDTO> adminChecklist = companyService.getAdminChecklist(companies);

        adminChecklist.sort((o1, o2) -> {
            if (o1.getAddress() && o1.getPhoneNumber()) {
                return 0;
            } else {
                return -1;
            }
        });

        return adminChecklist;
    }

    @GetMapping("/data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminDashboardDTO getAdminDashboardData() {
        log.debug("REST request to get admin dashboard data");
        AdminDashboardDTO data = new AdminDashboardDTO();
        return data;
    }
}
