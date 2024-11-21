package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.service.dto.UserRegistrationDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BoothUserMapper;
import de.tsystems.onsite.bookabooth.web.rest.BoothUserResource;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntPredicate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BoothUserResource} REST controller.
 */
@SpringBootTest
@ActiveProfiles("mytest")
@AutoConfigureMockMvc
class BoothUserResourceTest {

    private static final UUID DEFAULT_VERIFICATION_CODE = UUID.randomUUID();
    private static final UUID UPDATED_VERIFICATION_CODE = UUID.randomUUID();

    private static final ZonedDateTime DEFAULT_VERIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VERIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_LAST_LOGIN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_LOGIN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String REGISTER_URL = "/api/register";
    private static final String REGISTER_CONFIRM_URL = "/api/activate";
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoothUserRepository boothUserRepository;

    @Autowired
    private BoothUserMapper boothUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMockMvc;

    @Test
    @Transactional
    void registerUserAndActivateTest() throws Exception {
        // Create Registration DTO
        UserRegistrationDTO userRegistrationDTO = createDefaultUserRegistrationDTO(USER1);

        // Register the user
        restMockMvc
            .perform(
                post(REGISTER_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userRegistrationDTO))
            )
            .andExpect(status().isCreated());

        // assert db contains the user
        Optional<BoothUser> boothUser = boothUserRepository.findByUserLogin(userRegistrationDTO.getLogin());
        assertTrue(boothUser.isPresent());
        assertIsEqual(userRegistrationDTO, boothUser.get());
        assertFalse(boothUser.get().getUser().isActivated());

        // Activate the user
        restMockMvc
            .perform(get(REGISTER_CONFIRM_URL).param("key", boothUser.get().getUser().getActivationKey()))
            .andExpect(status().isOk());

        // assert the user is activated
        assertTrue(boothUser.get().getUser().isActivated());
    }

    @Test
    @Transactional
    void registerUserWithoutTermsAcceptedFailsTest() throws Exception {
        // Create Registration DTO
        UserRegistrationDTO userRegistrationDTO = createDefaultUserRegistrationDTO(USER2);
        userRegistrationDTO.setTermsAccepted(false);

        // Register the user
        restMockMvc
            .perform(
                post(REGISTER_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // assert db do not contain the user
        Optional<BoothUser> boothUser = boothUserRepository.findByUserLogin(userRegistrationDTO.getLogin());
        assertTrue(boothUser.isEmpty());
    }

    @Test
    @Transactional
    void OnlyOneRelationForUserAndCompanyTest() throws Exception {
        // create activated user
        this.registerUserAndActivateTest();

        // Create Registration DTO
        UserRegistrationDTO userRegistrationDTO = createDefaultUserRegistrationDTO(USER2);
        userRegistrationDTO.setCompanyName(USER1 + "-company");

        // Register the user
        restMockMvc
            .perform(
                post(REGISTER_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userRegistrationDTO))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void loginNameShouldBeUniqeTest() throws Exception {
        // create activated user
        this.registerUserAndActivateTest();

        // Create Registration DTO
        UserRegistrationDTO userRegistrationDTO = createDefaultUserRegistrationDTO(USER1);

        // Register the user
        restMockMvc
            .perform(
                post(REGISTER_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userRegistrationDTO))
            )
            .andExpect(status().isBadRequest());
    }

    private void assertIsEqual(UserRegistrationDTO userRegistrationDTO, BoothUser boothUser) {
        assertNotNull(boothUser);
        assertNotNull(userRegistrationDTO);
        assertEquals(userRegistrationDTO.getLogin(), boothUser.getUser().getLogin());
        assertEquals(userRegistrationDTO.getCompanyName(), boothUser.getCompany().getName());
        assertEquals(userRegistrationDTO.getEmail(), boothUser.getUser().getEmail());
        assertThat(boothUser.getUser().getPassword()).isNotNull(); // todo improvement: check password with password encoder
    }

    private static @NotNull UserRegistrationDTO createDefaultUserRegistrationDTO(String name) {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setLogin(name);
        userRegistrationDTO.setCompanyName(name + "-company");
        userRegistrationDTO.setEmail(name + "@onsite.de");
        userRegistrationDTO.setPassword("password");
        userRegistrationDTO.setTermsAccepted(true);
        return userRegistrationDTO;
    }
}
