package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.repository.UserRepository;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.web.rest.AccountResource;
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
@ActiveProfiles("test")
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
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Company testCompany;
    private BoothUser testBoothUser;

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
    }

    @Test
    @Transactional
    public void deletesUserBoothUserAndCompany() {
        // Verifizieren, dass Entities gespeichert wurden
        assertNotNull(testBoothUser.getId());
        assertNotNull(testUser.getId());
        assertNotNull(testCompany.getId());

        // Löschen des BoothUsers
        userService.deleteBoothUser(testBoothUser.getId());

        // Verifizieren, dass BoothUser, User und Company gelöscht wurden
        assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
    }

    @Test
    @Transactional
    @WithMockUser(username = "testUser", password = "user", authorities = "ROLE_USER")
    public void deleteBoothUserOverAPI() throws Exception {
        // JSON für die Anfrage
        String requestJson = "{currentPassword: \"user\"}";

        // API-Endpunkt aufrufen
        mockMvc
            .perform(
                delete("/api/account/delete-account/" + testBoothUser.getId()).contentType(MediaType.APPLICATION_JSON).content(requestJson)
            )
            .andExpect(status().isNoContent());

        // Überprüfen, dass Einträge geleert wurden
        assertFalse(boothUserRepository.findById(testBoothUser.getId()).isPresent());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
    }
}
