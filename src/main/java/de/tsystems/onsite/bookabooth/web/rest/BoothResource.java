package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.Booth}.
 */
@RestController
@RequestMapping("/api/booths")
@Transactional
public class BoothResource {

    private final Logger log = LoggerFactory.getLogger(BoothResource.class);

    private static final String ENTITY_NAME = "booth";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoothRepository boothRepository;

    public BoothResource(BoothRepository boothRepository) {
        this.boothRepository = boothRepository;
    }

    /**
     * {@code POST  /booths} : Create a new booth.
     *
     * @param booth the booth to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new booth, or with status {@code 400 (Bad Request)} if the booth has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Booth> createBooth(@Valid @RequestBody Booth booth) throws URISyntaxException {
        log.debug("REST request to save Booth : {}", booth);
        if (booth.getId() != null) {
            throw new BadRequestAlertException("A new booth cannot already have an ID", ENTITY_NAME, "idexists");
        }
        booth = boothRepository.save(booth);
        return ResponseEntity.created(new URI("/api/booths/" + booth.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, booth.getId().toString()))
            .body(booth);
    }

    /**
     * {@code PUT  /booths/:id} : Updates an existing booth.
     *
     * @param id the id of the booth to save.
     * @param booth the booth to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated booth,
     * or with status {@code 400 (Bad Request)} if the booth is not valid,
     * or with status {@code 500 (Internal Server Error)} if the booth couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Booth> updateBooth(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Booth booth)
        throws URISyntaxException {
        log.debug("REST request to update Booth : {}, {}", id, booth);
        if (booth.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, booth.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        booth = boothRepository.save(booth);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, booth.getId().toString()))
            .body(booth);
    }

    /**
     * {@code PATCH  /booths/:id} : Partial updates given fields of an existing booth, field will ignore if it is null
     *
     * @param id the id of the booth to save.
     * @param booth the booth to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated booth,
     * or with status {@code 400 (Bad Request)} if the booth is not valid,
     * or with status {@code 404 (Not Found)} if the booth is not found,
     * or with status {@code 500 (Internal Server Error)} if the booth couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Booth> partialUpdateBooth(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Booth booth
    ) throws URISyntaxException {
        log.debug("REST request to partial update Booth partially : {}, {}", id, booth);
        if (booth.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, booth.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Booth> result = boothRepository
            .findById(booth.getId())
            .map(existingBooth -> {
                if (booth.getTitle() != null) {
                    existingBooth.setTitle(booth.getTitle());
                }
                if (booth.getCeilingHeight() != null) {
                    existingBooth.setCeilingHeight(booth.getCeilingHeight());
                }
                if (booth.getAvailable() != null) {
                    existingBooth.setAvailable(booth.getAvailable());
                }

                return existingBooth;
            })
            .map(boothRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, booth.getId().toString())
        );
    }

    /**
     * {@code GET  /booths} : get all the booths.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of booths in body.
     */
    @GetMapping("")
    public List<Booth> getAllBooths() {
        log.debug("REST request to get all Booths");
        return boothRepository.findAll();
    }

    /**
     * {@code GET  /booths/:id} : get the "id" booth.
     *
     * @param id the id of the booth to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the booth, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booth> getBooth(@PathVariable("id") Long id) {
        log.debug("REST request to get Booth : {}", id);
        Optional<Booth> booth = boothRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(booth);
    }

    /**
     * {@code DELETE  /booths/:id} : delete the "id" booth.
     *
     * @param id the id of the booth to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooth(@PathVariable("id") Long id) {
        log.debug("REST request to delete Booth : {}", id);
        boothRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
