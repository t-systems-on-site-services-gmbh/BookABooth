package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.ServicePackageDTO;
import de.tsystems.onsite.bookabooth.service.exception.BadRequestException;
import de.tsystems.onsite.bookabooth.service.mapper.BoothMapper;
import de.tsystems.onsite.bookabooth.service.mapper.ServicePackageMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
    private final ServicePackageMapper servicePackageMapper;

    private final ServicePackageService servicePackageService;

    public BoothService(
        BoothRepository boothRepository,
        BoothMapper boothMapper,
        ServicePackageMapper servicePackageMapper,
        ServicePackageService servicePackageService
    ) {
        this.boothRepository = boothRepository;
        this.boothMapper = boothMapper;
        this.servicePackageMapper = servicePackageMapper;
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

        // update servicePackages via owning side
        updateServicePackages(boothDTO);

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
        //return boothRepository.findAll().stream().map(boothMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
        return boothRepository
            .findAllBoothsWithCompanyName()
            .stream()
            .map(o -> {
                BoothDTO dto = boothMapper.toDto((Booth) o[0]);
                dto.setCompanyName((String) o[1]);
                return dto;
            })
            .toList();
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
     * Removes booth from all servicePackages and add the new ones.
     * @param newBoothDTO the entity to update.
     */
    public void updateServicePackages(BoothDTO newBoothDTO) {
        // throw exception if the booth or booth id is null
        if (newBoothDTO == null || newBoothDTO.getId() == null) {
            throw new BadRequestException("The booth or booth id is null");
        }
        Booth repoBooth = boothRepository
            .findById(newBoothDTO.getId())
            .orElseThrow(() -> new BadRequestException("The booth does not exist in the repository"));
        BoothDTO oldBoothDTO = boothMapper.toDto(repoBooth);

        // all servicePackageIds from newBoothDTO excluding the ones that are already in the oldBoothDTO
        List<Long> spToAdd = newBoothDTO
            .getServicePackages()
            .stream()
            .map(ServicePackageDTO::getId)
            .filter(id -> !oldBoothDTO.getServicePackages().stream().map(ServicePackageDTO::getId).toList().contains(id))
            .toList();
        // all servicePackageIds from oldBoothDTO excluding the ones that are already in the newBoothDTO
        List<Long> spToRemove = oldBoothDTO
            .getServicePackages()
            .stream()
            .map(ServicePackageDTO::getId)
            .filter(id -> !newBoothDTO.getServicePackages().stream().map(ServicePackageDTO::getId).toList().contains(id))
            .toList();

        log.debug(
            "Request to update ServicePackages relations for Booth : {} to remove ServicePackage Ids: {} and add {}",
            newBoothDTO.getId(),
            spToRemove,
            spToAdd
        );
        servicePackageService.removeBooth(spToRemove, repoBooth);
        servicePackageService.addBooth(spToAdd, boothMapper.toEntity(newBoothDTO));
    }

    @Transactional(readOnly = true)
    public List<Booth> getBookableBooths() {
        List<BookingStatus> excludedStatus = List.of(BookingStatus.BLOCKED, BookingStatus.CONFIRMED);
        return boothRepository.findAvailableBoothsWithoutBookingStatus(excludedStatus);
    }

    public BigDecimal getPriceForBooth(Long id) {
        var packages = servicePackageService.findAll();
        Optional<BigDecimal> price = packages
            .stream()
            .filter(p -> p.getBooths().stream().map(BoothDTO::getId).toList().contains(id))
            .map(p -> {
                if (p.getPrice() == null) {
                    return BigDecimal.ZERO;
                } else {
                    return p.getPrice();
                }
            })
            .reduce(BigDecimal::add);
        return price.orElse(BigDecimal.ZERO);
    }
}
