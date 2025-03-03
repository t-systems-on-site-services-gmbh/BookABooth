package de.tsystems.onsite.bookabooth.service.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDTO {

    List<AdminChecklistDTO> checklist = new ArrayList<>();
    List<String> companies = new ArrayList<>(); // all mail addresses
    List<String> uncompletedProfiles = new ArrayList<>(); // mail addresses from companies with uncompleted profiles

    public List<AdminChecklistDTO> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<AdminChecklistDTO> checklist) {
        this.checklist = checklist;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    public List<String> getUncompletedProfiles() {
        return uncompletedProfiles;
    }

    public void setUncompletedProfiles(List<String> uncompletedProfiles) {
        this.uncompletedProfiles = uncompletedProfiles;
    }

    public AdminDashboardDTO() {}
}
