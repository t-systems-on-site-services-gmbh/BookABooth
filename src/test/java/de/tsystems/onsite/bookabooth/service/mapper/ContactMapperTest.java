package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.ContactAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.ContactTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactMapperTest {

    private ContactMapper contactMapper;

    @BeforeEach
    void setUp() {
        contactMapper = new ContactMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getContactSample1();
        var actual = contactMapper.toEntity(contactMapper.toDto(expected));
        assertContactAllPropertiesEquals(expected, actual);
    }
}
