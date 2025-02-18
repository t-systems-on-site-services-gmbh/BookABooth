package de.tsystems.onsite.bookabooth.service;

import static de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus.CANCELED;

import de.tsystems.onsite.bookabooth.config.ApplicationProperties;
import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.dto.BookingDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothDTO;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.exception.BadRequestException;
import de.tsystems.onsite.bookabooth.service.exception.ForbiddenException;
import de.tsystems.onsite.bookabooth.service.mapper.BookingMapper;
import de.tsystems.onsite.bookabooth.service.mapper.BoothMapper;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.Booking}.
 */
@Service
@Transactional
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final ApplicationProperties applicationProperties;

    private final BookingRepository bookingRepository;

    private final BoothUserRepository boothUserRepository;

    private final CompanyRepository companyRepository;

    private final BookingMapper bookingMapper;

    private final BoothMapper boothMapper;

    private final MailService mailService;

    private final BoothService boothService;

    private final SystemService systemService;

    private final UserService userService;

    public BookingService(
        ApplicationProperties applicationProperties,
        BookingRepository bookingRepository,
        BoothUserRepository boothUserRepository,
        CompanyRepository companyRepository,
        BookingMapper bookingMapper,
        BoothMapper boothMapper,
        MailService mailService,
        BoothService boothService,
        SystemService systemService,
        UserService userService
    ) {
        this.applicationProperties = applicationProperties;
        this.bookingRepository = bookingRepository;
        this.boothUserRepository = boothUserRepository;
        this.companyRepository = companyRepository;
        this.bookingMapper = bookingMapper;
        this.boothMapper = boothMapper;
        this.mailService = mailService;
        this.boothService = boothService;
        this.systemService = systemService;
        this.userService = userService;
    }

    /**
     * Save a booking.
     *
     * @param bookingDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDTO save(BookingDTO bookingDTO) {
        log.debug("Request to save Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking.setReceived(ZonedDateTime.now());
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Update a booking.
     *
     * @param bookingDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDTO update(BookingDTO bookingDTO) {
        log.debug("Request to update Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking.setReceived(ZonedDateTime.now());
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Partially update a booking.
     *
     * @param bookingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookingDTO> partialUpdate(BookingDTO bookingDTO) {
        log.debug("Request to partially update Booking : {}", bookingDTO);

        return bookingRepository
            .findById(bookingDTO.getId())
            .map(existingBooking -> {
                bookingMapper.partialUpdate(existingBooking, bookingDTO);
                existingBooking.setReceived(ZonedDateTime.now());
                return existingBooking;
            })
            .map(bookingRepository::save)
            .map(bookingMapper::toDto);
    }

    /**
     * Get all the bookings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BookingDTO> findAll() {
        log.debug("Request to get all Bookings");
        return bookingRepository.findAll().stream().map(bookingMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one booking by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookingDTO> findOne(Long id) {
        log.debug("Request to get Booking : {}", id);
        return bookingRepository.findById(id).map(bookingMapper::toDto);
    }

    /**
     * Delete the booking by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Booking : {}", id);
        bookingRepository.deleteById(id);
    }

    private void sendBookingCancelledEmail(Long bookingId) {
        List<User> users = findUsersByBookingId(bookingId);
        users.forEach(mailService::sendBookingDeletedEmail);
    }

    private void sendBookingConfirmedEmail(Long bookingId) {
        List<User> users = findUsersByBookingId(bookingId);
        users.forEach(mailService::sendBookingConfirmedEmail);
    }

    /**
     *
     * @param id booking id passed down from api endpoint.
     * @return a list of users that will receive an email.
     */
    public List<User> findUsersByBookingId(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        Booking booking = optionalBooking.get();
        Company company = booking.getCompany();
        List<BoothUser> boothUsers = boothUserRepository.findByCompanyId(company.getId());
        return boothUsers.stream().map(BoothUser::getUser).collect(Collectors.toList());
    }

    public List<BoothDTO> getBoothsUnavailableForBooking(BoothUserDTO currentBoothUser) {
        var bookings = bookingRepository
            .findByStatusNot(CANCELED)
            .stream()
            .filter(b -> (b.getStatus() == BookingStatus.CONFIRMED) || (b.getStatus() == BookingStatus.BLOCKED))
            .map(Booking::getBooth)
            .map(boothMapper::toDto);
        return bookings.toList();
    }

    /**
     * Checks if a booking is present for booth
     *
     * @param boothId the id of the booth.
     * @return booking if a booking is present for the booth. The check excludes bookings with status CANCELED.
     */
    public Optional<Booking> getNotCanceledBooking(Long boothId) {
        return bookingRepository.findByBoothIdAndStatusNot(boothId, CANCELED);
    }

    public Optional<BookingDTO> getBookingByCompanyId(Long id) {
        return bookingRepository.findByCompanyIdOrderByReceivedDesc(id).stream().findFirst().map(bookingMapper::toDto);
    }

    public BookingDTO blockABoothBooking(Long boothId, BoothUserDTO bUserDTO) {
        BoothDTO boothDTO = boothService.findOne(boothId).orElseThrow(() -> new BadRequestException("Booth not found"));
        return this.blockABoothBooking(boothDTO, bUserDTO);
    }

    public BookingDTO blockABoothBooking(BoothDTO boothDTO, BoothUserDTO bUserDTO) {
        // check if system is enabled
        if (!systemService.isSystemEnabled()) {
            throw new ForbiddenException("System is disabled");
        }

        // Profile completed
        String login = bUserDTO.getUser().getLogin();
        boolean profileCompleted = userService.getChecklistDTO(login).isMandatoryComplete();
        if (!profileCompleted) {
            throw new BadRequestException("Profile is not complete");
        }

        // check if the booth is already booked or blocked
        Optional<Booking> bookingforBoothId = this.getNotCanceledBooking(boothDTO.getId());
        if (bookingforBoothId.isPresent()) {
            throw new BadRequestException("Booth already booked or blocked");
        }

        // a BoothUser can only book one booth for his company
        Optional<Booking> bookingforCompany = this.getNotCanceledBooking(bUserDTO.getCompany().getId());
        if (bookingforCompany.isPresent()) {
            throw new BadRequestException("Company already booked or blocked a booth");
        }

        // create booking
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBooth(boothDTO);
        bookingDTO.setStatus(BookingStatus.BLOCKED);
        bookingDTO.setCompany(bUserDTO.getCompany());
        bookingDTO.setReceived(ZonedDateTime.now());
        bookingDTO.setPrice(calculatePrice(boothDTO));
        bookingDTO = save(bookingDTO);

        return bookingDTO;
    }

    public BookingDTO confirmABoothBooking(Long bookingId, BoothUserDTO bUserDTO) {
        BookingDTO bookingDTO = this.findOne(bookingId).orElseThrow(() -> new BadRequestException("Booking not found"));
        return this.confirmABoothBooking(bookingDTO, bUserDTO);
    }

    public BookingDTO confirmABoothBooking(BookingDTO bookingDTO, BoothUserDTO bUserDTO) {
        // get booking by bookinId and check the owner
        if (!bookingDTO.getCompany().getId().equals(bUserDTO.getCompany().getId())) {
            throw new ForbiddenException("Company does not own the booking");
        }

        var now = ZonedDateTime.now();
        // update the booking status
        bookingDTO.setStatus(BookingStatus.CONFIRMED);
        bookingDTO.setConfirmed(now);
        bookingDTO.setReceived(now);
        bookingDTO = this.update(bookingDTO);

        sendBookingConfirmedEmail(bookingDTO.getId());

        return bookingDTO;
    }

    public BookingDTO cancelAConfirmedBoothBooking(Long bookingId, BoothUserDTO bUserDTO, boolean force) {
        // get booking by bookinId and check the owner
        BookingDTO bookingDTO = this.findOne(bookingId).orElseThrow(() -> new BadRequestException("Booking not found"));
        return cancelAConfirmedBoothBooking(bookingDTO, bUserDTO, force);
    }

    public BookingDTO cancelAConfirmedBoothBooking(BookingDTO bookingDTO, BoothUserDTO bUserDTO, boolean force) {
        // update the booking status
        if (!force && !bookingDTO.getCompany().getId().equals(bUserDTO.getCompany().getId())) {
            throw new ForbiddenException("Company does not own the booking");
        }

        // update the booking status
        bookingDTO.setStatus(BookingStatus.CANCELED);
        bookingDTO.setReceived(ZonedDateTime.now());
        bookingDTO.setCancellationFee(calculateCancellationFee(bookingDTO.getPrice()));
        var updatedDto = this.update(bookingDTO);

        sendBookingCancelledEmail(updatedDto.getId());

        return updatedDto;
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long bookingId, BoothUserDTO currentBoothUser) {
        BookingDTO bookingDTO = this.findOne(bookingId).orElseThrow(() -> new BadRequestException("Booking not found"));
        if (currentBoothUser == null || currentBoothUser.getCompany() == null) {
            return false;
        } else {
            return bookingDTO.getCompany().getId().equals(currentBoothUser.getCompany().getId());
        }
    }

    private BigDecimal calculateCancellationFee(BigDecimal price) {
        var now = new Date(System.currentTimeMillis());
        if (now.after(applicationProperties.getCancellationReimbursementUntil())) {
            return price;
        }

        double multiplier = (100.0 - applicationProperties.getCancellationReimbursement()) / 100.0;
        return price.multiply(BigDecimal.valueOf(multiplier));
    }

    private BigDecimal calculatePrice(BoothDTO booth) {
        return boothService.getPriceForBooth(booth.getId());
    }

    @Scheduled(cron = "0 0/1 * * * *") // every minute
    public void removeBlockedBookings() {
        ZonedDateTime threshold = ZonedDateTime.now().minusSeconds(applicationProperties.getBookingRemovalInterval());

        List<Booking> bookings = bookingRepository
            .findByStatus(BookingStatus.BLOCKED)
            .stream()
            .filter(b -> b.getReceived().isBefore(threshold))
            .toList();

        bookings.forEach(bookingRepository::delete);
    }
}
