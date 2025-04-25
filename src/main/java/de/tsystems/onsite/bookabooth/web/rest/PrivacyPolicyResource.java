package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.service.BoothUserService;
import de.tsystems.onsite.bookabooth.service.PrivacyPolicyService;
import de.tsystems.onsite.bookabooth.service.dto.BoothUserDTO;
import de.tsystems.onsite.bookabooth.service.dto.PrivacyPolicyDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privacy-policy")
public class PrivacyPolicyResource {

    private final Logger log = LoggerFactory.getLogger(PrivacyPolicyResource.class);

    private final PrivacyPolicyService privacyPolicyService;

    private final BoothUserService boothUserService;

    public PrivacyPolicyResource(PrivacyPolicyService privacyPolicyService, BoothUserService boothUserService) {
        this.privacyPolicyService = privacyPolicyService;
        this.boothUserService = boothUserService;
    }

    /**
     * {@code GET  /privacy-policy} : get all privacy policies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of privacy policies in body.
     */
    @GetMapping("")
    public List<PrivacyPolicyDTO> getAllPrivacyPolicies() {
        log.debug("REST request to get all privacy policies");
        return privacyPolicyService.findAll();
    }

    /**
     * {@code GET  /privacy-policy/latest} : get the latest privacy policy.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the latest privacy policy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/latest")
    public ResponseEntity<PrivacyPolicyDTO> getLatestPrivacyPolicy() {
        log.debug("REST request to get the latest PrivacyPolicy");
        Optional<PrivacyPolicyDTO> privacyPolicyDTO = privacyPolicyService.findLatestById();
        return privacyPolicyDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /privacy-policy/check} : check which privacy policy has been accepted by the user.
     *
     * @param authentication the authentication object containing user details.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the privacy policy accepted by the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/check")
    public ResponseEntity<PrivacyPolicyDTO> checkPrivacyPolicy(Authentication authentication) {
        log.debug("REST request to check which privacy policy has been accepted by user");
        BoothUserDTO boothUser = boothUserService.getCurrentBoothUserDTO(authentication);
        Optional<PrivacyPolicyDTO> privacyPolicyDTO = Optional.ofNullable(boothUser.getAcceptedPrivacyPolicy());
        return privacyPolicyDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code POST  /privacy-policy} : Create a new privacy policy.
     *
     * @param privacyPolicyDTO the privacy policy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new privacy policy, or with status {@code 400 (Bad Request)} if the privacy policy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PrivacyPolicyDTO> createPrivacyPolicy(@RequestBody PrivacyPolicyDTO privacyPolicyDTO) throws URISyntaxException {
        log.debug("REST request to save PrivacyPolicy : {}", privacyPolicyDTO);
        if (privacyPolicyDTO.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        PrivacyPolicyDTO result = privacyPolicyService.save(privacyPolicyDTO);
        return ResponseEntity.created(new URI("/api/privacy-policy/" + result.getId())).body(result);
    }

    /**
     * {@code PUT  /privacy-policy/accept} : Accepts the latest privacy policy for the logged in user.
     */
    @PostMapping("/accept/")
    public ResponseEntity<Void> acceptPrivacyPolicy(Authentication authentication) {
        log.debug("REST request to accept the latest PrivacyPolicy");

        Optional<PrivacyPolicyDTO> privacyPolicyDTO = privacyPolicyService.findLatestById();
        if (privacyPolicyDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BoothUserDTO bUserDTO = boothUserService.getCurrentBoothUserDTO(authentication);
        bUserDTO.setAcceptedPrivacyPolicy(privacyPolicyDTO.get());
        boothUserService.save(bUserDTO);

        return ResponseEntity.ok().build();
    }
}
