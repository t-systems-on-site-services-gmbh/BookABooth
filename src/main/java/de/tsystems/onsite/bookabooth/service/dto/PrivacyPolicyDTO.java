package de.tsystems.onsite.bookabooth.service.dto;

import java.time.ZonedDateTime;

public class PrivacyPolicyDTO {

    private Long id;
    private ZonedDateTime fromDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(ZonedDateTime fromDate) {
        this.fromDate = fromDate;
    }
}
