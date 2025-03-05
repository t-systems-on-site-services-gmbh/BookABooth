package de.tsystems.onsite.bookabooth.web.rest.onsite;

import static de.tsystems.onsite.bookabooth.domain.BookingAsserts.assertBookingAllPropertiesEquals;
import static de.tsystems.onsite.bookabooth.domain.BookingAsserts.assertBookingAllUpdatablePropertiesEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.System;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.SystemRepository;
import de.tsystems.onsite.bookabooth.service.BoothUserService;
import de.tsystems.onsite.bookabooth.service.SystemService;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.service.dto.BookingDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserRegistrationDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BookingMapper;
import de.tsystems.onsite.bookabooth.web.rest.BookingResource;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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

/**
 * Integration tests for the {@link BookingResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mytest")
@WithMockUser
class BookingResourceIT {

    private static final ZonedDateTime DEFAULT_RECEIVED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RECEIVED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BookingStatus DEFAULT_STATUS = BookingStatus.PREBOOKED;
    private static final BookingStatus UPDATED_STATUS = BookingStatus.CONFIRMED;

    private static final String ENTITY_API_URL = "/api/bookings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingMockMvc;

    @Autowired
    private BoothUserRepository boothUserRepository;

    @Autowired
    private BoothUserService boothUserService;

    @Autowired
    private UserService userService;

    @Autowired
    SystemRepository systemRepository;

    @Autowired
    SystemService systemService;

    @BeforeEach
    void setup() {
        // Initialize the database
    }

    @Test
    @Transactional
    void blockABooth() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Booking
        var boothId = 1L; // Assuming a valid boothId
        var returnedBookingDTO = om.readValue(
            restBookingMockMvc
                .perform(post("/api/bookings/booth/{id}", boothId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookingDTO.class
        );

        // Validate the Booking in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void boothCanNotBeBooked2Times() throws Exception {
        // already booked booth with id 1
        this.blockABooth();

        long databaseSizeBeforeCreate = getRepositoryCount();

        // Create the Booking
        long boothId = 1L; // Assuming a valid boothId
        restBookingMockMvc
            .perform(post("/api/bookings/booth/{id}", boothId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("Booth already booked or blocked");

        // Validate the Booking in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void companyCanOnlyBook1Booth() throws Exception {
        // already booked booth with id 1
        this.blockABooth();

        long databaseSizeBeforeCreate = getRepositoryCount();

        // Create the Booking
        long boothId = 2L; // Assuming a valid boothId
        restBookingMockMvc
            .perform(post("/api/bookings/booth/{id}", boothId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("Company already booked or blocked a booth");

        // Validate the Booking in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(username = "user2", authorities = "ROLE_USER")
    void bookWithUncompletedProfileIsNotAllowed() throws Exception {
        // initialize the database
        this.registerAndActivateBoothUser(BoothUserResourceTest.createDefaultUserRegistrationDTO("user2"));

        long databaseSizeBeforeCreate = getRepositoryCount();

        // Create the Booking
        long boothId = 1L; // Assuming a valid boothId
        restBookingMockMvc
            .perform(post("/api/bookings/booth/{id}", boothId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("Profile is not complete");

        // Validate the Booking in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void SystemMustBeEnabledBeforeBlockingABooth() throws Exception {
        // initialize the database
        // System is not enabled
        systemService.disableSystem();

        long databaseSizeBeforeCreate = getRepositoryCount();

        // Create the Booking
        long boothId = 1L; // Assuming a valid boothId
        restBookingMockMvc
            .perform(post("/api/bookings/booth/{id}", boothId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("System is disabled");

        // Validate the Booking in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void confirmBooking() throws Exception {
        // initialize the database
        this.blockABooth();
        BoothUser boothUser = boothUserRepository.findByUserLogin("user").get();
        Booking booking = bookingRepository.findByCompanyIdOrderByReceivedDesc(boothUser.getCompany().getId()).get(0);

        assertEquals(BookingStatus.BLOCKED, booking.getStatus());

        // Create the Booking
        restBookingMockMvc
            .perform(patch("/api/bookings/confirm/{id}", booking.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @Transactional
    public void cancelBooking() throws Exception {
        // initialize the database
        this.confirmBooking();
        BoothUser boothUser = boothUserRepository.findByUserLogin("user").get();
        Booking booking = bookingRepository.findByCompanyIdOrderByReceivedDesc(boothUser.getCompany().getId()).get(0);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        // Create the Booking
        restBookingMockMvc
            .perform(patch("/api/bookings/cancel/{id}", booking.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isAccepted());

        assertEquals(BookingStatus.CANCELED, booking.getStatus());
    }

    @Test
    @Transactional
    void deleteBooking() throws Exception {
        // Initialize the database
        Booking booking = createBookingForUser("user");

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the booking
        restBookingMockMvc
            .perform(delete(ENTITY_API_URL_ID, booking.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(username = "user2", authorities = "ROLE_USER")
    void deleteBookingNotAllowed() throws Exception {
        // Initialize the database
        BoothUser user2 = registerAndActivateBoothUser(BoothUserResourceTest.createDefaultUserRegistrationDTO("user2"));
        Booking booking = createBookingForUser("user");

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the booking
        restBookingMockMvc
            .perform(delete(ENTITY_API_URL_ID, booking.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        assertSameRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = "ROLE_ADMIN")
    void deleteBookingAsAdmin() throws Exception {
        // Initialize the database
        BoothUser user2 = registerAndActivateBoothUser(BoothUserResourceTest.createDefaultUserRegistrationDTO("user2"));
        Booking booking = createBookingForUser("user");

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the booking
        restBookingMockMvc
            .perform(delete(ENTITY_API_URL_ID, booking.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    private Booking createBookingForUser(String user) {
        Optional<BoothUser> boothUser = boothUserRepository.findByUserLogin("user");

        Booking booking = new Booking();
        booking.setCompany(boothUser.get().getCompany());
        booking.setBooth(new Booth().id(1L));
        booking.setStatus(BookingStatus.BLOCKED);

        return bookingRepository.saveAndFlush(booking);
    }

    private BoothUser registerAndActivateBoothUser(UserRegistrationDTO userRegistrationDTO) {
        BoothUser bUser = userService.registerUser(userRegistrationDTO);
        userService.activateRegistration(bUser.getUser().getActivationKey());
        return bUser;
    }

    protected long getRepositoryCount() {
        return bookingRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Booking getPersistedBooking(Booking booking) {
        return bookingRepository.findById(booking.getId()).orElseThrow();
    }

    protected void assertPersistedBookingToMatchAllProperties(Booking expectedBooking) {
        assertBookingAllPropertiesEquals(expectedBooking, getPersistedBooking(expectedBooking));
    }

    protected void assertPersistedBookingToMatchUpdatableProperties(Booking expectedBooking) {
        assertBookingAllUpdatablePropertiesEquals(expectedBooking, getPersistedBooking(expectedBooking));
    }
}
