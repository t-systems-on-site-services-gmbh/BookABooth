package de.tsystems.onsite.bookabooth.service.dto;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AdminDashboardDTO {

    List<AdminChecklistDTO> checklist = new ArrayList<>();
    List<String> mailOfAllCompanies = new ArrayList<>(); // all mail addresses
    List<String> mailOfUncompletedProfiles = new ArrayList<>(); // mail addresses from companies with uncompleted profiles

    public List<AdminChecklistDTO> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<AdminChecklistDTO> checklist) {
        this.checklist = checklist;
    }

    public String getMailOfAllCompanies() {
        return StringUtils.join(mailOfAllCompanies, ";");
    }

    public void addMailOfAllCompanies(String mailAddress) {
        this.mailOfAllCompanies.add(mailAddress);
    }

    public String getMailOfUncompletedProfiles() {
        return StringUtils.join(mailOfUncompletedProfiles, ";");
    }

    public void addMailOfUncompletedProfiles(String mailAddress) {
        this.mailOfUncompletedProfiles.add(mailAddress);
    }

    public AdminDashboardDTO() {}
}
