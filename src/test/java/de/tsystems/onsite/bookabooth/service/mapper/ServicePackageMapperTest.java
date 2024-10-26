package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.ServicePackageAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.ServicePackageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicePackageMapperTest {

    private ServicePackageMapper servicePackageMapper;

    @BeforeEach
    void setUp() {
        servicePackageMapper = new ServicePackageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServicePackageSample1();
        var actual = servicePackageMapper.toEntity(servicePackageMapper.toDto(expected));
        assertServicePackageAllPropertiesEquals(expected, actual);
    }
}
