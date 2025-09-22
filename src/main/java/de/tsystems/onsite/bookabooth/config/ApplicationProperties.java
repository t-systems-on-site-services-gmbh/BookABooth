package de.tsystems.onsite.bookabooth.config;

import java.nio.file.Paths;
import java.util.Date;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Properties specific to Bookabooth.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    // jhipster-needle-application-properties-property-class

    private String uploadFolder;

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    private String workDir;

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getUploadFolderFull() {
        return Paths.get(workDir, uploadFolder).toString();
    }

    private Integer cancellationReimbursement;

    public Integer getCancellationReimbursement() {
        return cancellationReimbursement == null ? 100 : cancellationReimbursement;
    }

    public void setCancellationReimbursement(Integer cancellationReimbursement) {
        this.cancellationReimbursement = cancellationReimbursement;
    }

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date cancellationReimbursementUntil;

    public Date getCancellationReimbursementUntil() {
        return cancellationReimbursementUntil;
    }

    public void setCancellationReimbursementUntil(Date cancellationReimbursementUntil) {
        this.cancellationReimbursementUntil = cancellationReimbursementUntil;
    }

    private Long bookingRemovalInterval;

    public Long getBookingRemovalInterval() {
        return bookingRemovalInterval;
    }

    public void setBookingRemovalInterval(Long bookingRemovalInterval) {
        this.bookingRemovalInterval = bookingRemovalInterval;
    }

    private Long passwordResetTokenValidity;

    public Long getPasswordResetTokenValidity() {
        return passwordResetTokenValidity;
    }

    public void setPasswordResetTokenValidity(Long passwordResetTokenValidity) {
        this.passwordResetTokenValidity = passwordResetTokenValidity;
    }

    private String supportEmail;

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    private Long userRemovalThreshold;

    public Long getUserRemovalThreshold() {
        return userRemovalThreshold;
    }

    public void setUserRemovalThreshold(Long userRemovalThreshold) {
        this.userRemovalThreshold = userRemovalThreshold;
    }
}
