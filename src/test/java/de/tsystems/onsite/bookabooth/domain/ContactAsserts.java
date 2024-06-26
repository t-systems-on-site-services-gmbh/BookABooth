package de.tsystems.onsite.bookabooth.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertContactAllPropertiesEquals(Contact expected, Contact actual) {
        assertContactAutoGeneratedPropertiesEquals(expected, actual);
        assertContactAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertContactAllUpdatablePropertiesEquals(Contact expected, Contact actual) {
        assertContactUpdatableFieldsEquals(expected, actual);
        assertContactUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertContactAutoGeneratedPropertiesEquals(Contact expected, Contact actual) {
        assertThat(expected)
            .as("Verify Contact auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertContactUpdatableFieldsEquals(Contact expected, Contact actual) {
        assertThat(expected)
            .as("Verify Contact relevant properties")
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getLastName()).as("check lastName").isEqualTo(actual.getLastName()))
            .satisfies(e -> assertThat(e.getMail()).as("check mail").isEqualTo(actual.getMail()))
            .satisfies(e -> assertThat(e.getPhone()).as("check phone").isEqualTo(actual.getPhone()))
            .satisfies(e -> assertThat(e.getResponsibility()).as("check responsibility").isEqualTo(actual.getResponsibility()))
            .satisfies(e -> assertThat(e.getNote()).as("check note").isEqualTo(actual.getNote()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertContactUpdatableRelationshipsEquals(Contact expected, Contact actual) {
        assertThat(expected)
            .as("Verify Contact relationships")
            .satisfies(e -> assertThat(e.getCompanies()).as("check companies").isEqualTo(actual.getCompanies()));
    }
}
