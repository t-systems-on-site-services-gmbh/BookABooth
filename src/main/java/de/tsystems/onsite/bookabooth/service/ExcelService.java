package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.domain.Booking;
import de.tsystems.onsite.bookabooth.repository.BoothUserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    @Autowired
    private BoothUserRepository boothUserRepository;

    public byte[] generateExcel(List<Booking> BookingList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Firmenname");
        headerRow.createCell(1).setCellValue("Ansprechpartner");
        headerRow.createCell(2).setCellValue("Rechnungsadresse");
        headerRow.createCell(3).setCellValue("Bemerkung");
        headerRow.createCell(4).setCellValue("Standnummer");
        headerRow.createCell(5).setCellValue("Preis");
        headerRow.createCell(6).setCellValue("Stornierungspreis");
        headerRow.createCell(7).setCellValue("Rechnungsnummer ");
        headerRow.createCell(8).setCellValue("Innenauftrag");
        headerRow.createCell(9).setCellValue("Kundennummer");
        int i = 1;
        for (Booking booking : BookingList) {
            Row BodyRow = sheet.createRow(i);
            BodyRow.createCell(0).setCellValue(booking.getCompany().getName().toString());
            BodyRow.createCell(1).setCellValue(
                boothUserRepository.findFirstByCompanyId(booking.getCompany().getId()) == null
                    ? ""
                    : boothUserRepository.findFirstByCompanyId(booking.getCompany().getId()).getPhone()
            );
            BodyRow.createCell(2).setCellValue(booking.getCompany().getBillingAddress());
            BodyRow.createCell(3).setCellValue(booking.getCompany().getComment());
            BodyRow.createCell(4).setCellValue(booking.getBooth().getId());
            BodyRow.createCell(5).setCellValue(booking.getPrice() == null ? 0.00 : booking.getPrice().doubleValue());
            BodyRow.createCell(6).setCellValue(booking.getCancellationFee() == null ? 0.00 : booking.getCancellationFee().doubleValue());
            i++;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
