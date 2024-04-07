package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BookingTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.BoothTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.CompanyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booking.class);
        Booking booking1 = getBookingSample1();
        Booking booking2 = new Booking();
        assertThat(booking1).isNotEqualTo(booking2);

        booking2.setId(booking1.getId());
        assertThat(booking1).isEqualTo(booking2);

        booking2 = getBookingSample2();
        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void companyTest() throws Exception {
        Booking booking = getBookingRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        booking.setCompany(companyBack);
        assertThat(booking.getCompany()).isEqualTo(companyBack);

        booking.company(null);
        assertThat(booking.getCompany()).isNull();
    }

    @Test
    void boothTest() throws Exception {
        Booking booking = getBookingRandomSampleGenerator();
        Booth boothBack = getBoothRandomSampleGenerator();

        booking.setBooth(boothBack);
        assertThat(booking.getBooth()).isEqualTo(boothBack);

        booking.booth(null);
        assertThat(booking.getBooth()).isNull();
    }
}
