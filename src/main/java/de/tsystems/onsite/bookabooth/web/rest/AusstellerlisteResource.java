package de.tsystems.onsite.bookabooth.web.rest;

import de.tsystems.onsite.bookabooth.service.AusstellerlisteService;
import de.tsystems.onsite.bookabooth.service.dto.AusstellerlisteDTO;
import de.tsystems.onsite.bookabooth.service.dto.ExhibitorDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        log.debug("REST request to get all companies on the exhibitor list");
        return ausstellerlisteService.getAussteller();
    }

    /**
     * Download a zip file containing all logos of exhibitors with a confirmed booking.
     * @return
     */
    @GetMapping(value = "/zip", produces = "application/zip")
    public ResponseEntity<byte[]> logoZip() throws IOException {
        List<ExhibitorDTO> exhibitors = ausstellerlisteService.getExhibitorsWithConfirmedBooking();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ausstellerlisteService.createExhibitorsZip(exhibitors, baos);
        byte[] zip = baos.toByteArray();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exhibitor-logos.zip")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(zip);
    }
}
