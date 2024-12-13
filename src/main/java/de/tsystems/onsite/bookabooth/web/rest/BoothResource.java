package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.BoothService;
import de.tsystems.onsite.bookabooth.service.ServicePackageService;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
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
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.Booth}.
 */
@RestController
@RequestMapping("/api/booths")
public class BoothResource {

    private final Logger log = LoggerFactory.getLogger(BoothResource.class);

    private static final String ENTITY_NAME = "booth";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoothService boothService;

    private final BoothRepository boothRepository;

    public BoothResource(BoothService boothService, BoothRepository boothRepository) {
        this.boothService = boothService;
        this.boothRepository = boothRepository;
    }

    /**
     * {@code POST  /booths} : Create a new booth.
     *
     * @param boothDTO the boothDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boothDTO, or with status {@code 400 (Bad Request)} if the booth has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BoothDTO> createBooth(@Valid @RequestBody BoothDTO boothDTO) throws URISyntaxException {
        log.debug("REST request to save Booth : {}", boothDTO);
        if (boothDTO.getId() != null) {
            throw new BadRequestAlertException("A new booth cannot already have an ID", ENTITY_NAME, "idexists");
        }
        boothDTO = boothService.save(boothDTO);
        return ResponseEntity.created(new URI("/api/booths/" + boothDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, boothDTO.getId().toString()))
            .body(boothDTO);
    }

    /**
     * {@code PUT  /booths/:id} : Updates an existing booth.
     *
     * @param id the id of the boothDTO to save.
     * @param boothDTO the boothDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothDTO,
     * or with status {@code 400 (Bad Request)} if the boothDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boothDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoothDTO> updateBooth(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoothDTO boothDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Booth : {}, {}", id, boothDTO);
        if (boothDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boothService.updateServicePackages(boothDTO);
        boothDTO = boothService.update(boothDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothDTO.getId().toString()))
            .body(boothDTO);
    }

    /**
     * {@code PATCH  /booths/:id} : Partial updates given fields of an existing booth, field will ignore if it is null
     *
     * @param id the id of the boothDTO to save.
     * @param boothDTO the boothDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothDTO,
     * or with status {@code 400 (Bad Request)} if the boothDTO is not valid,
     * or with status {@code 404 (Not Found)} if the boothDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the boothDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BoothDTO> partialUpdateBooth(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoothDTO boothDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Booth partially : {}, {}", id, boothDTO);
        if (boothDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BoothDTO> result = boothService.partialUpdate(boothDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /booths} : get all the booths.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of booths in body.
     */
    @GetMapping("")
    public List<BoothDTO> getAllBooths() {
        log.debug("REST request to get all Booths");
        return boothService.findAll();
    }

    /**
     * {@code GET  /booths/:id} : get the "id" booth.
     *
     * @param id the id of the boothDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boothDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoothDTO> getBooth(@PathVariable("id") Long id) {
        log.debug("REST request to get Booth : {}", id);
        Optional<BoothDTO> boothDTO = boothService.findOne(id);
        return ResponseUtil.wrapOrNotFound(boothDTO);
    }

    /**
     * {@code DELETE  /booths/:id} : delete the "id" booth.
     *
     * @param id the id of the boothDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooth(@PathVariable("id") Long id) {
        log.debug("REST request to delete Booth : {}", id);
        boothService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
