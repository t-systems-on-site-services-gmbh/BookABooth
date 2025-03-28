package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.service.AdminDashboardService;
import de.tsystems.onsite.bookabooth.service.dto.AdminDashboardDTO;
import java.util.List;
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
    private AdminDashboardService AdminDashboardService;

    public AdminDashboardResource(AdminDashboardService AdminDashboardService) {
        this.AdminDashboardService = AdminDashboardService;
    }

    /**
     * {@code GET  /checklist} : get the checklist.
     *
     * @return the {@link List} of entities.
     */
    @GetMapping("/data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminDashboardDTO getChecklist() {
        return AdminDashboardService.getData();
    }
}
