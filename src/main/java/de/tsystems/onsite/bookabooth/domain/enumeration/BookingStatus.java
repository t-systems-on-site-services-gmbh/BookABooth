package de.tsystems.onsite.bookabooth.domain.enumeration;

/**
 * The BookingStatus enumeration.
 */
public enum BookingStatus {
    BLOCKED("blocked"),
    PREBOOKED("prebooked"),
    CONFIRMED("confirmed"),
    CANCELED("canceled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
