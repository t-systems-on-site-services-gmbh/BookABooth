package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import java.util.Optional;

public class ChecklistDTO {

    private boolean verified; // Verifizierte E-Mail-Adresse?
    private boolean address; // Rechnungsadresse hinterlegt?
    private boolean logo; // Logo hochgeladen?
    private boolean phoneNumber; // Telefonnummer hinterlegt?
    private boolean companyDescription; // Firmenbeschreibung hinterlegt?
    private Optional<BookingStatus> bookingStatus; // Buchung abgeschlossen?
    private Long boothId;

    public ChecklistDTO() {}

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean getAddress() {
        return address;
    }

    public void setAddress(boolean address) {
        this.address = address;
    }

    public boolean getLogo() {
        return logo;
    }

    public void setLogo(boolean logo) {
        this.logo = logo;
    }

    public boolean getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(boolean phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(boolean companyDescription) {
        this.companyDescription = companyDescription;
    }

    public Optional<BookingStatus> getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Optional<BookingStatus> bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Long getBoothId() {
        return boothId;
    }

    public void setBoothId(Long id) {
        boothId = id;
    }

    public boolean isMandatoryComplete() {
        return verified && address;
    }
}
