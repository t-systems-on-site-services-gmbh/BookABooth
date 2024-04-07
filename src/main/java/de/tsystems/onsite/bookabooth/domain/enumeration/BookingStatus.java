package de.tsystems.onsite.bookabooth.domain.enumeration;

/**
 * The BookingStatus enumeration.
 */
public enum BookingStatus {
    PREBOOKED("prebooked"),
    CONFIRMED("confirmed"),
    REJECTED("rejected");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
