package de.tsystems.onsite.bookabooth.service.dto;

public class AdminChecklistDTO {

    private long companyId;
    private String companyName;
    private boolean address; // Rechnungsadresse hinterlegt?
    private boolean logo; // Logo hochgeladen?
    private boolean phoneNumber; // Telefonnummer hinterlegt?
    private boolean companyDescription; // Firmenbeschreibung hinterlegt?
    private String booth;
    private String mail;

    public AdminChecklistDTO() {}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getBooth() {
        return booth;
    }

    public void setBooth(String booth) {
        this.booth = booth;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isMandatoryComplete() {
        return address;
    }
}
