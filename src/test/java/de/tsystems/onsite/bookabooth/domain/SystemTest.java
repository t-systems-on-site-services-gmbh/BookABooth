package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.SystemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class SystemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(System.class);
        System system1 = getSystemSample1();
        System system2 = new System();
        assertThat(system1).isNotEqualTo(system2);

        system2.setId(system1.getId());
        assertThat(system1).isEqualTo(system2);

        system2 = getSystemSample2();
        assertThat(system1).isNotEqualTo(system2);
    }
}
