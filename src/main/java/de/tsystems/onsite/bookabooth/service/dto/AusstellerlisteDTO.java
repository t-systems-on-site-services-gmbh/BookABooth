package de.tsystems.onsite.bookabooth.service.dto;

public class AusstellerlisteDTO {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private Boolean exhibitorList;
    private String boothTitles;

    public AusstellerlisteDTO(Long id, String name, String logo, String description, Boolean exhibitorList, String boothTitles) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.exhibitorList = exhibitorList;
        this.boothTitles = boothTitles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getExhibitorList() {
        return exhibitorList;
    }

    public void setExhibitorList(Boolean exhibitorList) {
        this.exhibitorList = exhibitorList;
    }

    public String getBoothTitles() {
        return boothTitles;
    }

    public void setBoothTitles(String boothTitles) {
        this.boothTitles = boothTitles;
    }
}
