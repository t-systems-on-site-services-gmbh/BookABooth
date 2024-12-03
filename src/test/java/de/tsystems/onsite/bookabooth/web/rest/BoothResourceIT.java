package de.tsystems.onsite.bookabooth.web.rest;

import static de.tsystems.onsite.bookabooth.domain.BoothAsserts.*;
import static de.tsystems.onsite.bookabooth.web.rest.TestUtil.createUpdateProxyForBean;
import static de.tsystems.onsite.bookabooth.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BoothMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link BoothResource} REST controller.
 */
@SpringBootTest
@ActiveProfiles("mytest")
@AutoConfigureMockMvc
@WithMockUser
class BoothResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_CEILING_HEIGHT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CEILING_HEIGHT = new BigDecimal(2);

    private static final Boolean DEFAULT_AVAILABLE = false;
    private static final Boolean UPDATED_AVAILABLE = true;

    private static final String ENTITY_API_URL = "/api/booths";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private BoothMapper boothMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoothMockMvc;

    private Booth booth;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booth createEntity(EntityManager em) {
        Booth booth = new Booth().title(DEFAULT_TITLE).ceilingHeight(DEFAULT_CEILING_HEIGHT).available(DEFAULT_AVAILABLE);
        return booth;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Booth createUpdatedEntity(EntityManager em) {
        Booth booth = new Booth().title(UPDATED_TITLE).ceilingHeight(UPDATED_CEILING_HEIGHT).available(UPDATED_AVAILABLE);
        return booth;
    }

    @BeforeEach
    public void initTest() {
        booth = createEntity(em);
    }

    @Test
    @Transactional
    void createBooth() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);
        var returnedBoothDTO = om.readValue(
            restBoothMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BoothDTO.class
        );

        // Validate the Booth in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBooth = boothMapper.toEntity(returnedBoothDTO);
        assertBoothUpdatableFieldsEquals(returnedBooth, getPersistedBooth(returnedBooth));
    }

    @Test
    @Transactional
    void createBoothWithExistingId() throws Exception {
        // Create the Booth with an existing ID
        booth.setId(1L);
        BoothDTO boothDTO = boothMapper.toDto(booth);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoothMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        booth.setTitle(null);

        // Create the Booth, which fails.
        BoothDTO boothDTO = boothMapper.toDto(booth);

        restBoothMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        booth.setAvailable(null);

        // Create the Booth, which fails.
        BoothDTO boothDTO = boothMapper.toDto(booth);

        restBoothMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooths() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        // Get all the boothList
        restBoothMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(booth.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].ceilingHeight").value(hasItem(sameNumber(DEFAULT_CEILING_HEIGHT))))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.booleanValue())));
    }

    @Test
    @Transactional
    void getBooth() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        // Get the booth
        restBoothMockMvc
            .perform(get(ENTITY_API_URL_ID, booth.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(booth.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.ceilingHeight").value(sameNumber(DEFAULT_CEILING_HEIGHT)))
            .andExpect(jsonPath("$.available").value(DEFAULT_AVAILABLE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBooth() throws Exception {
        // Get the booth
        restBoothMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBooth() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the booth
        Booth updatedBooth = boothRepository.findById(booth.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBooth are not directly saved in db
        em.detach(updatedBooth);
        updatedBooth.title(UPDATED_TITLE).ceilingHeight(UPDATED_CEILING_HEIGHT).available(UPDATED_AVAILABLE);
        BoothDTO boothDTO = boothMapper.toDto(updatedBooth);

        restBoothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boothDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothDTO))
            )
            .andExpect(status().isOk());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBoothToMatchAllProperties(updatedBooth);
    }

    @Test
    @Transactional
    void putNonExistingBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boothDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boothDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoothWithPatch() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the booth using partial update
        Booth partialUpdatedBooth = new Booth();
        partialUpdatedBooth.setId(booth.getId());

        partialUpdatedBooth.title(UPDATED_TITLE).ceilingHeight(UPDATED_CEILING_HEIGHT).available(UPDATED_AVAILABLE);

        restBoothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooth.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBooth))
            )
            .andExpect(status().isOk());

        // Validate the Booth in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoothUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBooth, booth), getPersistedBooth(booth));
    }

    @Test
    @Transactional
    void fullUpdateBoothWithPatch() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the booth using partial update
        Booth partialUpdatedBooth = new Booth();
        partialUpdatedBooth.setId(booth.getId());

        partialUpdatedBooth.title(UPDATED_TITLE).ceilingHeight(UPDATED_CEILING_HEIGHT).available(UPDATED_AVAILABLE);

        restBoothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBooth.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBooth))
            )
            .andExpect(status().isOk());

        // Validate the Booth in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoothUpdatableFieldsEquals(partialUpdatedBooth, getPersistedBooth(partialUpdatedBooth));
    }

    @Test
    @Transactional
    void patchNonExistingBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boothDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boothDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBooth() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        booth.setId(longCount.incrementAndGet());

        // Create the Booth
        BoothDTO boothDTO = boothMapper.toDto(booth);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoothMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(boothDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Booth in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBooth() throws Exception {
        // Initialize the database
        boothRepository.saveAndFlush(booth);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the booth
        restBoothMockMvc
            .perform(delete(ENTITY_API_URL_ID, booth.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return boothRepository.count();
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

    protected Booth getPersistedBooth(Booth booth) {
        return boothRepository.findById(booth.getId()).orElseThrow();
    }

    protected void assertPersistedBoothToMatchAllProperties(Booth expectedBooth) {
        assertBoothAllPropertiesEquals(expectedBooth, getPersistedBooth(expectedBooth));
    }

    protected void assertPersistedBoothToMatchUpdatableProperties(Booth expectedBooth) {
        assertBoothAllUpdatablePropertiesEquals(expectedBooth, getPersistedBooth(expectedBooth));
    }
}
