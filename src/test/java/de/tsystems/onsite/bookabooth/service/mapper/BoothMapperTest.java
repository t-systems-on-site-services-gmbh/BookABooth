package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.BoothAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.BoothTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothMapperTest {

    private BoothMapper boothMapper;

    @BeforeEach
    void setUp() {
        boothMapper = new BoothMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBoothSample1();
        var actual = boothMapper.toEntity(boothMapper.toDto(expected));
        assertBoothAllPropertiesEquals(expected, actual);
    }
}
