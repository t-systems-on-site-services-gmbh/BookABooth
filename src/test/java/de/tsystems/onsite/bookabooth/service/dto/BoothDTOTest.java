package de.tsystems.onsite.bookabooth.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoothDTO.class);
        BoothDTO boothDTO1 = new BoothDTO();
        boothDTO1.setId(1L);
        BoothDTO boothDTO2 = new BoothDTO();
        assertThat(boothDTO1).isNotEqualTo(boothDTO2);
        boothDTO2.setId(boothDTO1.getId());
        assertThat(boothDTO1).isEqualTo(boothDTO2);
        boothDTO2.setId(2L);
        assertThat(boothDTO1).isNotEqualTo(boothDTO2);
        boothDTO1.setId(null);
        assertThat(boothDTO1).isNotEqualTo(boothDTO2);
    }
}
