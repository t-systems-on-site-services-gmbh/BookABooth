package de.tsystems.onsite.bookabooth.web.rest;

import static de.tsystems.onsite.bookabooth.domain.SystemAsserts.*;
import static de.tsystems.onsite.bookabooth.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.IntegrationTest;
import de.tsystems.onsite.bookabooth.domain.System;
import de.tsystems.onsite.bookabooth.repository.SystemRepository;
import de.tsystems.onsite.bookabooth.service.dto.SystemDTO;
import de.tsystems.onsite.bookabooth.service.mapper.SystemMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
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
 * Integration tests for the {@link SystemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SystemResourceIT {

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/systems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private SystemMapper systemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemMockMvc;

    private System system;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static System createEntity(EntityManager em) {
        System system = new System().enabled(DEFAULT_ENABLED);
        return system;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static System createUpdatedEntity(EntityManager em) {
        System system = new System().enabled(UPDATED_ENABLED);
        return system;
    }

    @BeforeEach
    public void initTest() {
        system = createEntity(em);
    }

    @Test
    @Transactional
    void createSystem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);
        var returnedSystemDTO = om.readValue(
            restSystemMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SystemDTO.class
        );

        // Validate the System in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSystem = systemMapper.toEntity(returnedSystemDTO);
        assertSystemUpdatableFieldsEquals(returnedSystem, getPersistedSystem(returnedSystem));
    }

    @Test
    @Transactional
    void createSystemWithExistingId() throws Exception {
        // Create the System with an existing ID
        system.setId(1L);
        SystemDTO systemDTO = systemMapper.toDto(system);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSystems() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        // Get all the systemList
        restSystemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(system.getId().intValue())))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())));
    }

    @Test
    @Transactional
    void getSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        // Get the system
        restSystemMockMvc
            .perform(get(ENTITY_API_URL_ID, system.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(system.getId().intValue()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingSystem() throws Exception {
        // Get the system
        restSystemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the system
        System updatedSystem = systemRepository.findById(system.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSystem are not directly saved in db
        em.detach(updatedSystem);
        updatedSystem.enabled(UPDATED_ENABLED);
        SystemDTO systemDTO = systemMapper.toDto(updatedSystem);

        restSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isOk());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSystemToMatchAllProperties(updatedSystem);
    }

    @Test
    @Transactional
    void putNonExistingSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(systemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSystemWithPatch() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the system using partial update
        System partialUpdatedSystem = new System();
        partialUpdatedSystem.setId(system.getId());

        restSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSystem))
            )
            .andExpect(status().isOk());

        // Validate the System in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSystemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSystem, system), getPersistedSystem(system));
    }

    @Test
    @Transactional
    void fullUpdateSystemWithPatch() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the system using partial update
        System partialUpdatedSystem = new System();
        partialUpdatedSystem.setId(system.getId());

        partialUpdatedSystem.enabled(UPDATED_ENABLED);

        restSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSystem))
            )
            .andExpect(status().isOk());

        // Validate the System in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSystemUpdatableFieldsEquals(partialUpdatedSystem, getPersistedSystem(partialUpdatedSystem));
    }

    @Test
    @Transactional
    void patchNonExistingSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, systemDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        system.setId(longCount.incrementAndGet());

        // Create the System
        SystemDTO systemDTO = systemMapper.toDto(system);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(systemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the System in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(system);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the system
        restSystemMockMvc
            .perform(delete(ENTITY_API_URL_ID, system.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return systemRepository.count();
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

    protected System getPersistedSystem(System system) {
        return systemRepository.findById(system.getId()).orElseThrow();
    }

    protected void assertPersistedSystemToMatchAllProperties(System expectedSystem) {
        assertSystemAllPropertiesEquals(expectedSystem, getPersistedSystem(expectedSystem));
    }

    protected void assertPersistedSystemToMatchUpdatableProperties(System expectedSystem) {
        assertSystemAllUpdatablePropertiesEquals(expectedSystem, getPersistedSystem(expectedSystem));
    }
}
