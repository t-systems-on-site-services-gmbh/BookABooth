package de.tsystems.onsite.bookabooth.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCompanyAllPropertiesEquals(Company expected, Company actual) {
        assertCompanyAutoGeneratedPropertiesEquals(expected, actual);
        assertCompanyAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCompanyAllUpdatablePropertiesEquals(Company expected, Company actual) {
        assertCompanyUpdatableFieldsEquals(expected, actual);
        assertCompanyUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCompanyAutoGeneratedPropertiesEquals(Company expected, Company actual) {
        assertThat(expected)
            .as("Verify Company auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCompanyUpdatableFieldsEquals(Company expected, Company actual) {
        assertThat(expected)
            .as("Verify Company relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getMail()).as("check mail").isEqualTo(actual.getMail()))
            .satisfies(e -> assertThat(e.getBillingAddress()).as("check billingAddress").isEqualTo(actual.getBillingAddress()))
            .satisfies(e -> assertThat(e.getLogo()).as("check logo").isEqualTo(actual.getLogo()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getWaitingList()).as("check waitingList").isEqualTo(actual.getWaitingList()))
            .satisfies(e -> assertThat(e.getExhibitorList()).as("check exhibitorList").isEqualTo(actual.getExhibitorList()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCompanyUpdatableRelationshipsEquals(Company expected, Company actual) {
        assertThat(expected)
            .as("Verify Company relationships")
            .satisfies(e -> assertThat(e.getDepartments()).as("check departments").isEqualTo(actual.getDepartments()))
            .satisfies(e -> assertThat(e.getContacts()).as("check contacts").isEqualTo(actual.getContacts()));
    }
}