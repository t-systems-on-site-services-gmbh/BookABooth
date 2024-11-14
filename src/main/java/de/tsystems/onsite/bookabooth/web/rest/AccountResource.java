package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.domain.PersistentToken;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.PersistentTokenRepository;
import de.tsystems.onsite.bookabooth.repository.UserRepository;
import de.tsystems.onsite.bookabooth.security.SecurityUtils;
import de.tsystems.onsite.bookabooth.service.MailService;
import de.tsystems.onsite.bookabooth.service.UserService;
import de.tsystems.onsite.bookabooth.service.dto.PasswordChangeDTO;
import de.tsystems.onsite.bookabooth.service.dto.UserProfileDTO;
import de.tsystems.onsite.bookabooth.web.rest.errors.*;
import de.tsystems.onsite.bookabooth.web.rest.vm.KeyAndPasswordVM;
import de.tsystems.onsite.bookabooth.web.rest.vm.ManagedUserVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.security.auth.login.AccountNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final PersistentTokenRepository persistentTokenRepository;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        PersistentTokenRepository persistentTokenRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     *
     * @param authentication authenticate the user
     * @return the data for the current user
     */
    @GetMapping("/account")
    public ResponseEntity<UserProfileDTO> getUserProfile(Authentication authentication) {
        String login = authentication.getName();
        if (userRepository.findOneByLogin(login).isEmpty()) {
            throw new AccountResourceException("User could not be found");
        }
        UserProfileDTO userProfileDTO = userService.getUserProfile(login);
        return ResponseEntity.ok(userProfileDTO);
    }

    /**
     *
     * @param userProfileDTO profile of the current user
     * @return the profile with its new values
     */
    @PostMapping("/account")
    public ResponseEntity<Void> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) throws AccountNotFoundException {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (user.isEmpty()) {
            throw new AccountResourceException("User could not be found");
        }
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userProfileDTO.getUser().getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        userService.updateUserProfile(userProfileDTO);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param userProfileDTO profile of the current user
     * @return profile without the check for waitingList
     */
    @PutMapping("/account/remove-waitinglist")
    public ResponseEntity<Void> removeFromWaitingList(@RequestBody UserProfileDTO userProfileDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        User user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new AccountResourceException("User could not be found"));
        if (!user.getEmail().equalsIgnoreCase(userProfileDTO.getUser().getEmail())) {
            throw new InvalidEmailException("Provided email does not match the existing email");
        }
        userService.removeFromWaitingList(userProfileDTO);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param userProfileDTO profile of the current user
     * @return profiled with canceled booking
     */
    @PutMapping("/account/cancel-booking")
    public ResponseEntity<Void> cancelBooking(@RequestBody UserProfileDTO userProfileDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        User user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new AccountResourceException("User could not be found"));
        if (!user.getEmail().equalsIgnoreCase(userProfileDTO.getUser().getEmail())) {
            throw new InvalidEmailException("Provided email does not match the existing email");
        }
        userService.cancelBooking(userProfileDTO);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param userProfileDTO profile of the current user
     * @return profile with confirmed booking
     */
    @PutMapping("/account/confirm-booking")
    public ResponseEntity<Void> confirmBooking(@RequestBody UserProfileDTO userProfileDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        User user = userRepository.findOneByLogin(userLogin).orElseThrow(() -> new AccountResourceException("User could not be found"));
        if (!user.getEmail().equalsIgnoreCase(userProfileDTO.getUser().getEmail())) {
            throw new InvalidEmailException("Provided email does not match the existing email");
        }
        userService.confirmBooking(userProfileDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code GET  /account/sessions} : get the current open sessions.
     *
     * @return the current open sessions.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the current open sessions couldn't be retrieved.
     */
    @GetMapping("/account/sessions")
    public List<PersistentToken> getCurrentSessions() {
        return persistentTokenRepository.findByUser(
            userRepository
                .findOneByLogin(
                    SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"))
                )
                .orElseThrow(() -> new AccountResourceException("User could not be found"))
        );
    }

    /**
     * {@code DELETE  /account/sessions?series={series}} : invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     *
     * @param series the series of an existing session.
     * @throws IllegalArgumentException if the series couldn't be URL decoded.
     */
    @DeleteMapping("/account/sessions/{series}")
    public void invalidateSession(@PathVariable("series") String series) {
        String decodedSeries = URLDecoder.decode(series, StandardCharsets.UTF_8);
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                u ->
                    persistentTokenRepository
                        .findByUser(u)
                        .stream()
                        .filter(persistentToken -> StringUtils.equals(persistentToken.getSeries(), decodedSeries))
                        .findAny()
                        .ifPresent(t -> persistentTokenRepository.deleteById(decodedSeries))
            );
    }

    // Checks, if the password of the user matches with the password input and deletes the account
    @DeleteMapping("/account/delete-account")
    public ResponseEntity<Void> deleteAccount(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        if (passwordChangeDTO.getCurrentPassword() == null || passwordChangeDTO.getCurrentPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.checkPassword(passwordChangeDTO.getCurrentPassword())) {
            // Hier später die Delete-Methoden aufrufen
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
