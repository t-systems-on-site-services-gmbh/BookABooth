package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BookingTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.CompanyTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.ContactTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.DepartmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompanyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = getCompanySample1();
        Company company2 = new Company();
        assertThat(company1).isNotEqualTo(company2);

        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);

        company2 = getCompanySample2();
        assertThat(company1).isNotEqualTo(company2);
    }

    @Test
    void bookingsTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        company.addBookings(bookingBack);
        assertThat(company.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getCompany()).isEqualTo(company);

        company.removeBookings(bookingBack);
        assertThat(company.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getCompany()).isNull();

        company.bookings(new HashSet<>(Set.of(bookingBack)));
        assertThat(company.getBookings()).containsOnly(bookingBack);
        assertThat(bookingBack.getCompany()).isEqualTo(company);

        company.setBookings(new HashSet<>());
        assertThat(company.getBookings()).doesNotContain(bookingBack);
        assertThat(bookingBack.getCompany()).isNull();
    }

    @Test
    void departmentTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        company.addDepartment(departmentBack);
        assertThat(company.getDepartments()).containsOnly(departmentBack);
        assertThat(departmentBack.getCompanies()).containsOnly(company);

        company.removeDepartment(departmentBack);
        assertThat(company.getDepartments()).doesNotContain(departmentBack);
        assertThat(departmentBack.getCompanies()).doesNotContain(company);

        company.departments(new HashSet<>(Set.of(departmentBack)));
        assertThat(company.getDepartments()).containsOnly(departmentBack);
        assertThat(departmentBack.getCompanies()).containsOnly(company);

        company.setDepartments(new HashSet<>());
        assertThat(company.getDepartments()).doesNotContain(departmentBack);
        assertThat(departmentBack.getCompanies()).doesNotContain(company);
    }

    @Test
    void contactTest() throws Exception {
        Company company = getCompanyRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        company.addContact(contactBack);
        assertThat(company.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getCompanies()).containsOnly(company);

        company.removeContact(contactBack);
        assertThat(company.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getCompanies()).doesNotContain(company);

        company.contacts(new HashSet<>(Set.of(contactBack)));
        assertThat(company.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getCompanies()).containsOnly(company);

        company.setContacts(new HashSet<>());
        assertThat(company.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getCompanies()).doesNotContain(company);
    }
}
