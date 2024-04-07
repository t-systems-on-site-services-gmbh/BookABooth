package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
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
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.BoothUser}.
 */
@RestController
@RequestMapping("/api/booth-users")
@Transactional
public class BoothUserResource {

    private final Logger log = LoggerFactory.getLogger(BoothUserResource.class);

    private static final String ENTITY_NAME = "boothUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BoothUserRepository boothUserRepository;

    public BoothUserResource(BoothUserRepository boothUserRepository) {
        this.boothUserRepository = boothUserRepository;
    }

    /**
     * {@code POST  /booth-users} : Create a new boothUser.
     *
     * @param boothUser the boothUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boothUser, or with status {@code 400 (Bad Request)} if the boothUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BoothUser> createBoothUser(@Valid @RequestBody BoothUser boothUser) throws URISyntaxException {
        log.debug("REST request to save BoothUser : {}", boothUser);
        if (boothUser.getId() != null) {
            throw new BadRequestAlertException("A new boothUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        boothUser = boothUserRepository.save(boothUser);
        return ResponseEntity.created(new URI("/api/booth-users/" + boothUser.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, boothUser.getId().toString()))
            .body(boothUser);
    }

    /**
     * {@code PUT  /booth-users/:id} : Updates an existing boothUser.
     *
     * @param id the id of the boothUser to save.
     * @param boothUser the boothUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothUser,
     * or with status {@code 400 (Bad Request)} if the boothUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boothUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoothUser> updateBoothUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoothUser boothUser
    ) throws URISyntaxException {
        log.debug("REST request to update BoothUser : {}, {}", id, boothUser);
        if (boothUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boothUser = boothUserRepository.save(boothUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothUser.getId().toString()))
            .body(boothUser);
    }

    /**
     * {@code PATCH  /booth-users/:id} : Partial updates given fields of an existing boothUser, field will ignore if it is null
     *
     * @param id the id of the boothUser to save.
     * @param boothUser the boothUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boothUser,
     * or with status {@code 400 (Bad Request)} if the boothUser is not valid,
     * or with status {@code 404 (Not Found)} if the boothUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the boothUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BoothUser> partialUpdateBoothUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoothUser boothUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update BoothUser partially : {}, {}", id, boothUser);
        if (boothUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boothUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boothUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BoothUser> result = boothUserRepository
            .findById(boothUser.getId())
            .map(existingBoothUser -> {
                if (boothUser.getPhone() != null) {
                    existingBoothUser.setPhone(boothUser.getPhone());
                }
                if (boothUser.getNote() != null) {
                    existingBoothUser.setNote(boothUser.getNote());
                }
                if (boothUser.getVerificationCode() != null) {
                    existingBoothUser.setVerificationCode(boothUser.getVerificationCode());
                }
                if (boothUser.getVerified() != null) {
                    existingBoothUser.setVerified(boothUser.getVerified());
                }
                if (boothUser.getLastLogin() != null) {
                    existingBoothUser.setLastLogin(boothUser.getLastLogin());
                }
                if (boothUser.getDisabled() != null) {
                    existingBoothUser.setDisabled(boothUser.getDisabled());
                }

                return existingBoothUser;
            })
            .map(boothUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, boothUser.getId().toString())
        );
    }

    /**
     * {@code GET  /booth-users} : get all the boothUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of boothUsers in body.
     */
    @GetMapping("")
    public List<BoothUser> getAllBoothUsers() {
        log.debug("REST request to get all BoothUsers");
        return boothUserRepository.findAll();
    }

    /**
     * {@code GET  /booth-users/:id} : get the "id" boothUser.
     *
     * @param id the id of the boothUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boothUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoothUser> getBoothUser(@PathVariable("id") Long id) {
        log.debug("REST request to get BoothUser : {}", id);
        Optional<BoothUser> boothUser = boothUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(boothUser);
    }

    /**
     * {@code DELETE  /booth-users/:id} : delete the "id" boothUser.
     *
     * @param id the id of the boothUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoothUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete BoothUser : {}", id);
        boothUserRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
