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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    public System getSystem() {
        log.debug("REST request to get System");
        return systemRepository.findAll().get(0);
    }
}
