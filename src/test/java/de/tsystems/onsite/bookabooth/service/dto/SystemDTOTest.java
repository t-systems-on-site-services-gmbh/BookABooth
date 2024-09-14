package de.tsystems.onsite.bookabooth.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SystemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemDTO.class);
        SystemDTO systemDTO1 = new SystemDTO();
        systemDTO1.setId(1L);
        SystemDTO systemDTO2 = new SystemDTO();
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
        systemDTO2.setId(systemDTO1.getId());
        assertThat(systemDTO1).isEqualTo(systemDTO2);
        systemDTO2.setId(2L);
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
        systemDTO1.setId(null);
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
    }
}
