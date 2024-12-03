package de.tsystems.onsite.bookabooth.service;

import static de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus.CANCELED;
import static de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus.CONFIRMED;

import de.tsystems.onsite.bookabooth.config.Constants;
import de.tsystems.onsite.bookabooth.domain.*;
import de.tsystems.onsite.bookabooth.domain.Authority;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Company;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.*;
import de.tsystems.onsite.bookabooth.security.AuthoritiesConstants;
import de.tsystems.onsite.bookabooth.security.SecurityUtils;
import de.tsystems.onsite.bookabooth.service.dto.*;
import de.tsystems.onsite.bookabooth.service.dto.AdminUserDTO;
import de.tsystems.onsite.bookabooth.service.dto.CompanyDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserRegistrationDTO;
import de.tsystems.onsite.bookabooth.service.exception.*;
import de.tsystems.onsite.bookabooth.service.mapper.BookingMapper;
import de.tsystems.onsite.bookabooth.service.mapper.CompanyMapper;
import de.tsystems.onsite.bookabooth.service.mapper.UserMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.security.auth.login.AccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    private final BoothUserRepository boothUserRepository;

    private final UserMapper userMapper;

    private final CompanyMapper companyMapper;

    private final BookingMapper bookingMapper;

    private final BookingRepository bookingRepository;

    private final PasswordEncoder passwordEncoder;

    private final PersistentTokenRepository persistentTokenRepository;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final Validator validator;

    public UserService(
        CompanyService companyService,
        UserRepository userRepository,
        CompanyRepository companyRepository,
        BoothUserRepository boothUserRepository,
        UserMapper userMapper,
        CompanyMapper companyMapper,
        BookingMapper bookingMapper,
        BookingRepository bookingRepository,
        PasswordEncoder passwordEncoder,
        PersistentTokenRepository persistentTokenRepository,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        Validator validator
    ) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.boothUserRepository = boothUserRepository;
        this.userMapper = userMapper;
        this.companyMapper = companyMapper;
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
        this.persistentTokenRepository = persistentTokenRepository;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.validator = validator;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public BoothUser registerUser(UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getTermsAccepted()) {
            throw new TermsNotAcceptedException();
        }

        userRepository
            .findOneByLogin(userRegistrationDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userRegistrationDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        // find user by company name
        boothUserRepository
            .findByCompanyName(userRegistrationDTO.getCompanyName())
            .stream()
            .findFirst()
            .ifPresent(existingUser -> {
                User user = existingUser.getUser();
                boolean removed = removeNonActivatedUser(user);
                if (!removed) {
                    throw new CompanyAlreadyUsedException();
                }
            });

        // new User
        User newUser = new User();
        // add Authority USER
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        newUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        newUser.setLogin(userRegistrationDTO.getLogin().toLowerCase());
        newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        newUser.setEmail(userRegistrationDTO.getEmail().toLowerCase());

        // new Company
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setName(userRegistrationDTO.getCompanyName());
        companyDTO = companyService.save(companyDTO);
        Company company = companyRepository.getReferenceById(companyDTO.getId());

        // new BoothUser
        BoothUser newBoothUser = new BoothUser();
        newBoothUser.setUser(newUser);
        newBoothUser.setCompany(company);
        boothUserRepository.save(newBoothUser);
        //this.clearUserCaches(newUser);

        log.debug("Created Information for User: {}", newUser);
        log.debug("user was rigistered: {}", newUser);
        return newBoothUser;
    }

    @Deprecated
    public User old_registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        companyRepository
            .findOneByNameIgnoreCase(userDTO.getCompanyName())
            .ifPresent(c -> {
                throw new CompanyAlreadyUsedException();
            });

        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                this.clearUserCaches(user);
                log.debug("Deleted User: {}", user);
            });
    }

    // Deletes an account and its dependencies, cannot delete admin if there is only one in the system
    @Transactional
    public void deleteAccount(Long userId) {
        log.debug("Request to delete Account with userId: {}", userId);

        // Checks, if the user is an admin and how many admins are in the system
        if (isAdmin()) {
            long adminCount = userRepository.countByAuthoritiesName("ROLE_ADMIN");
            if (adminCount <= 1) {
                throw new BadRequestException("Cannot delete the last admin user");
            }
            log.debug("User is an admin, only deleting user: {}", userId);
            userRepository.findById(userId).ifPresent(userRepository::delete);
        }
        // find and delete the user with its dependencies
        userRepository
            .findById(userId)
            .ifPresent(user -> {
                Optional<BoothUser> boothUser = boothUserRepository.findById(userId);

                if (boothUser.isPresent()) {
                    Long companyId = boothUser.get().getCompany().getId();

                    log.debug("Removing boothUser with ID: {}", userId);
                    boothUserRepository.deleteById(userId);

                    // Checks, if the user is the last user of its company
                    long boothUserCount = boothUserRepository.countByCompanyId(companyId);
                    if (boothUserCount == 0) {
                        bookingRepository
                            .findByCompanyId(companyId)
                            .ifPresent(booking -> {
                                log.debug("Removing booking with ID: {}", booking.getId());
                                bookingRepository.delete(booking);
                            });
                        log.debug("Removing company with ID: {}", companyId);
                        companyRepository.deleteById(companyId);
                    } else {
                        log.debug("Company with ID: {} has other users, not deleting company and booking", companyId);
                    }

                    log.debug("Removing user with ID: {}", userId);
                    userRepository.deleteById(userId);
                } else {
                    log.debug("No boothUser found with User ID: {}", userId);
                }
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     *
     * @param userProfileDTO Profile containing user- and company-information, is checked in controller-method and passed through to service
     * @throws AccountNotFoundException Exception is thrown if the repository cannot find the id passed down from the profile
     */
    public void updateUserProfile(UserProfileDTO userProfileDTO) throws AccountNotFoundException {
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userProfileDTO.getUser());
        if (!violations.isEmpty()) {
            // Is checked to prevent the user from entering invalid user-details
            throw new BadRequestException();
        }
        Optional<User> optionalUser = userRepository.findById(userProfileDTO.getUser().getId());
        if (optionalUser.isEmpty()) {
            throw new AccountNotFoundException("User could not be found");
        }

        User user = optionalUser.get();
        this.clearUserCaches(user);
        userMapper.profileDTOtoUserEntity(userProfileDTO, user);
        userRepository.save(user);

        if (!isAdmin() && userProfileDTO.getCompany() != null) {
            Optional<Company> optionalCompany = companyRepository.findById(userProfileDTO.getCompany().getId());
            if (optionalCompany.isPresent()) {
                Company company = optionalCompany.get();
                companyMapper.toEntity(userProfileDTO.getCompany(), company);
                companyRepository.save(company);
            }
        }
        this.clearUserCaches(user);
        log.debug("Changed Information for User: {}", user);
    }

    public static boolean isAdmin() {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equalsIgnoreCase("ROLE_ADMIN"));
    }

    // Removes the user from the waiting list
    public void removeFromWaitingList(UserProfileDTO userProfileDTO) {
        Optional.of(companyRepository.findById(userProfileDTO.getCompany().getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(company -> {
                company.setWaitingList(false);
                companyRepository.save(company);
                return userProfileDTO;
            });
    }

    // Set the booking status to canceled (from prebooked or confirmed)
    public void cancelBooking(UserProfileDTO userProfileDTO, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findByCompanyId(userProfileDTO.getCompany().getId());
        optionalBooking.ifPresent(booking -> {
            if (booking.getId().equals(bookingId)) {
                booking.setStatus(CANCELED);
                bookingRepository.save(booking);
            }
        });
    }

    // Set the booking status from prebooked to confirmed
    public void confirmBooking(UserProfileDTO userProfileDTO, Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findByCompanyId(userProfileDTO.getCompany().getId());
        optionalBooking.ifPresent(booking -> {
            if (booking.getId().equals(bookingId)) {
                booking.setStatus(CONFIRMED);
                bookingRepository.save(booking);
            }
        });
    }

    // Checks the password of the current user
    public Boolean checkPassword(String currentClearTextPassword) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    log.debug("Password is correct");
                    return true;
                } else {
                    log.debug("Password is incorrect");
                    return false;
                }
            })
            .orElse(false);
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Method to get User and Company from the database to display on the profile
     * Boothuser is used to join the tables
     * Booking is needed to display the booking status on the profile
     *
     * @param login to identify the user
     * @return DTO with necessary infos to build the Userprofile
     */
    @Transactional
    public UserProfileDTO getUserProfile(String login) {
        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDTO userDTO = userMapper.userToUserDTO(user);

        if (isAdmin()) {
            return new UserProfileDTO(
                userDTO,
                null,
                null,
                user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet())
            );
        } else {
            BoothUser boothUser = boothUserRepository
                .findById(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("BoothUser not found"));

            Company company = companyRepository
                .findById(boothUser.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

            CompanyDTO companyDTO = companyMapper.toDto(company);

            Optional<Booking> booking = bookingRepository.findByCompanyId(company.getId());
            BookingDTO bookingDTO = null;
            if (booking.isPresent()) {
                bookingDTO = bookingMapper.toDto(booking.get());
            }
            return new UserProfileDTO(
                userDTO,
                companyDTO,
                bookingDTO,
                user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet())
            );
        }
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository
            .findByTokenDateBefore(now.minusMonths(1))
            .forEach(token -> {
                log.debug("Deleting token {}", token.getSeries());
                User user = token.getUser();
                user.getPersistentTokens().remove(token);
                persistentTokenRepository.delete(token);
            });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }
}
