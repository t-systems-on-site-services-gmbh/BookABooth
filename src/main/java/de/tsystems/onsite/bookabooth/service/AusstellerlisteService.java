package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.config.ApplicationProperties;
import de.tsystems.onsite.bookabooth.domain.Booth;
import de.tsystems.onsite.bookabooth.domain.enumeration.BookingStatus;
import de.tsystems.onsite.bookabooth.repository.BookingRepository;
import de.tsystems.onsite.bookabooth.repository.BoothRepository;
import de.tsystems.onsite.bookabooth.service.dto.AusstellerlisteDTO;
import de.tsystems.onsite.bookabooth.service.dto.ExhibitorDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AusstellerlisteService {

    private final ApplicationProperties applicationProperties;

    private final Logger log = LoggerFactory.getLogger(AusstellerlisteService.class);
    private final BoothRepository boothRepository;
    private final BookingService bookingService;
    private final CompanyService companyService;
    private BookingRepository bookingRepository;

    public AusstellerlisteService(
        ApplicationProperties applicationProperties,
        BoothRepository boothRepository,
        BookingService bookingService,
        CompanyService companyService,
        BookingRepository bookingRepository
    ) {
        this.applicationProperties = applicationProperties;
        this.boothRepository = boothRepository;
        this.bookingService = bookingService;
        this.companyService = companyService;
        this.bookingRepository = bookingRepository;
    }

    public List<AusstellerlisteDTO> getAussteller() {
        // Collect confirmed bookings and map companyID to boothID
        Map<Long, Long> confirmedCompanyAndBoothId = bookingService
            .findAll()
            .stream()
            .filter(booking -> BookingStatus.CONFIRMED.equals(booking.getStatus()))
            .collect(Collectors.toMap(booking -> booking.getCompany().getId(), booking -> booking.getBooth().getId()));

        // Collect booths and map boothID to boothTitle
        Map<Long, String> boothIdAndBoothTitle = boothRepository
            .findAll()
            .stream()
            .collect(
                Collectors.toMap(
                    Booth::getId,
                    booth ->
                        String.format(
                            "%s-%s",
                            Optional.ofNullable(booth.getLocation()).map(location -> location.getLocation()).orElse(""),
                            booth.getTitle()
                        )
                )
            );

        // Filter and map companies to exhibitorListDTO
        return companyService
            .findAll()
            .stream()
            .filter(company -> Boolean.TRUE.equals(company.getExhibitorList()))
            .filter(company -> confirmedCompanyAndBoothId.containsKey(company.getId()))
            .sorted((c1, c2) -> Comparator.nullsFirst(String::compareTo).compare(c1.getName().toUpperCase(), c2.getName().toUpperCase()))
            .map(company -> {
                Long boothId = confirmedCompanyAndBoothId.get(company.getId());
                String boothTitle = boothIdAndBoothTitle.getOrDefault(boothId, "");
                return new AusstellerlisteDTO(company.getId(), company.getName(), company.getLogo(), company.getDescription(), boothTitle);
            })
            .collect(Collectors.toList());
    }

    public List<ExhibitorDTO> getExhibitorsWithConfirmedBooking() {
        return bookingRepository.findExhibitorsWithConfirmedBooking();
    }

    public void createExhibitorsZip(List<ExhibitorDTO> exhibitors, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (ExhibitorDTO exhibitor : exhibitors) {
                String baseDir = exhibitor.getLocationName() + "/";
                if (exhibitor.isExhibitorList()) {
                    baseDir += "not-in-exhibitor-list/";
                }

                // remove first path segment 'e.g. uploads, because its already' in uploadFolder path
                String extension = "";
                if (exhibitor.getCompanyLogo() != null) {
                    Path original = Paths.get(exhibitor.getCompanyLogo());

                    if (original.getNameCount() > 1) {
                        extension = original.toString().split("\\.")[original.toString().split("\\.").length - 1];
                    }
                }

                // create file name
                String fileName = (exhibitor.getCompanyName() + "-" + exhibitor.getBoothTitle() + "." + extension).replaceAll("\\s+", "_");

                // full path in zip file
                String zipEntryName = baseDir + fileName;

                File imageFile = new File(applicationProperties.getWorkDir() + File.separator + exhibitor.getCompanyLogo());
                if (imageFile.exists() && imageFile.isFile()) {
                    try (FileInputStream fis = new FileInputStream(imageFile)) {
                        zos.putNextEntry(new ZipEntry(zipEntryName));

                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                } else {
                    // create file not found file in case image is missing
                    System.err.println("Image file not found: " + exhibitor.getCompanyName());
                    zos.putNextEntry(new ZipEntry(zipEntryName + "-file_not_found"));
                }
            }
        }
    }
}
