package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.domain.BoothUser;
import de.tsystems.onsite.bookabooth.domain.Location;
import de.tsystems.onsite.bookabooth.domain.User;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import de.tsystems.onsite.bookabooth.repository.LocationRepository;
import de.tsystems.onsite.bookabooth.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExcelService {

    @Autowired
    private BoothUserRepository boothUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public byte[] generateExcel(List<Booking> BookingList) throws IOException {
        Map<Long, String> locationNames = locationRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(Location::getId, Location::getLocation));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Buchungen");
        sheet.setDefaultColumnWidth(15);

        // Set specific column widths
        sheet.setColumnWidth(0, 40 * 256); // Firmenname
        sheet.setColumnWidth(1, 40 * 256); // Ansprechpartner
        sheet.setColumnWidth(2, 40 * 256); // Mail-Adresse
        sheet.setColumnWidth(3, 40 * 256); // Rechnungsadresse

        // Create a cell style for numbers with 2 decimal places
        CellStyle numberStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        numberStyle.setDataFormat(format.getFormat("#,##0.00"));

        // Create a cell style for address
        CellStyle addressStyle = workbook.createCellStyle();
        addressStyle.setWrapText(true);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        String[] headers = {
            "Firmenname",
            "Ansprechpartner",
            "Mail-Adresse",
            "Rechnungsadresse 1",
            "Rechnungsadresse 2",
            "Rechnungsadresse 3",
            "Rechnungsadresse 4",
            "Rechnungsadresse PLZ",
            "Rechnungsadresse Ort",
            "Bemerkung",
            "Standnummer",
            "Preis",
            "Stornierung",
            "Rechnungsnummer",
            "Innenauftrag",
            "Kundennummer",
        };
        for (int j = 0; j < headers.length; j++) {
            Cell cell = headerRow.createCell(j);
            cell.setCellValue(headers[j]);
            cell.setCellStyle(headerStyle);
        }

        int i = 1;
        for (Booking booking : BookingList) {
            Row bodyRow = sheet.createRow(i);
            createCell(bodyRow, 0, null).setCellValue(booking.getCompany().getName());
            BoothUser bUser = boothUserRepository.findFirstByCompanyId(booking.getCompany().getId());
            if (bUser != null) {
                User user = userRepository.findById(bUser.getId()).get();
                createCell(bodyRow, 1, null).setCellValue(
                    String.format("%s, %s (%s)", user.getLastName(), user.getFirstName(), bUser.getPhone())
                );
                createCell(bodyRow, 2, null).setCellValue(user.getEmail());
            }
            createCell(bodyRow, 3, addressStyle).setCellValue(booking.getCompany().getBillingAddressRow1());
            createCell(bodyRow, 4, addressStyle).setCellValue(booking.getCompany().getBillingAddressRow2());
            createCell(bodyRow, 5, addressStyle).setCellValue(booking.getCompany().getBillingAddressRow3());
            createCell(bodyRow, 6, addressStyle).setCellValue(booking.getCompany().getBillingAddressRow4());
            createCell(bodyRow, 7, addressStyle).setCellValue(booking.getCompany().getBillingZipCode());
            createCell(bodyRow, 8, addressStyle).setCellValue(booking.getCompany().getBillingCity());

            createCell(bodyRow, 9, null).setCellValue(booking.getCompany().getComment());
            String locBooth = String.format(
                "%s-%s",
                locationNames.get(booking.getBooth().getLocation().getId()),
                booking.getBooth().getTitle()
            );
            createCell(bodyRow, 10, null).setCellValue(locBooth);
            createCell(bodyRow, 11, numberStyle).setCellValue(booking.getPrice() == null ? 0.00 : booking.getPrice().doubleValue());
            createCell(bodyRow, 12, numberStyle).setCellValue(
                booking.getCancellationFee() == null ? 0.00 : booking.getCancellationFee().doubleValue()
            );
            i++;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private Cell createCell(Row r, int c, CellStyle style) {
        Cell cell = r.createCell(c);

        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }
}
