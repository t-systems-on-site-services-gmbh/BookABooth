package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.System;
import de.tsystems.onsite.bookabooth.repository.SystemRepository;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.System}.
 */
@RestController
@RequestMapping("/api/systems")
@Transactional
public class SystemResource {

    private final Logger log = LoggerFactory.getLogger(SystemResource.class);

    private static final String ENTITY_NAME = "system";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemRepository systemRepository;

    public SystemResource(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    /**
     * {@code POST  /systems} : Create a new system.
     *
     * @param system the system to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new system, or with status {@code 400 (Bad Request)} if the system has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<System> createSystem(@RequestBody System system) throws URISyntaxException {
        log.debug("REST request to save System : {}", system);
        if (system.getId() != null) {
            throw new BadRequestAlertException("A new system cannot already have an ID", ENTITY_NAME, "idexists");
        }
        system = systemRepository.save(system);
        return ResponseEntity.created(new URI("/api/systems/" + system.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, system.getId().toString()))
            .body(system);
    }

    /**
     * {@code PUT  /systems/:id} : Updates an existing system.
     *
     * @param id the id of the system to save.
     * @param system the system to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated system,
     * or with status {@code 400 (Bad Request)} if the system is not valid,
     * or with status {@code 500 (Internal Server Error)} if the system couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<System> updateSystem(@PathVariable(value = "id", required = false) final Long id, @RequestBody System system)
        throws URISyntaxException {
        log.debug("REST request to update System : {}, {}", id, system);
        if (system.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, system.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        system = systemRepository.save(system);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, system.getId().toString()))
            .body(system);
    }

    /**
     * {@code PATCH  /systems/:id} : Partial updates given fields of an existing system, field will ignore if it is null
     *
     * @param id the id of the system to save.
     * @param system the system to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated system,
     * or with status {@code 400 (Bad Request)} if the system is not valid,
     * or with status {@code 404 (Not Found)} if the system is not found,
     * or with status {@code 500 (Internal Server Error)} if the system couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<System> partialUpdateSystem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody System system
    ) throws URISyntaxException {
        log.debug("REST request to partial update System partially : {}, {}", id, system);
        if (system.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, system.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<System> result = systemRepository
            .findById(system.getId())
            .map(existingSystem -> {
                if (system.getEnabled() != null) {
                    existingSystem.setEnabled(system.getEnabled());
                }

                return existingSystem;
            })
            .map(systemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, system.getId().toString())
        );
    }

    /**
     * {@code GET  /systems} : get all the systems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systems in body.
     */
    @GetMapping("")
    public List<System> getAllSystems() {
        log.debug("REST request to get all Systems");
        return systemRepository.findAll();
    }

    /**
     * {@code GET  /systems/:id} : get the "id" system.
     *
     * @param id the id of the system to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the system, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<System> getSystem(@PathVariable("id") Long id) {
        log.debug("REST request to get System : {}", id);
        Optional<System> system = systemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(system);
    }

    /**
     * {@code DELETE  /systems/:id} : delete the "id" system.
     *
     * @param id the id of the system to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystem(@PathVariable("id") Long id) {
        log.debug("REST request to delete System : {}", id);
        systemRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
