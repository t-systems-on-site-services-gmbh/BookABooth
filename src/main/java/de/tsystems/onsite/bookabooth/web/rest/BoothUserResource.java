package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.service.BoothUserService;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.BoothUser}.
 */
@RestController
@RequestMapping("/api/booth-users")
public class BoothUserResource {

    private final Logger log = LoggerFactory.getLogger(BoothUserResource.class);

    private static final String ENTITY_NAME = "boothUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoothUserService boothUserService;

    private final BoothUserRepository boothUserRepository;

    public BoothUserResource(BoothUserService boothUserService, BoothUserRepository boothUserRepository) {
        this.boothUserService = boothUserService;
        this.boothUserRepository = boothUserRepository;
    }

    /**
     * {@code POST  /booth-users} : Create a new boothUser.
     *
     * @param boothUserDTO the boothUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boothUserDTO, or with status {@code 400 (Bad Request)} if the boothUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BoothUserDTO> createBoothUser(@Valid @RequestBody BoothUserDTO boothUserDTO) throws URISyntaxException {
        log.debug("REST request to save BoothUser : {}", boothUserDTO);
        if (boothUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new boothUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(boothUserDTO.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        boothUserDTO = boothUserService.save(boothUserDTO);
        return ResponseEntity.created(new URI("/api/booth-users/" + boothUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, boothUserDTO.getId().toString()))
            .body(boothUserDTO);
    }

    /**
     * {@code PUT  /booth-users/:id} : Updates an existing boothUser.
     *
     * @param id the id of the boothUserDTO to save.
     * @param boothUserDTO the boothUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothUserDTO,
     * or with status {@code 400 (Bad Request)} if the boothUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boothUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoothUserDTO> updateBoothUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoothUserDTO boothUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BoothUser : {}, {}", id, boothUserDTO);
        if (boothUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boothUserDTO = boothUserService.update(boothUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothUserDTO.getId().toString()))
            .body(boothUserDTO);
    }

    /**
     * {@code PATCH  /booth-users/:id} : Partial updates given fields of an existing boothUser, field will ignore if it is null
     *
     * @param id the id of the boothUserDTO to save.
     * @param boothUserDTO the boothUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothUserDTO,
     * or with status {@code 400 (Bad Request)} if the boothUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the boothUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the boothUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BoothUserDTO> partialUpdateBoothUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoothUserDTO boothUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BoothUser partially : {}, {}", id, boothUserDTO);
        if (boothUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BoothUserDTO> result = boothUserService.partialUpdate(boothUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /booth-users} : get all the boothUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of boothUsers in body.
     */
    @GetMapping("")
    public List<BoothUserDTO> getAllBoothUsers() {
        log.debug("REST request to get all BoothUsers");
        return boothUserService.findAll();
    }

    /**
     * {@code GET  /booth-users/:id} : get the "id" boothUser.
     *
     * @param id the id of the boothUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boothUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoothUserDTO> getBoothUser(@PathVariable("id") Long id) {
        log.debug("REST request to get BoothUser : {}", id);
        Optional<BoothUserDTO> boothUserDTO = boothUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(boothUserDTO);
    }

    /**
     * {@code DELETE  /booth-users/:id} : delete the "id" boothUser.
     *
     * @param id the id of the boothUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoothUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete BoothUser : {}", id);
        boothUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
