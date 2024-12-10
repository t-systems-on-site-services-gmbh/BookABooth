package de.tsystems.onsite.bookabooth.service.dto;

import java.util.Set;

/**

*    A DTO representing a user
*    with his company infos

 */

public class UserProfileDTO {

    private UserDTO user;
    private CompanyDTO company;
    private BookingDTO booking;
    private Set<String> authorities;
    private String phoneNumber;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserProfileDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserProfileDTO(UserDTO user, CompanyDTO company, BookingDTO booking, Set<String> authorities, String phoneNumber) {
        this.user = user;
        this.company = company;
        this.booking = booking;
        this.authorities = authorities;
        this.phoneNumber = phoneNumber;
    }
}
