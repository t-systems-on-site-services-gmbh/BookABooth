package de.tsystems.onsite.bookabooth.web.rest;

import static de.tsystems.onsite.bookabooth.domain.BoothUserAsserts.*;
import static de.tsystems.onsite.bookabooth.web.rest.TestUtil.createUpdateProxyForBean;
import static de.tsystems.onsite.bookabooth.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.IntegrationTest;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BoothUserMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BoothUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BoothUserResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final UUID DEFAULT_VERIFICATION_CODE = UUID.randomUUID();
    private static final UUID UPDATED_VERIFICATION_CODE = UUID.randomUUID();

    private static final ZonedDateTime DEFAULT_VERIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VERIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_LAST_LOGIN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_LOGIN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_DISABLED = false;
    private static final Boolean UPDATED_DISABLED = true;

    private static final String ENTITY_API_URL = "/api/booth-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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
    private MockMvc restBoothUserMockMvc;

    private BoothUser boothUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BoothUser createEntity(EntityManager em) {
        BoothUser boothUser = new BoothUser()
            .phone(DEFAULT_PHONE)
            .note(DEFAULT_NOTE)
            .verificationCode(DEFAULT_VERIFICATION_CODE)
            .verified(DEFAULT_VERIFIED)
            .lastLogin(DEFAULT_LAST_LOGIN)
            .disabled(DEFAULT_DISABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        boothUser.setUser(user);
        return boothUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BoothUser createUpdatedEntity(EntityManager em) {
        BoothUser boothUser = new BoothUser()
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE)
            .verificationCode(UPDATED_VERIFICATION_CODE)
            .verified(UPDATED_VERIFIED)
            .lastLogin(UPDATED_LAST_LOGIN)
            .disabled(UPDATED_DISABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        boothUser.setUser(user);
        return boothUser;
    }

    @BeforeEach
    public void initTest() {
        boothUser = createEntity(em);
    }

    @Test
    @Transactional
    void createBoothUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);
        var returnedBoothUserDTO = om.readValue(
            restBoothUserMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothUserDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BoothUserDTO.class
        );

        // Validate the BoothUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBoothUser = boothUserMapper.toEntity(returnedBoothUserDTO);
        assertBoothUserUpdatableFieldsEquals(returnedBoothUser, getPersistedBoothUser(returnedBoothUser));

        assertBoothUserMapsIdRelationshipPersistedValue(boothUser, returnedBoothUser);
    }

    @Test
    @Transactional
    void createBoothUserWithExistingId() throws Exception {
        // Create the BoothUser with an existing ID
        boothUser.setId(1L);
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoothUserMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateBoothUserMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the boothUser
        BoothUser updatedBoothUser = boothUserRepository.findById(boothUser.getId()).orElseThrow();
        assertThat(updatedBoothUser).isNotNull();
        // Disconnect from session so that the updates on updatedBoothUser are not directly saved in db
        em.detach(updatedBoothUser);

        // Update the User with new association value
        updatedBoothUser.setUser(user);
        BoothUserDTO updatedBoothUserDTO = boothUserMapper.toDto(updatedBoothUser);
        assertThat(updatedBoothUserDTO).isNotNull();

        // Update the entity
        restBoothUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBoothUserDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBoothUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the BoothUser in the database
        List<BoothUser> boothUserList = boothUserRepository.findAll();
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        BoothUser testBoothUser = boothUserList.get(boothUserList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testBoothUser.getId()).isEqualTo(testBoothUser.getUser().getId());
    }

    @Test
    @Transactional
    void getAllBoothUsers() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        // Get all the boothUserList
        restBoothUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boothUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())))
            .andExpect(jsonPath("$.[*].verificationCode").value(hasItem(DEFAULT_VERIFICATION_CODE.toString())))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(sameInstant(DEFAULT_VERIFIED))))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(sameInstant(DEFAULT_LAST_LOGIN))))
            .andExpect(jsonPath("$.[*].disabled").value(hasItem(DEFAULT_DISABLED.booleanValue())));
    }

    @Test
    @Transactional
    void getBoothUser() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        // Get the boothUser
        restBoothUserMockMvc
            .perform(get(ENTITY_API_URL_ID, boothUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boothUser.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()))
            .andExpect(jsonPath("$.verificationCode").value(DEFAULT_VERIFICATION_CODE.toString()))
            .andExpect(jsonPath("$.verified").value(sameInstant(DEFAULT_VERIFIED)))
            .andExpect(jsonPath("$.lastLogin").value(sameInstant(DEFAULT_LAST_LOGIN)))
            .andExpect(jsonPath("$.disabled").value(DEFAULT_DISABLED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBoothUser() throws Exception {
        // Get the boothUser
        restBoothUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBoothUser() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boothUser
        BoothUser updatedBoothUser = boothUserRepository.findById(boothUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBoothUser are not directly saved in db
        em.detach(updatedBoothUser);
        updatedBoothUser
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE)
            .verificationCode(UPDATED_VERIFICATION_CODE)
            .verified(UPDATED_VERIFIED)
            .lastLogin(UPDATED_LAST_LOGIN)
            .disabled(UPDATED_DISABLED);
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(updatedBoothUser);

        restBoothUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boothUserDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBoothUserToMatchAllProperties(updatedBoothUser);
    }

    @Test
    @Transactional
    void putNonExistingBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boothUserDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoothUserWithPatch() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boothUser using partial update
        BoothUser partialUpdatedBoothUser = new BoothUser();
        partialUpdatedBoothUser.setId(boothUser.getId());

        partialUpdatedBoothUser.verified(UPDATED_VERIFIED);

        restBoothUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoothUser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoothUser))
            )
            .andExpect(status().isOk());

        // Validate the BoothUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoothUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBoothUser, boothUser),
            getPersistedBoothUser(boothUser)
        );
    }

    @Test
    @Transactional
    void fullUpdateBoothUserWithPatch() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boothUser using partial update
        BoothUser partialUpdatedBoothUser = new BoothUser();
        partialUpdatedBoothUser.setId(boothUser.getId());

        partialUpdatedBoothUser
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE)
            .verificationCode(UPDATED_VERIFICATION_CODE)
            .verified(UPDATED_VERIFIED)
            .lastLogin(UPDATED_LAST_LOGIN)
            .disabled(UPDATED_DISABLED);

        restBoothUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoothUser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoothUser))
            )
            .andExpect(status().isOk());

        // Validate the BoothUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoothUserUpdatableFieldsEquals(partialUpdatedBoothUser, getPersistedBoothUser(partialUpdatedBoothUser));
    }

    @Test
    @Transactional
    void patchNonExistingBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boothUserDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoothUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boothUser.setId(longCount.incrementAndGet());

        // Create the BoothUser
        BoothUserDTO boothUserDTO = boothUserMapper.toDto(boothUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothUserMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(boothUserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BoothUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoothUser() throws Exception {
        // Initialize the database
        boothUserRepository.saveAndFlush(boothUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the boothUser
        restBoothUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, boothUser.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return boothUserRepository.count();
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

    protected BoothUser getPersistedBoothUser(BoothUser boothUser) {
        return boothUserRepository.findById(boothUser.getId()).orElseThrow();
    }

    protected void assertPersistedBoothUserToMatchAllProperties(BoothUser expectedBoothUser) {
        assertBoothUserAllPropertiesEquals(expectedBoothUser, getPersistedBoothUser(expectedBoothUser));
    }

    protected void assertPersistedBoothUserToMatchUpdatableProperties(BoothUser expectedBoothUser) {
        assertBoothUserAllUpdatablePropertiesEquals(expectedBoothUser, getPersistedBoothUser(expectedBoothUser));
    }
}
