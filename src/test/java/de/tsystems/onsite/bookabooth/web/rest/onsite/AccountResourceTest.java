package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.domain.*;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.*;
import de.tsystems.onsite.bookabooth.security.AuthoritiesConstants;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.service.dto.*;
import de.tsystems.onsite.bookabooth.service.mapper.*;
import de.tsystems.onsite.bookabooth.web.rest.AccountResource;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private ObjectMapper om;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USER_LOGIN = "testUser";
    private static final String TEST_USER_PASSWORD = "testPassword";
    private User testUser;
    private Company testCompany;
    private BoothUser testBoothUser;
    private Booking confirmedBooking;
    private Booking canceledBooking;
    private UserProfileDTO userProfileDTO;

    @BeforeEach
    public void setup() {
        // Erstellen und Speichern von testEntities
        testUser = new User();
        testUser.setFirstName("Test User");
        testUser.setLastName("Initial");
        testUser.setLogin(TEST_USER_LOGIN);
        testUser.setEmail("test@localhost.de");
        testUser.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
        testUser.setActivated(true);
        userRepository.saveAndFlush(testUser);
        UserDTO testUserDTO = userMapper.userToUserDTO(testUser);

        // Booth muss vorhanden sein, damit kein Fehler auftritt
        Booth testBooth = new Booth();
        testBooth.setTitle("testBooth");
        testBooth.setAvailable(true);
        boothRepository.saveAndFlush(testBooth);

        testCompany = new Company();
        testCompany.setName("Test Company");
        companyRepository.save(testCompany);
        CompanyDTO testCompanyDTO = companyMapper.toDto(testCompany);

        testBoothUser = new BoothUser();
        testBoothUser.setUser(testUser);
        testBoothUser.setCompany(testCompany);
        boothUserRepository.saveAndFlush(testBoothUser);

        canceledBooking = new Booking();
        canceledBooking.setCompany(testCompany);
        canceledBooking.setBooth(testBooth);
        canceledBooking.setReceived(ZonedDateTime.now());
        canceledBooking.setConfirmed(ZonedDateTime.now());
        canceledBooking.setPrice(BigDecimal.valueOf(100));
        canceledBooking.setCancellationFee(BigDecimal.valueOf(25));
        canceledBooking.setStatus(BookingStatus.CANCELED);

        confirmedBooking = new Booking();
        confirmedBooking.setCompany(testCompany);
        confirmedBooking.setBooth(testBooth);
        confirmedBooking.setPrice(BigDecimal.valueOf(100));
        confirmedBooking.setReceived(ZonedDateTime.now());
        confirmedBooking.setConfirmed(ZonedDateTime.now());
        confirmedBooking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.saveAllAndFlush(List.of(confirmedBooking, canceledBooking));
        BookingDTO testBookingDTO = bookingMapper.toDto(confirmedBooking);

        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUser(testUserDTO);
        userProfileDTO.setCompany(testCompanyDTO);
        userProfileDTO.setBooking(testBookingDTO);
    }

    @Test
    @Transactional
    @WithMockUser(username = TEST_USER_LOGIN, authorities = "ROLE_USER")
    @DisplayName("prevent deletion of user with open billing")
    void openBilling() throws Exception {
        // Create PasswordChangeDTO
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword(TEST_USER_PASSWORD);

        // Perform delete request
        mockMvc
            .perform(
                delete("/api/account/delete-account/{id}", testUser.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passwordChangeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the user is not deleted
        assertThat(userRepository.findOneByLogin(TEST_USER_LOGIN)).isPresent();

        // set confirmedBooking to canceled and canceledBooking cancelationFee to null
        confirmedBooking.setStatus(BookingStatus.CANCELED);
        canceledBooking.setCancellationFee(null);
        bookingRepository.saveAllAndFlush(List.of(confirmedBooking, canceledBooking));

        // Perform delete request
        mockMvc
            .perform(
                delete("/api/account/delete-account/{id}", testUser.getId())
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(passwordChangeDTO))
            )
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = TEST_USER_LOGIN)
    public void deleteOverApiAndSucceed() throws Exception {
        // Löschen des Users, mitsamt der Dependencies
        userService.deleteAccount(testUser.getId());

        // Überprüfen, dass Einträge geleert wurden
        assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
        assertFalse(bookingRepository.findById(confirmedBooking.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(username = "lastAdmin", authorities = AuthoritiesConstants.ADMIN)
    public void atLeastOneAdminMustBePresent() throws Exception {
        // find all users with admin role
        List<User> admins = userRepository
            .findAll()
            .stream()
            .filter(user -> user.getAuthorities().stream().anyMatch(authority -> authority.getName().equals(AuthoritiesConstants.ADMIN)))
            .collect(Collectors.toList());
        userRepository.deleteAll(admins);

        // set admin authority for lastAdmin
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthoritiesConstants.ADMIN);

        User lastAdmin = new User();
        lastAdmin.setLogin("lastAdmin");
        lastAdmin.setEmail("last@admin.sw");
        lastAdmin.setFirstName("Last");
        lastAdmin.setLastName("Admin");
        lastAdmin.setPassword(RandomStringUtils.randomAlphanumeric(60));
        lastAdmin.setActivated(true);
        lastAdmin.setLangKey("en");
        lastAdmin.setAuthorities(Set.of(adminAuthority));
        lastAdmin = userRepository.saveAndFlush(lastAdmin);

        // one admin is present
        assertEquals(userRepository.countByAuthoritiesName(AuthoritiesConstants.ADMIN), 1);

        mockMvc
            .perform(delete("/api/account/delete-account/" + lastAdmin.getId()).contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isBadRequest());

        // Der einzige Admin kann nicht gelöscht werden
        assertTrue(userRepository.findById(lastAdmin.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.USER)
    public void testUpdateProfileOverApi() throws Exception {
        // Verifizieren, dass Entities gespeichert wurden
        assertNotNull(testBoothUser.getId());
        assertNotNull(testUser.getId());
        assertNotNull(testCompany.getId());
        assertNotNull(confirmedBooking.getId());
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

        mockMvc
            .perform(
                post("/api/account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestUserProfileDTO))
                    .with(csrf())
            )
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
            .andExpect(jsonPath("$.booking.id").value(confirmedBooking.getId()))
            .andExpect(jsonPath("$.booking.booth.id").value(confirmedBooking.getBooth().getId()));
    }
}
