package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.CompanyTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.ContactTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contact.class);
        Contact contact1 = getContactSample1();
        Contact contact2 = new Contact();
        assertThat(contact1).isNotEqualTo(contact2);

        contact2.setId(contact1.getId());
        assertThat(contact1).isEqualTo(contact2);

        contact2 = getContactSample2();
        assertThat(contact1).isNotEqualTo(contact2);
    }

    @Test
    void companyTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        contact.addCompany(companyBack);
        assertThat(contact.getCompanies()).containsOnly(companyBack);

        contact.removeCompany(companyBack);
        assertThat(contact.getCompanies()).doesNotContain(companyBack);

        contact.companies(new HashSet<>(Set.of(companyBack)));
        assertThat(contact.getCompanies()).containsOnly(companyBack);

        contact.setCompanies(new HashSet<>());
        assertThat(contact.getCompanies()).doesNotContain(companyBack);
    }
}
