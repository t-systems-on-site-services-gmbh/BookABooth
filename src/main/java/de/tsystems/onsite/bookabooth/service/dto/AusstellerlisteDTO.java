package de.tsystems.onsite.bookabooth.service.dto;

public class AusstellerlisteDTO {

    private Long id;
    private String name;
    private String logo;
    private String description;
    private String boothTitle;

    public AusstellerlisteDTO(Long id, String name, String logo, String description, String boothTitle) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.boothTitle = boothTitle;
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

    public String getBoothTitle() {
        return boothTitle;
    }

    public void setBoothTitle(String boothTitle) {
        this.boothTitle = boothTitle;
    }
}
