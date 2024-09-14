package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import de.tsystems.onsite.bookabooth.repository.ServicePackageRepository;
import de.tsystems.onsite.bookabooth.service.dto.ServicePackageDTO;
import de.tsystems.onsite.bookabooth.service.mapper.ServicePackageMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.ServicePackage}.
 */
@Service
@Transactional
public class ServicePackageService {

    private final Logger log = LoggerFactory.getLogger(ServicePackageService.class);

    private final ServicePackageRepository servicePackageRepository;

    private final ServicePackageMapper servicePackageMapper;

    public ServicePackageService(ServicePackageRepository servicePackageRepository, ServicePackageMapper servicePackageMapper) {
        this.servicePackageRepository = servicePackageRepository;
        this.servicePackageMapper = servicePackageMapper;
    }

    /**
     * Save a servicePackage.
     *
     * @param servicePackageDTO the entity to save.
     * @return the persisted entity.
     */
    public ServicePackageDTO save(ServicePackageDTO servicePackageDTO) {
        log.debug("Request to save ServicePackage : {}", servicePackageDTO);
        ServicePackage servicePackage = servicePackageMapper.toEntity(servicePackageDTO);
        servicePackage = servicePackageRepository.save(servicePackage);
        return servicePackageMapper.toDto(servicePackage);
    }

    /**
     * Update a servicePackage.
     *
     * @param servicePackageDTO the entity to save.
     * @return the persisted entity.
     */
    public ServicePackageDTO update(ServicePackageDTO servicePackageDTO) {
        log.debug("Request to update ServicePackage : {}", servicePackageDTO);
        ServicePackage servicePackage = servicePackageMapper.toEntity(servicePackageDTO);
        servicePackage = servicePackageRepository.save(servicePackage);
        return servicePackageMapper.toDto(servicePackage);
    }

    /**
     * Partially update a servicePackage.
     *
     * @param servicePackageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ServicePackageDTO> partialUpdate(ServicePackageDTO servicePackageDTO) {
        log.debug("Request to partially update ServicePackage : {}", servicePackageDTO);

        return servicePackageRepository
            .findById(servicePackageDTO.getId())
            .map(existingServicePackage -> {
                servicePackageMapper.partialUpdate(existingServicePackage, servicePackageDTO);

                return existingServicePackage;
            })
            .map(servicePackageRepository::save)
            .map(servicePackageMapper::toDto);
    }

    /**
     * Get all the servicePackages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ServicePackageDTO> findAll() {
        log.debug("Request to get all ServicePackages");
        return servicePackageRepository
            .findAll()
            .stream()
            .map(servicePackageMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the servicePackages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ServicePackageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return servicePackageRepository.findAllWithEagerRelationships(pageable).map(servicePackageMapper::toDto);
    }

    /**
     * Get one servicePackage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ServicePackageDTO> findOne(Long id) {
        log.debug("Request to get ServicePackage : {}", id);
        return servicePackageRepository.findOneWithEagerRelationships(id).map(servicePackageMapper::toDto);
    }

    /**
     * Delete the servicePackage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ServicePackage : {}", id);
        servicePackageRepository.deleteById(id);
    }
}
