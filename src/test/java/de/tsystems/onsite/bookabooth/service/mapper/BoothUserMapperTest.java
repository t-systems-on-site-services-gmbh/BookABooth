package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.BoothUserAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.BoothUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BoothUserMapperTest {

    private BoothUserMapper boothUserMapper;

    @BeforeEach
    void setUp() {
        boothUserMapper = new BoothUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBoothUserSample1();
        var actual = boothUserMapper.toEntity(boothUserMapper.toDto(expected));
        assertBoothUserAllPropertiesEquals(expected, actual);
    }
}
