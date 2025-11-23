package com.travel.hub.service;

import com.travel.hub.service.dto.AgencyDTO;
import jakarta.persistence.EntityExistsException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.travel.hub.domain.Agency}.
 */
public interface AgencyService {
    /**
     * Save a agency.
     *
     * @param agencyDTO the entity to save.
     * @return the persisted entity.
     */
    AgencyDTO save(AgencyDTO agencyDTO);

    /**
     * Updates a agency.
     *
     * @param agencyDTO the entity to update.
     * @return the persisted entity.
     */
    AgencyDTO update(AgencyDTO agencyDTO);

    /**
     * Partially updates a agency.
     *
     * @param agencyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AgencyDTO> partialUpdate(AgencyDTO agencyDTO);

    /**
     * Get all the agencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgencyDTO> findAll(Pageable pageable);

    /**
     * Get all the agencies with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgencyDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" agency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgencyDTO> findOne(Long id);

    /**
     * Delete the "id" agency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
