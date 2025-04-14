package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.PrivacyPolicy;
import de.tsystems.onsite.bookabooth.repository.PrivacyPolicyRepository;
import de.tsystems.onsite.bookabooth.service.dto.LocationDTO;
import de.tsystems.onsite.bookabooth.service.dto.PrivacyPolicyDTO;
import de.tsystems.onsite.bookabooth.service.mapper.PrivacyPolicyMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrivacyPolicyService {

    private final Logger log = LoggerFactory.getLogger(PrivacyPolicyService.class);

    private final PrivacyPolicyRepository privacyPolicyRepository;

    private final PrivacyPolicyMapper privacyPolicyMapper;

    public PrivacyPolicyService(PrivacyPolicyRepository privacyPolicyRepository, PrivacyPolicyMapper privacyPolicyMapper) {
        this.privacyPolicyRepository = privacyPolicyRepository;
        this.privacyPolicyMapper = privacyPolicyMapper;
    }

    /**
     * Save a new privacy policy.
     *
     * @param PrivacyPolicyDTO the entity to save.
     * @return the persisted entity.
     */
    public PrivacyPolicyDTO save(PrivacyPolicyDTO privacyPolicyDTO) {
        log.debug("Request to save PrivacyPolicy : {}", privacyPolicyDTO);
        PrivacyPolicy privacyPolicy = privacyPolicyMapper.toEntity(privacyPolicyDTO);
        privacyPolicy = privacyPolicyRepository.save(privacyPolicy);
        return privacyPolicyMapper.toDto(privacyPolicy);
    }

    /**
     * Get the latest PrivacyPolicy.
     *
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PrivacyPolicyDTO> findLatestById() {
        log.debug("Request to get the latest PrivacyPolicy");
        return privacyPolicyRepository.findLatestPrivacyPolicyById().map(privacyPolicyMapper::toDto);
    }

    /**
     * Get all the privacy policies.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PrivacyPolicyDTO> findAll() {
        log.debug("Request to get all privacy policies");
        return privacyPolicyRepository.findAll().stream().map(privacyPolicyMapper::toDto).toList();
    }

    /**
     * Get one PrivacyPolicy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PrivacyPolicyDTO> findOne(Long id) {
        log.debug("Request to get PrivacyPolicy : {}", id);
        return privacyPolicyRepository.findById(id).map(privacyPolicyMapper::toDto);
    }
}
