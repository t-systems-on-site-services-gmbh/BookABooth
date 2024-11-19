package de.tsystems.onsite.bookabooth.service.dto;

import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;

public class ChecklistDTO {

    private Boolean verified; // Verifizierte E-Mail-Adresse?
    private Boolean address; // Rechnungsadresse hinterlegt?
    private Boolean logo; // Logo hochgeladen?
    private Boolean pressContact; // Pressekontakt hinterlegt?
    private Boolean companyDescription; // Firmenbeschreibung hinterlegt?
    private BookingStatus bookingStatus; // Buchung abgeschlossen?

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

    public Boolean getPressContact() {
        return pressContact;
    }

    public void setPressContact(Boolean pressContact) {
        this.pressContact = pressContact;
    }

    public Boolean getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(Boolean companyDescription) {
        this.companyDescription = companyDescription;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
