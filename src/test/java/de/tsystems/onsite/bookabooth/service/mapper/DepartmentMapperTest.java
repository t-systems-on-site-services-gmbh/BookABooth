package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.DepartmentAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.DepartmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class DepartmentMapperTest {

    private DepartmentMapper departmentMapper;

    @BeforeEach
    void setUp() {
        departmentMapper = new DepartmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDepartmentSample1();
        var actual = departmentMapper.toEntity(departmentMapper.toDto(expected));
        assertDepartmentAllPropertiesEquals(expected, actual);
    }
}
