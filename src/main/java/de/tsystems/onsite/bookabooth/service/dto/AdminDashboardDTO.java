package de.tsystems.onsite.bookabooth.service.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDTO {

    private List<String> uncompletedProfiles = new ArrayList<>();
    private List<String> companyAddresses = new ArrayList<>();

    public AdminDashboardDTO() {}

    public List<String> getUncompletedProfiles() {
        return uncompletedProfiles;
    }

    public void setUncompletedProfiles(List<String> uncompletedProfiles) {
        this.uncompletedProfiles = uncompletedProfiles;
    }

    public List<String> getCompanyAddresses() {
        return companyAddresses;
    }

    public void setCompanyAddresses(List<String> companyAddresses) {
        this.companyAddresses = companyAddresses;
    }
}
