package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.security.SecurityUtils;
import de.tsystems.onsite.bookabooth.service.BookingService;
import de.tsystems.onsite.bookabooth.service.BoothService;
import de.tsystems.onsite.bookabooth.service.BoothUserService;
import de.tsystems.onsite.bookabooth.service.dto.BookingDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.exception.ForbiddenException;
import de.tsystems.onsite.bookabooth.service.mapper.CompanyMapper;
import de.tsystems.onsite.bookabooth.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Booking}.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);

    private static final String ENTITY_NAME = "booking";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;

    private final BoothService boothService;

    private final BoothUserService boothUserService;

    private final CompanyMapper companyMapper;

    public BookingResource(
        BookingService bookingService,
        BookingRepository bookingRepository,
        BoothService boothService,
        BoothUserService boothUserService,
        CompanyMapper companyMapper
    ) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.boothService = boothService;
        this.boothUserService = boothUserService;
        this.companyMapper = companyMapper;
    }

    /**
     * {@code POST  /bookings} : Create a new booking.
     *
     * @param boothId of the booth for create a booking.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookingDTO, or with status {@code 400 (Bad Request)} if the booking was not created.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/booth/{id}")
    public ResponseEntity<BookingDTO> blockBooking(
        @PathVariable(value = "id", required = true) final Long boothId,
        Authentication authentication
    ) throws URISyntaxException {
        log.debug("REST request to save Booking : {}", boothId);

        BoothUserDTO bUserDTO = boothUserService.getCurrentBoothUser(authentication);
        BookingDTO blockedBookingDTO = bookingService.blockABoothBooking(boothId, bUserDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, blockedBookingDTO.getId().toString()))
            .body(blockedBookingDTO);
    }

    /**
     * {@code PATCH  /bookings} : Confirm a blocked booking.
     *
     * @param bookingId og the booking to confirm.
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the new bookingDTO, or with status {@code 400 (Bad Request)} if the booking was not confirmed.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/confirm/{id}")
    public ResponseEntity<BookingDTO> confirmBooking(
        @PathVariable(value = "id", required = true) final Long bookingId,
        Authentication authentication
    ) throws URISyntaxException {
        log.debug("REST request to confirm Booking : {}", bookingId);

        BoothUserDTO bUserDTO = boothUserService.getCurrentBoothUser(authentication);

        BookingDTO blockedBookingDTO = bookingService.confirmABoothBooking(bookingId, bUserDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blockedBookingDTO.getId().toString()))
            .body(blockedBookingDTO);
    }

    /**
     * {@code PATCH  /bookings} : Confirm a blocked booking.
     *
     * @param bookingId of booking to cancel.
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the new bookingDTO, or with status {@code 400 (Bad Request)} if the booking was not canceled.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<BookingDTO> cancelBooking(
        @PathVariable(value = "id", required = true) final Long bookingId,
        Authentication authentication
    ) throws URISyntaxException {
        log.debug("REST request to cancel a Booking : {}", bookingId);

        BoothUserDTO bUserDTO = boothUserService.getCurrentBoothUser(authentication);

        boolean force = SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN");

        BookingDTO bookingDTO = bookingService.cancelAConfirmedBoothBooking(bookingId, bUserDTO, force);

        return ResponseEntity.accepted()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookingId.toString()))
            .body(bookingDTO);
    }

    /**
     * {@code PUT  /bookings/:id} : Updates an existing booking.
     *
     * @param id the id of the bookingDTO to save.
     * @param bookingDTO the bookingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BookingDTO> updateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookingDTO bookingDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Booking : {}, {}", id, bookingDTO);
        if (bookingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookingDTO = bookingService.update(bookingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookingDTO.getId().toString()))
            .body(bookingDTO);
    }

    /**
     * {@code PATCH  /bookings/:id} : Partial updates given fields of an existing booking, field will ignore if it is null
     *
     * @param id the id of the bookingDTO to save.
     * @param bookingDTO the bookingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BookingDTO> partialUpdateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookingDTO bookingDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Booking partially : {}, {}", id, bookingDTO);
        if (bookingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookingDTO> result = bookingService.partialUpdate(bookingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bookings} : get all the bookings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("")
    public List<BookingDTO> getAllBookings() {
        log.debug("REST request to get all Bookings");
        return bookingService.findAll();
    }

    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the bookingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable("id") Long id) {
        log.debug("REST request to get Booking : {}", id);
        Optional<BookingDTO> bookingDTO = bookingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookingDTO);
    }

    @GetMapping("/unavailable")
    public List<BoothDTO> getUnavailableBooths(Authentication authentication) {
        log.debug("REST request to get all unavailable Booths");
        return bookingService.getBoothsUnavailableForBooking(boothUserService.getCurrentBoothUser(authentication));
    }

    /**
     * {@code DELETE  /bookings/:bookingId} : delete the "bookingId" booking.
     *
     * @param bookingId the bookingId of the bookingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long bookingId, Authentication authentication) {
        log.debug("REST request to delete Booking : {}", bookingId);

        if (!isBookingOwnerOrAdmin(bookingId, authentication)) {
            throw new ForbiddenException("You are not authorized to delete this booking");
        }

        Optional<BookingDTO> booking = bookingService.findOne(bookingId);

        if (SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN") || BookingStatus.BLOCKED.equals(booking.get().getStatus())) {
            bookingService.delete(bookingId);
        } else {
            throw new ForbiddenException("You are not authorized to delete this booking");
        }

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, bookingId.toString()))
            .build();
    }

    private boolean isBookingOwnerOrAdmin(Long bookingId, Authentication authentication) {
        return (
            bookingService.isOwner(bookingId, boothUserService.getCurrentBoothUser(authentication)) ||
            SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN")
        );
    }
}
