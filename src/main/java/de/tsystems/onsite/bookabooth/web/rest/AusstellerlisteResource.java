package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.service.AusstellerlisteService;
import de.tsystems.onsite.bookabooth.service.dto.AusstellerlisteDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ausstellerliste")
public class AusstellerlisteResource {

    private final Logger log = LoggerFactory.getLogger(AusstellerlisteResource.class);
    private final AusstellerlisteService ausstellerlisteService;

    public AusstellerlisteResource(AusstellerlisteService ausstellerlisteService) {
        this.ausstellerlisteService = ausstellerlisteService;
    }

    @GetMapping("")
    public List<AusstellerlisteDTO> getAllAusstellerlisteCompanies() {
        log.debug("REST request to get all companies on the waiting list");
        return ausstellerlisteService.getAussteller();
    }
}
