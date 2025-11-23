package com.travel.hub.web.rest;

import static com.travel.hub.domain.AgencyAsserts.*;
import static com.travel.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.hub.IntegrationTest;
import com.travel.hub.domain.Agency;
import com.travel.hub.domain.User;
import com.travel.hub.repository.AgencyRepository;
import com.travel.hub.repository.UserRepository;
import com.travel.hub.service.AgencyService;
import com.travel.hub.service.dto.AgencyDTO;
import com.travel.hub.service.mapper.AgencyMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AgencyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AgencyResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE = "BBBBBBBBBB";

    private static final Double DEFAULT_RATING_AVG = 1D;
    private static final Double UPDATED_RATING_AVG = 2D;

    private static final String ENTITY_API_URL = "/api/agencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AgencyRepository agencyRepositoryMock;

    @Autowired
    private AgencyMapper agencyMapper;

    @Mock
    private AgencyService agencyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgencyMockMvc;

    private Agency agency;

    private Agency insertedAgency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createEntity(EntityManager em) {
        Agency agency = new Agency()
            .description(DEFAULT_DESCRIPTION)
            .address(DEFAULT_ADDRESS)
            .website(DEFAULT_WEBSITE)
            .ratingAvg(DEFAULT_RATING_AVG);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        agency.setUser_agency(user);
        return agency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createUpdatedEntity(EntityManager em) {
        Agency updatedAgency = new Agency()
            .description(UPDATED_DESCRIPTION)
            .address(UPDATED_ADDRESS)
            .website(UPDATED_WEBSITE)
            .ratingAvg(UPDATED_RATING_AVG);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAgency.setUser_agency(user);
        return updatedAgency;
    }

    @BeforeEach
    void initTest() {
        agency = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAgency != null) {
            agencyRepository.delete(insertedAgency);
            insertedAgency = null;
        }
    }

    @Test
    @Transactional
    void createAgency() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);
        var returnedAgencyDTO = om.readValue(
            restAgencyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agencyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgencyDTO.class
        );

        // Validate the Agency in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgency = agencyMapper.toEntity(returnedAgencyDTO);
        assertAgencyUpdatableFieldsEquals(returnedAgency, getPersistedAgency(returnedAgency));

        assertAgencyMapsIdRelationshipPersistedValue(agency, returnedAgency);

        insertedAgency = returnedAgency;
    }

    @Test
    @Transactional
    void createAgencyWithExistingId() throws Exception {
        // Create the Agency with an existing ID
        agency.setId(1L);
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agencyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateAgencyMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Add a new parent entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();

        // Load the agency
        Agency updatedAgency = agencyRepository.findById(agency.getId()).orElseThrow();
        assertThat(updatedAgency).isNotNull();
        // Disconnect from session so that the updates on updatedAgency are not directly saved in db
        em.detach(updatedAgency);

        // Update the User with new association value
        //updatedAgency.setUser(user);
        AgencyDTO updatedAgencyDTO = agencyMapper.toDto(updatedAgency);
        assertThat(updatedAgencyDTO).isNotNull();

        // Update the entity
        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAgencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAgencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        /**
         * Validate the id for MapsId, the ids must be same
         * Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
         * Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
         * assertThat(testAgency.getId()).isEqualTo(testAgency.getUser().getId());
         */
    }

    @Test
    @Transactional
    void getAllAgencies() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        // Get all the agencyList
        restAgencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agency.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
            .andExpect(jsonPath("$.[*].ratingAvg").value(hasItem(DEFAULT_RATING_AVG)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgenciesWithEagerRelationshipsIsEnabled() throws Exception {
        when(agencyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAgencyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(agencyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgenciesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(agencyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAgencyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(agencyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAgency() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        // Get the agency
        restAgencyMockMvc
            .perform(get(ENTITY_API_URL_ID, agency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agency.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE))
            .andExpect(jsonPath("$.ratingAvg").value(DEFAULT_RATING_AVG));
    }

    @Test
    @Transactional
    void getNonExistingAgency() throws Exception {
        // Get the agency
        restAgencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgency() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agency
        Agency updatedAgency = agencyRepository.findById(agency.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgency are not directly saved in db
        em.detach(updatedAgency);
        updatedAgency.description(UPDATED_DESCRIPTION).address(UPDATED_ADDRESS).website(UPDATED_WEBSITE).ratingAvg(UPDATED_RATING_AVG);
        AgencyDTO agencyDTO = agencyMapper.toDto(updatedAgency);

        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agencyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgencyToMatchAllProperties(updatedAgency);
    }

    @Test
    @Transactional
    void putNonExistingAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agencyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agencyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgencyWithPatch() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agency using partial update
        Agency partialUpdatedAgency = new Agency();
        partialUpdatedAgency.setId(agency.getId());

        partialUpdatedAgency.website(UPDATED_WEBSITE);

        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgency.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgency))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgencyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAgency, agency), getPersistedAgency(agency));
    }

    @Test
    @Transactional
    void fullUpdateAgencyWithPatch() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agency using partial update
        Agency partialUpdatedAgency = new Agency();
        partialUpdatedAgency.setId(agency.getId());

        partialUpdatedAgency
            .description(UPDATED_DESCRIPTION)
            .address(UPDATED_ADDRESS)
            .website(UPDATED_WEBSITE)
            .ratingAvg(UPDATED_RATING_AVG);

        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgency.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgency))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgencyUpdatableFieldsEquals(partialUpdatedAgency, getPersistedAgency(partialUpdatedAgency));
    }

    @Test
    @Transactional
    void patchNonExistingAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agencyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgency() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agency.setId(longCount.incrementAndGet());

        // Create the Agency
        AgencyDTO agencyDTO = agencyMapper.toDto(agency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(agencyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agency in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgency() throws Exception {
        // Initialize the database
        insertedAgency = agencyRepository.saveAndFlush(agency);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agency
        restAgencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, agency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return agencyRepository.count();
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

    protected Agency getPersistedAgency(Agency agency) {
        return agencyRepository.findById(agency.getId()).orElseThrow();
    }

    protected void assertPersistedAgencyToMatchAllProperties(Agency expectedAgency) {
        assertAgencyAllPropertiesEquals(expectedAgency, getPersistedAgency(expectedAgency));
    }

    protected void assertPersistedAgencyToMatchUpdatableProperties(Agency expectedAgency) {
        assertAgencyAllUpdatablePropertiesEquals(expectedAgency, getPersistedAgency(expectedAgency));
    }
}
