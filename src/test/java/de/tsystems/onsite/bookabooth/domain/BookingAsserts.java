package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.AssertUtils.zonedDataTimeSameInstant;
import static org.assertj.core.api.Assertions.assertThat;

public class BookingAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookingAllPropertiesEquals(Booking expected, Booking actual) {
        assertBookingAutoGeneratedPropertiesEquals(expected, actual);
        assertBookingAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookingAllUpdatablePropertiesEquals(Booking expected, Booking actual) {
        assertBookingUpdatableFieldsEquals(expected, actual);
        assertBookingUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookingAutoGeneratedPropertiesEquals(Booking expected, Booking actual) {
        assertThat(expected)
            .as("Verify Booking auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookingUpdatableFieldsEquals(Booking expected, Booking actual) {
        assertThat(expected)
            .as("Verify Booking relevant properties")
            .satisfies(
                e ->
                    assertThat(e.getReceived())
                        .as("check received")
                        .usingComparator(zonedDataTimeSameInstant)
                        .isEqualTo(actual.getReceived())
            )
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookingUpdatableRelationshipsEquals(Booking expected, Booking actual) {
        assertThat(expected)
            .as("Verify Booking relationships")
            .satisfies(e -> assertThat(e.getCompany()).as("check company").isEqualTo(actual.getCompany()))
            .satisfies(e -> assertThat(e.getBooth()).as("check booth").isEqualTo(actual.getBooth()));
    }
}