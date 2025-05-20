package de.tsystems.onsite.bookabooth.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO representing a password change required data - current and new password.
 */
public class PasswordChangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String currentPassword;

    @NotNull
    @Size(min = 12, max = 50)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\])[a-zA-Z\\d@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\]{12,50}"
    )
    private String newPassword;

    public PasswordChangeDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordChangeDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public @NotNull @Size(min = 12, max = 50) @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\])[a-zA-Z\\d@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\]{12,50}$"
    ) String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(
        @NotNull @Size(min = 12, max = 50) @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\])[a-zA-Z\\d@$!%*?+_=)(#><.:&\"'|~^/\\]\\[{}\\\\]{12,50}"
        ) String newPassword
    ) {
        this.newPassword = newPassword;
    }
}
