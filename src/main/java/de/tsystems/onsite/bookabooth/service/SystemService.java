package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.System;
import de.tsystems.onsite.bookabooth.repository.SystemRepository;
import de.tsystems.onsite.bookabooth.service.dto.SystemDTO;
import de.tsystems.onsite.bookabooth.service.mapper.SystemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.System}.
 */
@Service
@Transactional
public class SystemService {

    private final Logger log = LoggerFactory.getLogger(SystemService.class);

    private final SystemRepository systemRepository;

    private final SystemMapper systemMapper;

    public SystemService(SystemRepository systemRepository, SystemMapper systemMapper) {
        this.systemRepository = systemRepository;
        this.systemMapper = systemMapper;
    }

    /**
     * Save a system.
     *
     * @param systemDTO the entity to save.
     * @return the persisted entity.
     */
    public SystemDTO save(SystemDTO systemDTO) {
        log.debug("Request to save System : {}", systemDTO);
        System system = systemMapper.toEntity(systemDTO);
        system = systemRepository.save(system);
        return systemMapper.toDto(system);
    }

    /**
     * Update a system.
     *
     * @param systemDTO the entity to save.
     * @return the persisted entity.
     */
    public SystemDTO update(SystemDTO systemDTO) {
        log.debug("Request to update System : {}", systemDTO);
        System system = systemMapper.toEntity(systemDTO);
        system = systemRepository.save(system);
        return systemMapper.toDto(system);
    }

    /**
     * Partially update a system.
     *
     * @param systemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SystemDTO> partialUpdate(SystemDTO systemDTO) {
        log.debug("Request to partially update System : {}", systemDTO);

        return systemRepository
            .findById(systemDTO.getId())
            .map(existingSystem -> {
                systemMapper.partialUpdate(existingSystem, systemDTO);

                return existingSystem;
            })
            .map(systemRepository::save)
            .map(systemMapper::toDto);
    }

    /**
     * Get all the systems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public SystemDTO findFirstSystemEntry() {
        log.debug("Request to get all Systems");
        // return mapped system to systemMapper.toDto
        System system = systemRepository.findFirstByOrderById();
        if (system != null) {
            return systemMapper.toDto(system);
        }
        return null;
    }

    /**
     * Get all the systems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SystemDTO> findAll() {
        log.debug("Request to get all Systems");
        return systemRepository.findAll().stream().map(systemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one system by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SystemDTO> findOne(Long id) {
        log.debug("Request to get System : {}", id);
        return systemRepository.findById(id).map(systemMapper::toDto);
    }

    /**
     * Delete the system by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete System : {}", id);
        systemRepository.deleteById(id);
    }
}
