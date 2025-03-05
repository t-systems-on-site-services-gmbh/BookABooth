package de.tsystems.onsite.bookabooth.service.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardDTO {

    List<AdminChecklistDTO> checklist = new ArrayList<>();

    public List<AdminChecklistDTO> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<AdminChecklistDTO> checklist) {
        this.checklist = checklist;
    }

    public AdminDashboardDTO() {}
}
