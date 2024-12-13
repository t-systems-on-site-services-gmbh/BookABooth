package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BoothMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.Booth}.
 */
@Service
@Transactional
public class BoothService {

    private final Logger log = LoggerFactory.getLogger(BoothService.class);

    private final BoothRepository boothRepository;

    private final BoothMapper boothMapper;

    private final ServicePackageService servicePackageService;

    public BoothService(BoothRepository boothRepository, BoothMapper boothMapper, ServicePackageService servicePackageService) {
        this.boothRepository = boothRepository;
        this.boothMapper = boothMapper;
        this.servicePackageService = servicePackageService;
    }

    /**
     * Save a booth.
     *
     * @param boothDTO the entity to save.
     * @return the persisted entity.
     */
    public BoothDTO save(BoothDTO boothDTO) {
        log.debug("Request to save Booth : {}", boothDTO);
        Booth booth = boothMapper.toEntity(boothDTO);
        booth = boothRepository.save(booth);
        return boothMapper.toDto(booth);
    }

    /**
     * Update a booth.
     *
     * @param boothDTO the entity to save.
     * @return the persisted entity.
     */
    public BoothDTO update(BoothDTO boothDTO) {
        log.debug("Request to update Booth : {}", boothDTO);
        Booth booth = boothMapper.toEntity(boothDTO);
        booth = boothRepository.save(booth);
        return boothMapper.toDto(booth);
    }

    /**
     * Partially update a booth.
     *
     * @param boothDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BoothDTO> partialUpdate(BoothDTO boothDTO) {
        log.debug("Request to partially update Booth : {}", boothDTO);

        return boothRepository
            .findById(boothDTO.getId())
            .map(existingBooth -> {
                boothMapper.partialUpdate(existingBooth, boothDTO);

                return existingBooth;
            })
            .map(boothRepository::save)
            .map(boothMapper::toDto);
    }

    /**
     * Get all the booths.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BoothDTO> findAll() {
        log.debug("Request to get all Booths");
        return boothRepository.findAll().stream().map(boothMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one booth by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BoothDTO> findOne(Long id) {
        log.debug("Request to get Booth : {}", id);
        return boothRepository.findById(id).map(boothMapper::toDto);
    }

    /**
     * Delete the booth by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Booth : {}", id);
        boothRepository.deleteById(id);
    }

    /**
     * Update the servicePackages of a booth.
     * Removes booth from all servicePackages and adds it to the ones in the DTO.
     * @param boothDTO the entity to update.
     */
    public void updateServicePackages(BoothDTO boothDTO) {
        log.debug("Request to update ServicePackages : {}", boothDTO);
        Optional<Booth> optionalBooth = boothRepository.findById(boothDTO.getId());
        if (optionalBooth.isPresent()) {
            Booth booth = optionalBooth.get();
            var servicePackages = booth.getServicePackages();
            servicePackages.forEach(servicePackage -> {
                servicePackageService.removeBooth(servicePackage, booth);
            });
            boothDTO
                .getServicePackages()
                .forEach(servicePackageDTO -> {
                    servicePackageService.addBooth(servicePackageDTO, booth);
                });
        }
    }
}
