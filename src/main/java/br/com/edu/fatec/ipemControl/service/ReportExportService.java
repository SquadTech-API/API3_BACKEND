package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.FrequentDestinationDTO;
import br.com.edu.fatec.ipemControl.dto.TechnicianReportDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ReportExportService {

    // ── PDF ───────────────────────────────────────────────────────
    public ResponseEntity<byte[]> exportPdfResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"technician_report_" + registration + ".pdf\"")
                    .body(generatePdf(dto));
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private byte[] generatePdf(TechnicianReportDTO dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(baos)));

        document.add(new Paragraph("IPEM Control — Technician Report")
                .setFontSize(18).setBold().setFontColor(ColorConstants.DARK_GRAY));
        document.add(new Paragraph(" "));

        // Identificação
        document.add(new Paragraph("1. Identification").setFontSize(14).setBold());
        Table tId = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tId, "Name",           dto.getName());
        addRow(tId, "Registration",   str(dto.getRegistration()));
        addRow(tId, "CPF",            dto.getCpf());
        addRow(tId, "Email",          dto.getEmail());
        addRow(tId, "Role",           dto.getRole());
        addRow(tId, "License Type",   dto.getLicenseType());
        addRow(tId, "License Number", dto.getLicenseNumber());
        addRow(tId, "Birth Date",     dto.getBirthDate());
        document.add(tId);
        document.add(new Paragraph(" "));

        // Status
        document.add(new Paragraph("2. Operational Status").setFontSize(14).setBold());
        Table tSt = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tSt, "Active",          Boolean.TRUE.equals(dto.getActive()) ? "Yes" : "No");
        addRow(tSt, "Open Departure",  Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Yes" : "No");
        addRow(tSt, "Registered At",   dto.getRegistrationDate());
        addRow(tSt, "Last Update",     dto.getLastUpdate());
        document.add(tSt);
        document.add(new Paragraph(" "));

        // Comportamento
        document.add(new Paragraph("3. Operational Behavior").setFontSize(14).setBold());
        Table tComp = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tComp, "Avg Duration (h)",       fmt(dto.getAvgDurationHours()));
        addRow(tComp, "Longest Departure (km)", fmt(dto.getLongestKm()));
        addRow(tComp, "Longest Duration (h)",   fmt(dto.getLongestDurationHours()));
        addRow(tComp, "Departures/week",        fmt(dto.getDepartureFrequencyPerWeek()));
        document.add(tComp);
        document.add(new Paragraph(" "));

        // Manutenção
        document.add(new Paragraph("4. Maintenance").setFontSize(14).setBold());
        Table tMan = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tMan, "Oil Changes",     str(dto.getOilChanges()));
        addRow(tMan, "Last Oil Change", dto.getLastOilChange());
        document.add(tMan);
        document.add(new Paragraph(" "));

        // Documentos
        if (dto.getDocuments() != null) {
            document.add(new Paragraph("5. Documents").setFontSize(14).setBold());
            Table tDoc = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addRow(tDoc, "Received",   str(dto.getDocuments().getReceived()));
            addRow(tDoc, "Read",       str(dto.getDocuments().getRead()));
            addRow(tDoc, "Downloaded", str(dto.getDocuments().getDownloaded()));
            document.add(tDoc);
            document.add(new Paragraph(" "));
        }

        // Destinos
        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            document.add(new Paragraph("Top Destinations").setFontSize(14).setBold());
            Table tDest = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            tDest.addHeaderCell(new Cell().add(new Paragraph("Location").setBold()));
            tDest.addHeaderCell(new Cell().add(new Paragraph("Visits").setBold()));
            for (FrequentDestinationDTO d : dto.getDestinations()) {
                tDest.addCell(d.getLocation());
                tDest.addCell(String.valueOf(d.getQuantity()));
            }
            document.add(tDest);
        }

        document.close();
        return baos.toByteArray();
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "—")));
    }

    // ── CSV ───────────────────────────────────────────────────────
    public ResponseEntity<byte[]> exportCsvResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"technician_report_" + registration + ".csv\"")
                    .body(generateCsv(dto));
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV: " + e.getMessage(), e);
        }
    }

    private byte[] generateCsv(TechnicianReportDTO dto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter csv = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        csv.writeNext(new String[]{"Section", "Field", "Value"});
        csv.writeNext(new String[]{"Identification", "Name",           dto.getName()});
        csv.writeNext(new String[]{"Identification", "Registration",   str(dto.getRegistration())});
        csv.writeNext(new String[]{"Identification", "CPF",            dto.getCpf()});
        csv.writeNext(new String[]{"Identification", "Email",          dto.getEmail()});
        csv.writeNext(new String[]{"Identification", "Role",           dto.getRole()});
        csv.writeNext(new String[]{"Identification", "License Type",   dto.getLicenseType()});
        csv.writeNext(new String[]{"Identification", "License Number", dto.getLicenseNumber()});
        csv.writeNext(new String[]{"Identification", "Birth Date",     dto.getBirthDate()});

        csv.writeNext(new String[]{"Status", "Active",
                Boolean.TRUE.equals(dto.getActive()) ? "Yes" : "No"});
        csv.writeNext(new String[]{"Status", "Open Departure",
                Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Yes" : "No"});

        csv.writeNext(new String[]{"Behavior", "Avg Duration (h)",       fmt(dto.getAvgDurationHours())});
        csv.writeNext(new String[]{"Behavior", "Longest Departure (km)", fmt(dto.getLongestKm())});
        csv.writeNext(new String[]{"Behavior", "Departures/week",        fmt(dto.getDepartureFrequencyPerWeek())});

        if (dto.getDeparturesByPeriod() != null)
            for (Map.Entry<String, Long> e : dto.getDeparturesByPeriod().entrySet())
                csv.writeNext(new String[]{"Departures/period", e.getKey(), str(e.getValue())});

        if (dto.getSpendingByPeriod() != null)
            for (Map.Entry<String, BigDecimal> e : dto.getSpendingByPeriod().entrySet())
                csv.writeNext(new String[]{"Spending/period", e.getKey(), fmt(e.getValue())});

        if (dto.getDocuments() != null) {
            csv.writeNext(new String[]{"Documents", "Received",   str(dto.getDocuments().getReceived())});
            csv.writeNext(new String[]{"Documents", "Read",       str(dto.getDocuments().getRead())});
            csv.writeNext(new String[]{"Documents", "Downloaded", str(dto.getDocuments().getDownloaded())});
        }

        if (dto.getDestinations() != null)
            for (FrequentDestinationDTO d : dto.getDestinations())
                csv.writeNext(new String[]{"Destinations", d.getLocation(), str(d.getQuantity())});

        csv.close();
        return baos.toByteArray();
    }

    // ── Excel ─────────────────────────────────────────────────────
    public ResponseEntity<byte[]> exportExcelResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"technician_report_" + registration + ".xlsx\"")
                    .body(generateExcel(dto));
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel: " + e.getMessage(), e);
        }
    }

    private byte[] generateExcel(TechnicianReportDTO dto) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Aba 1 — Identification
        Sheet shId = workbook.createSheet("Identification");
        addXlsRow(shId, headerStyle, 0, "Field", "Value");
        addXlsRow(shId, null, 1,  "Name",           dto.getName());
        addXlsRow(shId, null, 2,  "Registration",   str(dto.getRegistration()));
        addXlsRow(shId, null, 3,  "CPF",            dto.getCpf());
        addXlsRow(shId, null, 4,  "Email",          dto.getEmail());
        addXlsRow(shId, null, 5,  "Role",           dto.getRole());
        addXlsRow(shId, null, 6,  "License Type",   dto.getLicenseType());
        addXlsRow(shId, null, 7,  "License Number", dto.getLicenseNumber());
        addXlsRow(shId, null, 8,  "Birth Date",     dto.getBirthDate());
        addXlsRow(shId, null, 9,  "Active",         Boolean.TRUE.equals(dto.getActive()) ? "Yes" : "No");
        addXlsRow(shId, null, 10, "Open Departure", Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Yes" : "No");
        shId.autoSizeColumn(0); shId.autoSizeColumn(1);

        // Aba 2 — Behavior
        Sheet shBeh = workbook.createSheet("Behavior");
        addXlsRow(shBeh, headerStyle, 0, "Indicator", "Value");
        addXlsRow(shBeh, null, 1, "Avg Duration (h)",       fmt(dto.getAvgDurationHours()));
        addXlsRow(shBeh, null, 2, "Longest Departure (km)", fmt(dto.getLongestKm()));
        addXlsRow(shBeh, null, 3, "Longest Duration (h)",   fmt(dto.getLongestDurationHours()));
        addXlsRow(shBeh, null, 4, "Departures/week",        fmt(dto.getDepartureFrequencyPerWeek()));
        shBeh.autoSizeColumn(0); shBeh.autoSizeColumn(1);

        // Aba 3 — Departures by period
        if (dto.getDeparturesByPeriod() != null) {
            Sheet shDep = workbook.createSheet("Departures by Period");
            addXlsRow(shDep, headerStyle, 0, "Period", "Departures", "KM");
            int r = 1;
            for (String p : dto.getDeparturesByPeriod().keySet()) {
                Row row = shDep.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(str(dto.getDeparturesByPeriod().get(p)));
                BigDecimal km = dto.getKmByPeriod() != null ? dto.getKmByPeriod().get(p) : null;
                row.createCell(2).setCellValue(fmt(km));
            }
            shDep.autoSizeColumn(0); shDep.autoSizeColumn(1); shDep.autoSizeColumn(2);
        }

        // Aba 4 — Spending by period
        if (dto.getSpendingByPeriod() != null) {
            Sheet shFin = workbook.createSheet("Spending by Period");
            addXlsRow(shFin, headerStyle, 0, "Period", "Spending (R$)", "Fuelings");
            int r = 1;
            for (String p : dto.getSpendingByPeriod().keySet()) {
                Row row = shFin.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(fmt(dto.getSpendingByPeriod().get(p)));
                Long f = dto.getFuelingsByPeriod() != null ? dto.getFuelingsByPeriod().get(p) : null;
                row.createCell(2).setCellValue(str(f));
            }
            shFin.autoSizeColumn(0); shFin.autoSizeColumn(1); shFin.autoSizeColumn(2);
        }

        // Aba 5 — Destinations
        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            Sheet shDest = workbook.createSheet("Destinations");
            addXlsRow(shDest, headerStyle, 0, "Location", "Visits");
            int r = 1;
            for (FrequentDestinationDTO d : dto.getDestinations()) {
                Row row = shDest.createRow(r++);
                row.createCell(0).setCellValue(d.getLocation());
                row.createCell(1).setCellValue(d.getQuantity());
            }
            shDest.autoSizeColumn(0); shDest.autoSizeColumn(1);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void addXlsRow(Sheet sheet, CellStyle style, int rowNum, String... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            var cell = row.createCell(i);
            cell.setCellValue(values[i] != null ? values[i] : "");
            if (style != null) cell.setCellStyle(style);
        }
    }

    // ── DOCX ──────────────────────────────────────────────────────
    public ResponseEntity<byte[]> exportDocxResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"technician_report_" + registration + ".docx\"")
                    .body(generateDocx(dto));
        } catch (Exception e) {
            throw new RuntimeException("Error generating DOCX: " + e.getMessage(), e);
        }
    }

    private byte[] generateDocx(TechnicianReportDTO dto) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addDocxTitle(doc, "IPEM Control — Technician Report", 20);
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitle(doc, "1. Identification", 14);
        addDocxField(doc, "Name",           dto.getName());
        addDocxField(doc, "Registration",   str(dto.getRegistration()));
        addDocxField(doc, "CPF",            dto.getCpf());
        addDocxField(doc, "Email",          dto.getEmail());
        addDocxField(doc, "Role",           dto.getRole());
        addDocxField(doc, "License Type",   dto.getLicenseType());
        addDocxField(doc, "License Number", dto.getLicenseNumber());
        addDocxField(doc, "Birth Date",     dto.getBirthDate());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitle(doc, "2. Operational Status", 14);
        addDocxField(doc, "Active",        Boolean.TRUE.equals(dto.getActive()) ? "Yes" : "No");
        addDocxField(doc, "Open Departure",Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Yes" : "No");
        addDocxField(doc, "Registered At", dto.getRegistrationDate());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitle(doc, "3. Operational Behavior", 14);
        addDocxField(doc, "Avg Duration (h)",       fmt(dto.getAvgDurationHours()));
        addDocxField(doc, "Longest Departure (km)", fmt(dto.getLongestKm()));
        addDocxField(doc, "Longest Duration (h)",   fmt(dto.getLongestDurationHours()));
        addDocxField(doc, "Departures/week",        fmt(dto.getDepartureFrequencyPerWeek()));
        addDocxParagraph(doc, " ", false, 11);

        if (dto.getDocuments() != null) {
            addDocxTitle(doc, "4. Documents", 14);
            addDocxField(doc, "Received",   str(dto.getDocuments().getReceived()));
            addDocxField(doc, "Read",       str(dto.getDocuments().getRead()));
            addDocxField(doc, "Downloaded", str(dto.getDocuments().getDownloaded()));
            addDocxParagraph(doc, " ", false, 11);
        }

        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            addDocxTitle(doc, "Top Destinations", 14);
            for (FrequentDestinationDTO d : dto.getDestinations())
                addDocxParagraph(doc, "• " + d.getLocation() + " — " + d.getQuantity() + " visit(s)",
                        false, 11);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.write(baos);
        doc.close();
        return baos.toByteArray();
    }

    private void addDocxTitle(XWPFDocument doc, String text, int size) {
        XWPFRun run = doc.createParagraph().createRun();
        run.setText(text); run.setBold(true); run.setFontSize(size);
    }

    private void addDocxParagraph(XWPFDocument doc, String text, boolean bold, int size) {
        XWPFRun run = doc.createParagraph().createRun();
        run.setText(text != null ? text : ""); run.setBold(bold); run.setFontSize(size);
    }

    private void addDocxField(XWPFDocument doc, String label, String value) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun bold = p.createRun();
        bold.setBold(true); bold.setText(label + ": ");
        XWPFRun val = p.createRun();
        val.setText(value != null ? value : "—");
    }

    // ── Helpers ───────────────────────────────────────────────────
    private String fmt(BigDecimal v) { return v != null ? v.toPlainString() : "0"; }
    private String fmt(Double v)     { return v != null ? String.format("%.2f", v) : "0"; }
    private String str(Object v)     { return v != null ? v.toString() : ""; }
}