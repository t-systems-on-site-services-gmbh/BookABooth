package de.tsystems.onsite.bookabooth.service.dto;

public class ExhibitorDTO {

    private String companyName;
    private String companyLogo;
    private String locationName;
    private String boothTitle;
    private Boolean exhibitorList;

    public ExhibitorDTO(String companyName, String companyLogo, String locationName, String boothTitle, Boolean exhibitorList) {
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.locationName = locationName;
        this.boothTitle = boothTitle;
        this.exhibitorList = exhibitorList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getBoothTitle() {
        return boothTitle;
    }

    public void setBoothTitle(String boothTitle) {
        this.boothTitle = boothTitle;
    }

    public Boolean isExhibitorList() {
        return exhibitorList;
    }

    public void setExhibitorList(Boolean exhibitorList) {
        this.exhibitorList = exhibitorList;
    }
}
