package de.tsystems.onsite.bookabooth.service.mapper;

import de.tsystems.onsite.bookabooth.domain.System;
import de.tsystems.onsite.bookabooth.service.dto.SystemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link System} and its DTO {@link SystemDTO}.
 */
@Mapper(componentModel = "spring")
public interface SystemMapper extends EntityMapper<SystemDTO, System> {}
