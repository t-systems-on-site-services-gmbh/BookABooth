package de.tsystems.onsite.bookabooth.service.mapper;

import static de.tsystems.onsite.bookabooth.domain.BookingAsserts.*;
import static de.tsystems.onsite.bookabooth.domain.BookingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookingSample1();
        var actual = bookingMapper.toEntity(bookingMapper.toDto(expected));
        assertBookingAllPropertiesEquals(expected, actual);
    }
}
