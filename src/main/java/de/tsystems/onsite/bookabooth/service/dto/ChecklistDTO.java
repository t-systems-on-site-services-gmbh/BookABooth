package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import java.util.Optional;

public class ChecklistDTO {

    private Boolean verified; // Verifizierte E-Mail-Adresse?
    private Boolean address; // Rechnungsadresse hinterlegt?
    private Boolean logo; // Logo hochgeladen?
    private Boolean phoneNumber; // Telefonnummer hinterlegt?
    private Boolean companyDescription; // Firmenbeschreibung hinterlegt?
    private Optional<BookingStatus> bookingStatus; // Buchung abgeschlossen?

    public ChecklistDTO() {}

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getAddress() {
        return address;
    }

    public void setAddress(Boolean address) {
        this.address = address;
    }

    public Boolean getLogo() {
        return logo;
    }

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }

    public Boolean getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Boolean phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(Boolean companyDescription) {
        this.companyDescription = companyDescription;
    }

    public Optional<BookingStatus> getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Optional<BookingStatus> bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
