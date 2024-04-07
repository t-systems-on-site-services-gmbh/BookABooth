package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BoothTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.ServicePackageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePackageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServicePackage.class);
        ServicePackage servicePackage1 = getServicePackageSample1();
        ServicePackage servicePackage2 = new ServicePackage();
        assertThat(servicePackage1).isNotEqualTo(servicePackage2);

        servicePackage2.setId(servicePackage1.getId());
        assertThat(servicePackage1).isEqualTo(servicePackage2);

        servicePackage2 = getServicePackageSample2();
        assertThat(servicePackage1).isNotEqualTo(servicePackage2);
    }

    @Test
    void boothTest() throws Exception {
        ServicePackage servicePackage = getServicePackageRandomSampleGenerator();
        Booth boothBack = getBoothRandomSampleGenerator();

        servicePackage.addBooth(boothBack);
        assertThat(servicePackage.getBooths()).containsOnly(boothBack);

        servicePackage.removeBooth(boothBack);
        assertThat(servicePackage.getBooths()).doesNotContain(boothBack);

        servicePackage.booths(new HashSet<>(Set.of(boothBack)));
        assertThat(servicePackage.getBooths()).containsOnly(boothBack);

        servicePackage.setBooths(new HashSet<>());
        assertThat(servicePackage.getBooths()).doesNotContain(boothBack);
    }
}
