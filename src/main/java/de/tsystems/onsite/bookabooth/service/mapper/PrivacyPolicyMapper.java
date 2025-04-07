package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.PrivacyPolicy;
import de.tsystems.onsite.bookabooth.service.dto.PrivacyPolicyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PrivacyPolicy} and its DTO {@link PrivacyPolicyDTO}.
 */
@Mapper(componentModel = "spring")
public interface PrivacyPolicyMapper extends EntityMapper<PrivacyPolicyDTO, PrivacyPolicy> {}
