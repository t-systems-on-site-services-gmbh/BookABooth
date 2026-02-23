package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link de.tsystems.onsite.bookabooth.domain.Company} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanyDTO implements Serializable {

    private Long id;

    @Size(max = 200)
    private String name;

    private String mail;

    private String comment;

    private String billingAddressRow1;

    private String billingAddressRow2;

    private String billingAddressRow3;

    private String billingAddressRow4;

    private String billingZipCode;

    private String billingCity;

    private String logo;

    private String description;

    private Boolean waitingList;

    private Boolean exhibitorList;

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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBillingAddressRow1() {
        return billingAddressRow1;
    }

    public void setBillingAddressRow1(String billingAddressRow1) {
        this.billingAddressRow1 = billingAddressRow1;
    }

    public String getBillingAddressRow2() {
        return billingAddressRow2;
    }

    public void setBillingAddressRow2(String billingAddressRow2) {
        this.billingAddressRow2 = billingAddressRow2;
    }

    public String getBillingAddressRow3() {
        return billingAddressRow3;
    }

    public void setBillingAddressRow3(String billingAddressRow3) {
        this.billingAddressRow3 = billingAddressRow3;
    }

    public String getBillingAddressRow4() {
        return billingAddressRow4;
    }

    public void setBillingAddressRow4(String billingAddressRow4) {
        this.billingAddressRow4 = billingAddressRow4;
    }

    public String getBillingZipCode() {
        return billingZipCode;
    }

    public void setBillingZipCode(String billingZipCode) {
        this.billingZipCode = billingZipCode;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
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

    public Boolean getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(Boolean waitingList) {
        this.waitingList = waitingList;
    }

    public Boolean getExhibitorList() {
        return exhibitorList;
    }

    public void setExhibitorList(Boolean exhibitorList) {
        this.exhibitorList = exhibitorList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyDTO)) {
            return false;
        }

        CompanyDTO companyDTO = (CompanyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, companyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", billingAddressRow1='" + getBillingAddressRow1() + "'" +
            ", billingAddressRow2='" + getBillingAddressRow2() + "'" +
            ", billingAddressRow3='" + getBillingAddressRow3() + "'" +
            ", billingAddressRow4='" + getBillingAddressRow4() + "'" +
            ", billingZipCode='" + getBillingZipCode() + "'" +
            ", billingCity='" + getBillingCity() + "'" +
            ", comment='" + getComment() + "'" +
            ", logo='" + getLogo() + "'" +
            ", description='" + getDescription() + "'" +
            ", waitingList='" + getWaitingList() + "'" +
            ", exhibitorList='" + getExhibitorList() + "'" +
            "}";
    }
}
