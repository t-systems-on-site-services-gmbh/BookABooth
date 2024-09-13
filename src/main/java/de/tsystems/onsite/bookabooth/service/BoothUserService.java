package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.UserRepository;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BoothUserMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.BoothUser}.
 */
@Service
@Transactional
public class BoothUserService {

    private final Logger log = LoggerFactory.getLogger(BoothUserService.class);

    private final BoothUserRepository boothUserRepository;

    private final BoothUserMapper boothUserMapper;

    private final UserRepository userRepository;

    public BoothUserService(BoothUserRepository boothUserRepository, BoothUserMapper boothUserMapper, UserRepository userRepository) {
        this.boothUserRepository = boothUserRepository;
        this.boothUserMapper = boothUserMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a boothUser.
     *
     * @param boothUserDTO the entity to save.
     * @return the persisted entity.
     */
    public BoothUserDTO save(BoothUserDTO boothUserDTO) {
        log.debug("Request to save BoothUser : {}", boothUserDTO);
        BoothUser boothUser = boothUserMapper.toEntity(boothUserDTO);
        Long userId = boothUser.getUser().getId();
        userRepository.findById(userId).ifPresent(boothUser::user);
        boothUser = boothUserRepository.save(boothUser);
        return boothUserMapper.toDto(boothUser);
    }

    /**
     * Update a boothUser.
     *
     * @param boothUserDTO the entity to save.
     * @return the persisted entity.
     */
    public BoothUserDTO update(BoothUserDTO boothUserDTO) {
        log.debug("Request to update BoothUser : {}", boothUserDTO);
        BoothUser boothUser = boothUserMapper.toEntity(boothUserDTO);
        boothUser = boothUserRepository.save(boothUser);
        return boothUserMapper.toDto(boothUser);
    }

    /**
     * Partially update a boothUser.
     *
     * @param boothUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BoothUserDTO> partialUpdate(BoothUserDTO boothUserDTO) {
        log.debug("Request to partially update BoothUser : {}", boothUserDTO);

        return boothUserRepository
            .findById(boothUserDTO.getId())
            .map(existingBoothUser -> {
                boothUserMapper.partialUpdate(existingBoothUser, boothUserDTO);

                return existingBoothUser;
            })
            .map(boothUserRepository::save)
            .map(boothUserMapper::toDto);
    }

    /**
     * Get all the boothUsers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BoothUserDTO> findAll() {
        log.debug("Request to get all BoothUsers");
        return boothUserRepository.findAll().stream().map(boothUserMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one boothUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BoothUserDTO> findOne(Long id) {
        log.debug("Request to get BoothUser : {}", id);
        return boothUserRepository.findById(id).map(boothUserMapper::toDto);
    }

    /**
     * Delete the boothUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BoothUser : {}", id);
        boothUserRepository.deleteById(id);
    }
}
