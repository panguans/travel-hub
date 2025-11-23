package com.travel.hub.service.impl;

import com.travel.hub.domain.Agency;
import com.travel.hub.repository.AgencyRepository;
import com.travel.hub.repository.UserRepository;
import com.travel.hub.service.AgencyService;
import com.travel.hub.service.dto.AgencyDTO;
import com.travel.hub.service.mapper.AgencyMapper;
import jakarta.persistence.EntityExistsException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.travel.hub.domain.Agency}.
 */
@Service
@Transactional
public class AgencyServiceImpl implements AgencyService {

    private static final Logger LOG = LoggerFactory.getLogger(AgencyServiceImpl.class);

    private final AgencyRepository agencyRepository;

    private final AgencyMapper agencyMapper;

    private final UserRepository userRepository;

    public AgencyServiceImpl(AgencyRepository agencyRepository, AgencyMapper agencyMapper, UserRepository userRepository) {
        this.agencyRepository = agencyRepository;
        this.agencyMapper = agencyMapper;
        this.userRepository = userRepository;
    }

    @Override
    public AgencyDTO save(AgencyDTO agencyDTO) {
        LOG.debug("Request to save Agency : {}", agencyDTO);
        Agency agency = agencyMapper.toEntity(agencyDTO);
        Long userId = agency.getUser_agency().getId();
        if (this.findOne(userId).isPresent()) {
            LOG.debug("Agency Already exist : {}", userId);
            throw new EntityExistsException("Agency Already exist : " + userId);
        }
        userRepository.findById(userId).ifPresent(agency::user_agency);
        agency = agencyRepository.save(agency);
        return agencyMapper.toDto(agency);
    }

    @Override
    public AgencyDTO update(AgencyDTO agencyDTO) {
        LOG.debug("Request to update Agency : {}", agencyDTO);
        Agency agency = agencyMapper.toEntity(agencyDTO);
        agency = agencyRepository.save(agency);
        return agencyMapper.toDto(agency);
    }

    @Override
    public Optional<AgencyDTO> partialUpdate(AgencyDTO agencyDTO) {
        LOG.debug("Request to partially update Agency : {}", agencyDTO);

        return agencyRepository
            .findById(agencyDTO.getId())
            .map(existingAgency -> {
                agencyMapper.partialUpdate(existingAgency, agencyDTO);

                return existingAgency;
            })
            .map(agencyRepository::save)
            .map(agencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgencyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Agencies");
        return agencyRepository.findAll(pageable).map(agencyMapper::toDto);
    }

    public Page<AgencyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return agencyRepository.findAllWithEagerRelationships(pageable).map(agencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgencyDTO> findOne(Long id) {
        LOG.debug("Request to get Agency : {}", id);
        return agencyRepository.findOneWithEagerRelationships(id).map(agencyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Agency : {}", id);
        agencyRepository.deleteById(id);
    }
}
