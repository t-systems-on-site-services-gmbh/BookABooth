package de.tsystems.onsite.bookabooth.service;

import static de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus.CANCELED;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.CompanyRepository;
import de.tsystems.onsite.bookabooth.service.dto.BookingDTO;
import de.tsystems.onsite.bookabooth.service.mapper.BookingMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link de.tsystems.onsite.bookabooth.domain.Booking}.
 */
@Service
@Transactional
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;

    private final BoothUserRepository boothUserRepository;

    private final CompanyRepository companyRepository;

    private final BookingMapper bookingMapper;

    private final MailService mailService;

    public BookingService(
        BookingRepository bookingRepository,
        BoothUserRepository boothUserRepository,
        CompanyRepository companyRepository,
        BookingMapper bookingMapper,
        MailService mailService
    ) {
        this.bookingRepository = bookingRepository;
        this.boothUserRepository = boothUserRepository;
        this.companyRepository = companyRepository;
        this.bookingMapper = bookingMapper;
        this.mailService = mailService;
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

    /**
     *
     * @param booking Object passed down from api endpoint.
     *                This method sets the booking to canceled and removes the company from the exhibitor list.
     *                It also sends an email to all users associated with that company.
     */
    public void cancelBooking(Booking booking) {
        if (booking != null) {
            booking.setStatus(CANCELED);
            bookingRepository.save(booking);
            List<User> users = findUsersByBookingId(booking.getId());
            users.forEach(mailService::sendBookingDeletedEmail);

            // Removes user from exhibitor list
            Optional<Company> optionalCompany = companyRepository.findById(booking.getCompany().getId());
            optionalCompany.ifPresent(company -> {
                company.setExhibitorList(false);
                companyRepository.save(company);
            });
        }
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
}
