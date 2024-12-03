package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.BoothTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.LocationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class LocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Location.class);
        Location location1 = getLocationSample1();
        Location location2 = new Location();
        assertThat(location1).isNotEqualTo(location2);

        location2.setId(location1.getId());
        assertThat(location1).isEqualTo(location2);

        location2 = getLocationSample2();
        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    void boothsTest() throws Exception {
        Location location = getLocationRandomSampleGenerator();
        Booth boothBack = getBoothRandomSampleGenerator();

        location.addBooths(boothBack);
        assertThat(location.getBooths()).containsOnly(boothBack);
        assertThat(boothBack.getLocation()).isEqualTo(location);

        location.removeBooths(boothBack);
        assertThat(location.getBooths()).doesNotContain(boothBack);
        assertThat(boothBack.getLocation()).isNull();

        location.booths(new HashSet<>(Set.of(boothBack)));
        assertThat(location.getBooths()).containsOnly(boothBack);
        assertThat(boothBack.getLocation()).isEqualTo(location);

        location.setBooths(new HashSet<>());
        assertThat(location.getBooths()).doesNotContain(boothBack);
        assertThat(boothBack.getLocation()).isNull();
    }
}
