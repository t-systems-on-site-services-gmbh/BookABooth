package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.CompanyService;
import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @GetMapping("/checklist")
    public List<AdminChecklistDTO> getChecklist() {
        log.debug("REST request to get checklist");

        // all companies
        List<Company> companies = companyRepository.findAll();
        List<AdminChecklistDTO> adminChecklist = companyService.getAdminChecklist(companies);

        AdminChecklistDTO adminChecklistDTO = new AdminChecklistDTO();
        adminChecklistDTO.setCompanyName("Company Name");
        adminChecklistDTO.setAddress(true);
        adminChecklistDTO.setLogo(true);
        adminChecklistDTO.setPhoneNumber(true);
        adminChecklistDTO.setCompanyDescription(true);
        adminChecklistDTO.setBooth("Booth");
        adminChecklist.add(adminChecklistDTO);
        AdminChecklistDTO adminChecklistDTO2 = new AdminChecklistDTO();
        adminChecklistDTO2.setCompanyName("Company Name 2");
        adminChecklistDTO2.setAddress(true);
        adminChecklistDTO2.setLogo(true);
        adminChecklistDTO2.setPhoneNumber(true);
        adminChecklistDTO2.setCompanyDescription(true);
        adminChecklistDTO2.setBooth("Booth");
        adminChecklist.add(adminChecklistDTO2);

        return adminChecklist;
    }
}
