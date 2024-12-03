package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.tsystems.onsite.bookabooth.domain.*;
import de.tsystems.onsite.bookabooth.repository.*;
import de.tsystems.onsite.bookabooth.security.AuthoritiesConstants;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.service.dto.*;
import de.tsystems.onsite.bookabooth.service.mapper.*;
import de.tsystems.onsite.bookabooth.web.rest.AccountResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private BookingMapper bookingMapper;

    private User testUser;
    private Company testCompany;
    private BoothUser testBoothUser;
    private Booking testBooking;
    private UserProfileDTO userProfileDTO;

    @BeforeEach
    public void setup() {
        // Erstellen und Speichern von testEntities
        testUser = new User();
        testUser.setFirstName("Test User");
        testUser.setLastName("Initial");
        testUser.setLogin("testuser");
        testUser.setEmail("test@localhost.de");
        testUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
        userRepository.saveAndFlush(testUser);
        UserDTO testUserDTO = userMapper.userToUserDTO(testUser);

        testCompany = new Company();
        testCompany.setName("Test Company");
        companyRepository.saveAndFlush(testCompany);
        CompanyDTO testCompanyDTO = companyMapper.toDto(testCompany);

        testBoothUser = new BoothUser();
        testBoothUser.setUser(testUser);
        testBoothUser.setCompany(testCompany);
        boothUserRepository.saveAndFlush(testBoothUser);

        // Booth muss vorhanden sein, damit kein Fehler auftritt
        Booth testBooth = new Booth();
        testBooth.setTitle("testBooth");
        testBooth.setAvailable(true);
        boothRepository.saveAndFlush(testBooth);

        testBooking = new Booking();
        testBooking.setCompany(testCompany);
        testBooking.setBooth(testBooth);
        bookingRepository.saveAndFlush(testBooking);
        BookingDTO testBookingDTO = bookingMapper.toDto(testBooking);

        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUser(testUserDTO);
        userProfileDTO.setCompany(testCompanyDTO);
        userProfileDTO.setBooking(testBookingDTO);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    public void deleteUserCompanyBookingAndBoothUserAsUser() {
        // Verifizieren, dass Entities gespeichert wurden
        assertNotNull(testBoothUser.getId());
        assertNotNull(testUser.getId());
        assertNotNull(testCompany.getId());
        assertNotNull(testBooking.getId());

        // Löschen des Users, mitsamt der Dependencies
        userService.deleteAccount(testUser.getId());

        // Verifizieren, dass User, Company, BoothUser und Booking gelöscht wurden
        assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
        assertFalse(bookingRepository.findById(testBooking.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
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
        assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
        assertFalse(bookingRepository.findById(testBooking.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    public void deleteOnlyAdminOverApiAndFail() throws Exception {
        String requestJson = "{\"currentPassword\": \"user\"}";

        mockMvc
            .perform(
                delete("/api/account/delete-account/" + testUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .with(csrf())
            )
            .andExpect(status().isBadRequest());

        // Der einzige Admin kann nicht gelöscht werden
        assertTrue(userRepository.findById(testUser.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    public void testUpdateProfileOverApi() throws Exception {
        // Verifizieren, dass Entities gespeichert wurden
        assertNotNull(testBoothUser.getId());
        assertNotNull(testUser.getId());
        assertNotNull(testCompany.getId());
        assertNotNull(testBooking.getId());
        assertNotNull(userProfileDTO.getUser());

        // Neue Userinfos in DTO einfügen
        UserDTO userDTO = new UserDTO();
        userDTO.setId(testUser.getId());
        userDTO.setLogin(testUser.getLogin());
        userDTO.setEmail("testchange@localhost.de");
        userDTO.setFirstName("api Request");
        userDTO.setLastName("updated");

        // Neue Unternehmensinfos ins DTO einfügen
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(testCompany.getId());
        companyDTO.setName("Test Systems");

        UserProfileDTO requestUserProfileDTO = new UserProfileDTO();
        requestUserProfileDTO.setUser(userDTO);
        requestUserProfileDTO.setCompany(companyDTO);
        requestUserProfileDTO.setBooking(userProfileDTO.getBooking());

        // DTO zu JSON konvertieren
        ObjectMapper objectMapper = new ObjectMapper();
        String changesJson = objectMapper.writeValueAsString(requestUserProfileDTO);

        mockMvc
            .perform(post("/api/account").contentType(MediaType.APPLICATION_JSON).content(changesJson).with(csrf()))
            .andExpect(status().isOk());

        // Überprüfen, ob User in System aktualisiert wurde
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        Company updatedCompany = companyRepository.findById(testCompany.getId()).orElseThrow();
        assertEquals("api Request", updatedUser.getFirstName());
        assertEquals("updated", updatedUser.getLastName());
        assertEquals("Test Systems", updatedCompany.getName());
    }

    @Test
    @Transactional
    @WithMockUser(value = "testuser", authorities = AuthoritiesConstants.USER)
    public void testGetUserProfile() throws Exception {
        // Holt sich das Profil, welches in setup() eingerichtet wurde
        mockMvc
            .perform(get("/api/account").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.id").value(testUser.getId()))
            .andExpect(jsonPath("$.user.login").value(testUser.getLogin()))
            .andExpect(jsonPath("$.company.id").value(testCompany.getId()))
            .andExpect(jsonPath("$.booking.id").value(testBooking.getId()))
            .andExpect(jsonPath("$.booking.booth.id").value(testBooking.getBooth().getId()));
    }
}
