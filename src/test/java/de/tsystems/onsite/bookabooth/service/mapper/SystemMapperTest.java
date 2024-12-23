package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.SystemAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.SystemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class SystemMapperTest {

    private SystemMapper systemMapper;

    @BeforeEach
    void setUp() {
        systemMapper = new SystemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSystemSample1();
        var actual = systemMapper.toEntity(systemMapper.toDto(expected));
        assertSystemAllPropertiesEquals(expected, actual);
    }
}
