package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.Authority;
import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import java.util.Set;
import java.util.stream.Collectors;

/**

*    A DTO representing a user
*    with his company infos

 */

public class UserProfileDTO {

    private long Id;
    private String login;
    private String companyName;
    private String billingAddress;
    private String firstName;
    private String lastName;
    private String email;
    private String description;
    private String logo;
    private boolean exhibitorList;
    private BookingStatus status;
    private Boolean waitingList;
    private Set<String> authorities;

    public UserProfileDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserProfileDTO(User user, Company company, Booking booking) {
        this.Id = user.getId();
        this.login = user.getLogin();
        this.companyName = company.getName();
        this.billingAddress = company.getBillingAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.description = company.getDescription();
        this.logo = company.getLogo();
        this.exhibitorList = company.getExhibitorList();
        this.status = booking.getStatus();
        this.waitingList = company.getWaitingList();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean getExhibitorList() {
        return exhibitorList;
    }

    public void setExhibitorList(Boolean exhibitorList) {
        this.exhibitorList = exhibitorList;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Boolean getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(Boolean waitingList) {
        this.waitingList = waitingList;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
