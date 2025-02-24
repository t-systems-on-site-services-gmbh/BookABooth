package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.service.dto.AdminChecklistDTO;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dashboard")
public class AdminDashboardResource {

    private final Logger log = LoggerFactory.getLogger(AdminDashboardResource.class);

    public List<AdminChecklistDTO> getChecklist() {
        log.debug("REST request to get checklist");
        List<AdminChecklistDTO> checklist = new ArrayList<>();
        AdminChecklistDTO adminChecklistDTO = new AdminChecklistDTO();
        adminChecklistDTO.setCompanyName("Company Name");
        adminChecklistDTO.setAddress(true);
        adminChecklistDTO.setLogo(true);
        adminChecklistDTO.setPhoneNumber(true);
        adminChecklistDTO.setCompanyDescription(true);
        adminChecklistDTO.setBooth("Booth");
        checklist.add(adminChecklistDTO);
        AdminChecklistDTO adminChecklistDTO2 = new AdminChecklistDTO();
        adminChecklistDTO2.setCompanyName("Company Name 2");
        adminChecklistDTO2.setAddress(true);
        adminChecklistDTO2.setLogo(true);
        adminChecklistDTO2.setPhoneNumber(true);
        adminChecklistDTO2.setCompanyDescription(true);
        adminChecklistDTO2.setBooth("Booth");
        checklist.add(adminChecklistDTO);

        return checklist;
    }
}
