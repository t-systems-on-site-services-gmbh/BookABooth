package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.repository.SystemRepository;
import de.tsystems.onsite.bookabooth.service.SystemService;
import de.tsystems.onsite.bookabooth.service.dto.SystemDTO;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.System}.
 */
@RestController
@RequestMapping("/api/systems")
public class SystemResource {

    private final Logger log = LoggerFactory.getLogger(SystemResource.class);

    private static final String ENTITY_NAME = "system";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemService systemService;

    private final SystemRepository systemRepository;

    public SystemResource(SystemService systemService, SystemRepository systemRepository) {
        this.systemService = systemService;
        this.systemRepository = systemRepository;
    }

    /**
     * {@code PATCH  /systems/:id} : Partial updates given fields of an existing system, field will ignore if it is null
     *
     * @param id        the id of the systemDTO to save.
     * @param systemDTO the systemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemDTO,
     * or with status {@code 400 (Bad Request)} if the systemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the systemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the systemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SystemDTO> partialUpdateSystem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SystemDTO systemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update System partially : {}, {}", id, systemDTO);
        if (systemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SystemDTO> result = systemService.partialUpdate(systemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, systemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /systems} : get all the systems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systems in body.
     */
    @GetMapping("")
    public SystemDTO getSystem() {
        log.debug("REST request to get System");
        return systemService.findFirstSystemEntry();
    }

    /**
     * {@code GET  /systems/:id} : get the "id" system.
     *
     * @param id the id of the systemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemDTO> getSystem(@PathVariable("id") Long id) {
        log.debug("REST request to get System : {}", id);
        Optional<SystemDTO> systemDTO = systemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(systemDTO);
    }

    /**
     * {@code DELETE  /systems/:id} : delete the "id" system.
     *
     * @param id the id of the systemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystem(@PathVariable("id") Long id) {
        log.debug("REST request to delete System : {}", id);
        systemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
