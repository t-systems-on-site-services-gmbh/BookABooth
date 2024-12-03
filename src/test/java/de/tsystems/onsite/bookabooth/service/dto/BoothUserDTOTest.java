package de.tsystems.onsite.bookabooth.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoothUserDTO.class);
        BoothUserDTO boothUserDTO1 = new BoothUserDTO();
        boothUserDTO1.setId(1L);
        BoothUserDTO boothUserDTO2 = new BoothUserDTO();
        assertThat(boothUserDTO1).isNotEqualTo(boothUserDTO2);
        boothUserDTO2.setId(boothUserDTO1.getId());
        assertThat(boothUserDTO1).isEqualTo(boothUserDTO2);
        boothUserDTO2.setId(2L);
        assertThat(boothUserDTO1).isNotEqualTo(boothUserDTO2);
        boothUserDTO1.setId(null);
        assertThat(boothUserDTO1).isNotEqualTo(boothUserDTO2);
    }
}
