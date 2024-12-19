package de.tsystems.onsite.bookabooth.service;

import static org.junit.Assert.assertNotNull;

import de.tsystems.onsite.bookabooth.domain.*;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("mytest")
public class BookingServiceIT {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private BoothUserRepository boothUserRepository;

    @Test
    @Transactional
    public void testFindUserByBookingId() {
        Long bookingId = 1L;
        String userLogin = "login";
        String userPassword = "$2a$12$rIIB8Sv9G/X/wXUHgESVbuUPFik7Hj3FPsCwKv7OUiH2EVN0u4M9q";
        Booking booking = new Booking();
        Booth booth = new Booth();
        booth.setTitle("TestBooth");
        booth.setAvailable(true);
        booking.setId(bookingId);
        booking.setBooth(booth);
        Company company = new Company();
        BoothUser boothUser = new BoothUser();
        User user = new User();
        user.setLogin(userLogin);
        user.setPassword(userPassword);
        boothUser.setUser(user);
        boothUser.setCompany(company);
        booking.setCompany(company);

        boothRepository.saveAndFlush(booth);
        companyRepository.saveAndFlush(company);
        boothUserRepository.saveAndFlush(boothUser);

        bookingRepository.saveAndFlush(booking);

        User foundUser = bookingService.findUserByBookingId(bookingId);

        assertNotNull(foundUser);
    }
}
