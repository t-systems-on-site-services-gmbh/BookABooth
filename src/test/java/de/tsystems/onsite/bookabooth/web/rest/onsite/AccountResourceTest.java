package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.tsystems.onsite.bookabooth.domain.*;
import de.tsystems.onsite.bookabooth.repository.*;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.web.rest.AccountResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mytest")
public class AccountResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BoothUserRepository boothUserRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Company testCompany;
    private BoothUser testBoothUser;
    private Booking testBooking;
    private Booth testBooth;

    @BeforeEach
    public void setup() {
        // Erstellen und Speichern von testEntities
        testUser = new User();
        testUser.setFirstName("Test User");
        testUser.setLogin("TestUser");
        testUser.setPassword("$2a$12$QL4lZ1Hw0jWyZS9atpxU2.W2xGj71vNIfilPP2DMqHbh.POsRt8kO");
        userRepository.saveAndFlush(testUser);

        testCompany = new Company();
        testCompany.setName("Test Company");
        companyRepository.saveAndFlush(testCompany);

        testBoothUser = new BoothUser();
        testBoothUser.setUser(testUser);
        testBoothUser.setCompany(testCompany);
        boothUserRepository.saveAndFlush(testBoothUser);

        testBooth = new Booth(); // Booth muss vorhanden sein, damit kein Fehler auftritt
        testBooth.setTitle("testBooth");
        testBooth.setAvailable(true);
        boothRepository.saveAndFlush(testBooth);

        testBooking = new Booking();
        testBooking.setCompany(testCompany);
        testBooking.setBooth(testBooth);
        bookingRepository.saveAndFlush(testBooking);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_USER")
    public void deleteUserCompanyBookingAndBoothUserAsUser() {
        // Verifizieren, dass Entities gespeichert wurden
        Assertions.assertNotNull(testBoothUser.getId());
        Assertions.assertNotNull(testUser.getId());
        Assertions.assertNotNull(testCompany.getId());
        Assertions.assertNotNull(testBooking.getId());

        // Löschen des Users, mitsamt der Dependencies
        userService.deleteAccount(testUser.getId());

        // Verifizieren, dass User, Company, BoothUser und Booking gelöscht wurden
        Assertions.assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        Assertions.assertFalse(userRepository.findById(testUser.getId()).isPresent());
        Assertions.assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
        Assertions.assertFalse(bookingRepository.findById(testBooking.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(password = "user", authorities = "ROLE_USER")
    public void deleteOverApiAndSucceed() throws Exception {
        // JSON für die Anfrage
        String requestJson = "{\"currentPassword\": \"user\"}";

        // API-Endpunkt aufrufen
        mockMvc
            .perform(
                delete("/api/account/delete-account/" + testUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf())
            )
            .andExpect(status().isOk());

        // Überprüfen, dass Einträge geleert wurden
        Assertions.assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        Assertions.assertFalse(userRepository.findById(testUser.getId()).isPresent());
        Assertions.assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
        Assertions.assertFalse(bookingRepository.findById(testBooking.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(password = "user", authorities = "ROLE_ADMIN")
    public void deleteOnlyAdminOverApiAndFail() throws Exception {
        String requestJson = "{\"currentPassword\": \"user\"}";

        mockMvc
            .perform(
                delete("/api/account/delete-account/" + testUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf())
            )
            .andExpect(status().isInternalServerError()); // Der einzige Admin kann nicht gelöscht werden
    }
}
