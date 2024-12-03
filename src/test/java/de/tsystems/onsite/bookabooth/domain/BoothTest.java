package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BoothTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.LocationTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.ServicePackageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booth.class);
        Booth booth1 = getBoothSample1();
        Booth booth2 = new Booth();
        assertThat(booth1).isNotEqualTo(booth2);

        booth2.setId(booth1.getId());
        assertThat(booth1).isEqualTo(booth2);

        booth2 = getBoothSample2();
        assertThat(booth1).isNotEqualTo(booth2);
    }

    @Test
    void locationTest() throws Exception {
        Booth booth = getBoothRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        booth.setLocation(locationBack);
        assertThat(booth.getLocation()).isEqualTo(locationBack);

        booth.location(null);
        assertThat(booth.getLocation()).isNull();
    }

    @Test
    void servicePackageTest() throws Exception {
        Booth booth = getBoothRandomSampleGenerator();
        ServicePackage servicePackageBack = getServicePackageRandomSampleGenerator();

        booth.addServicePackage(servicePackageBack);
        assertThat(booth.getServicePackages()).containsOnly(servicePackageBack);
        assertThat(servicePackageBack.getBooths()).containsOnly(booth);

        booth.removeServicePackage(servicePackageBack);
        assertThat(booth.getServicePackages()).doesNotContain(servicePackageBack);
        assertThat(servicePackageBack.getBooths()).doesNotContain(booth);

        booth.servicePackages(new HashSet<>(Set.of(servicePackageBack)));
        assertThat(booth.getServicePackages()).containsOnly(servicePackageBack);
        assertThat(servicePackageBack.getBooths()).containsOnly(booth);

        booth.setServicePackages(new HashSet<>());
        assertThat(booth.getServicePackages()).doesNotContain(servicePackageBack);
        assertThat(servicePackageBack.getBooths()).doesNotContain(booth);
    }
}
