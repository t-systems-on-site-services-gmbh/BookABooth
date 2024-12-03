package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BoothUserTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.CompanyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoothUser.class);
        BoothUser boothUser1 = getBoothUserSample1();
        BoothUser boothUser2 = new BoothUser();
        assertThat(boothUser1).isNotEqualTo(boothUser2);

        boothUser2.setId(boothUser1.getId());
        assertThat(boothUser1).isEqualTo(boothUser2);

        boothUser2 = getBoothUserSample2();
        assertThat(boothUser1).isNotEqualTo(boothUser2);
    }

    @Test
    void companyTest() throws Exception {
        BoothUser boothUser = getBoothUserRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        boothUser.setCompany(companyBack);
        assertThat(boothUser.getCompany()).isEqualTo(companyBack);

        boothUser.company(null);
        assertThat(boothUser.getCompany()).isNull();
    }
}
