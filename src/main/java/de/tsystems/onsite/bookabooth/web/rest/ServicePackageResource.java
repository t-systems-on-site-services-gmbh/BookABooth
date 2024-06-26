package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.ServicePackage;
import de.tsystems.onsite.bookabooth.repository.ServicePackageRepository;
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
 * REST controller for managing {@link de.tsystems.onsite.bookabooth.domain.ServicePackage}.
 */
@RestController
@RequestMapping("/api/service-packages")
@Transactional
public class ServicePackageResource {

    private final Logger log = LoggerFactory.getLogger(ServicePackageResource.class);

    private static final String ENTITY_NAME = "servicePackage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServicePackageRepository servicePackageRepository;

    public ServicePackageResource(ServicePackageRepository servicePackageRepository) {
        this.servicePackageRepository = servicePackageRepository;
    }

    /**
     * {@code POST  /service-packages} : Create a new servicePackage.
     *
     * @param servicePackage the servicePackage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new servicePackage, or with status {@code 400 (Bad Request)} if the servicePackage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServicePackage> createServicePackage(@RequestBody ServicePackage servicePackage) throws URISyntaxException {
        log.debug("REST request to save ServicePackage : {}", servicePackage);
        if (servicePackage.getId() != null) {
            throw new BadRequestAlertException("A new servicePackage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        servicePackage = servicePackageRepository.save(servicePackage);
        return ResponseEntity.created(new URI("/api/service-packages/" + servicePackage.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, servicePackage.getId().toString()))
            .body(servicePackage);
    }

    /**
     * {@code PUT  /service-packages/:id} : Updates an existing servicePackage.
     *
     * @param id the id of the servicePackage to save.
     * @param servicePackage the servicePackage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicePackage,
     * or with status {@code 400 (Bad Request)} if the servicePackage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the servicePackage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicePackage> updateServicePackage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ServicePackage servicePackage
    ) throws URISyntaxException {
        log.debug("REST request to update ServicePackage : {}, {}", id, servicePackage);
        if (servicePackage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicePackage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicePackageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        servicePackage = servicePackageRepository.save(servicePackage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, servicePackage.getId().toString()))
            .body(servicePackage);
    }

    /**
     * {@code PATCH  /service-packages/:id} : Partial updates given fields of an existing servicePackage, field will ignore if it is null
     *
     * @param id the id of the servicePackage to save.
     * @param servicePackage the servicePackage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicePackage,
     * or with status {@code 400 (Bad Request)} if the servicePackage is not valid,
     * or with status {@code 404 (Not Found)} if the servicePackage is not found,
     * or with status {@code 500 (Internal Server Error)} if the servicePackage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServicePackage> partialUpdateServicePackage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ServicePackage servicePackage
    ) throws URISyntaxException {
        log.debug("REST request to partial update ServicePackage partially : {}, {}", id, servicePackage);
        if (servicePackage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, servicePackage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!servicePackageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServicePackage> result = servicePackageRepository
            .findById(servicePackage.getId())
            .map(existingServicePackage -> {
                if (servicePackage.getName() != null) {
                    existingServicePackage.setName(servicePackage.getName());
                }
                if (servicePackage.getPrice() != null) {
                    existingServicePackage.setPrice(servicePackage.getPrice());
                }
                if (servicePackage.getDescription() != null) {
                    existingServicePackage.setDescription(servicePackage.getDescription());
                }

                return existingServicePackage;
            })
            .map(servicePackageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, servicePackage.getId().toString())
        );
    }

    /**
     * {@code GET  /service-packages} : get all the servicePackages.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of servicePackages in body.
     */
    @GetMapping("")
    public List<ServicePackage> getAllServicePackages(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all ServicePackages");
        if (eagerload) {
            return servicePackageRepository.findAllWithEagerRelationships();
        } else {
            return servicePackageRepository.findAll();
        }
    }

    /**
     * {@code GET  /service-packages/:id} : get the "id" servicePackage.
     *
     * @param id the id of the servicePackage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the servicePackage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicePackage> getServicePackage(@PathVariable("id") Long id) {
        log.debug("REST request to get ServicePackage : {}", id);
        Optional<ServicePackage> servicePackage = servicePackageRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(servicePackage);
    }

    /**
     * {@code DELETE  /service-packages/:id} : delete the "id" servicePackage.
     *
     * @param id the id of the servicePackage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicePackage(@PathVariable("id") Long id) {
        log.debug("REST request to delete ServicePackage : {}", id);
        servicePackageRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
