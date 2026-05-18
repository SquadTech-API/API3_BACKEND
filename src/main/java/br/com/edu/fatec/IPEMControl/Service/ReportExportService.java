package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.FrequentDestinationDTO;
import br.com.edu.fatec.IPEMControl.DTO.TechnicianReportDTO;
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

    // ════════════════════════════════════════════════════════════════════════
    //  PDF — iText 7
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarPdfResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            byte[] bytes = gerarPdf(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + registration + ".pdf\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    private byte[] gerarPdf(TechnicianReportDTO dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter   writer  = new PdfWriter(baos);
        PdfDocument pdfDoc  = new PdfDocument(writer);
        Document    document = new Document(pdfDoc);

        document.add(new Paragraph("IPEM Control — Relatório de Técnico")
                .setFontSize(18).setBold().setFontColor(ColorConstants.DARK_GRAY));
        document.add(new Paragraph(" "));

        // Identificação
        document.add(new Paragraph("1. Identificação").setFontSize(14).setBold());
        Table tId = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tId, "Nome",           dto.getName());
        addRow(tId, "Matrícula",      str(dto.getRegistration()));
        addRow(tId, "CPF",            dto.getCpf());
        addRow(tId, "Email",          dto.getEmail());
        addRow(tId, "Cargo",          dto.getRole());
        addRow(tId, "CNH",            dto.getDriversLicense());
        addRow(tId, "Nº Habilitação", dto.getLicenseNumber());
        addRow(tId, "Nascimento",     dto.getBirthDate());
        document.add(tId);
        document.add(new Paragraph(" "));

        // Status
        document.add(new Paragraph("2. Status Operacional").setFontSize(14).setBold());
        Table tSt = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tSt, "Ativo",              Boolean.TRUE.equals(dto.getActive()) ? "Sim" : "Não");
        addRow(tSt, "Saída em aberto",    Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Sim" : "Não");
        addRow(tSt, "Cadastro",           dto.getRegistrationDate());
        addRow(tSt, "Última atualização", dto.getLastUpdate());
        document.add(tSt);
        document.add(new Paragraph(" "));

        // Comportamento
        document.add(new Paragraph("4. Comportamento Operacional").setFontSize(14).setBold());
        Table tComp = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tComp, "Tempo médio saída (h)",  fmt(dto.getAvgDepartureDurationHours()));
        addRow(tComp, "Maior saída (mileage)",       fmt(dto.getLongestDepartureKm()));
        addRow(tComp, "Maior duração (h)",      fmt(dto.getLongestDepartureDurationHours()));
        addRow(tComp, "Freq. saídas/semana",    fmt(dto.getDepartureFrequencyPerWeek()));
        document.add(tComp);
        document.add(new Paragraph(" "));

        // Manutenção
        document.add(new Paragraph("6. Manutenção").setFontSize(14).setBold());
        Table tMan = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tMan, "Trocas de óleo",    str(dto.getOilChanges()));
        addRow(tMan, "Última oilChange",      dto.getLastOilChange());
        document.add(tMan);
        document.add(new Paragraph(" "));

        // Documentos
        if (dto.getDocuments() != null) {
            document.add(new Paragraph("7. Documentos").setFontSize(14).setBold());
            Table tDoc = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addRow(tDoc, "Recebidos", str(dto.getDocuments().getReceived()));
            addRow(tDoc, "Lidos",     str(dto.getDocuments().getRead()));
            addRow(tDoc, "Baixados",  str(dto.getDocuments().getDownloaded()));
            document.add(tDoc);
            document.add(new Paragraph(" "));
        }

        // Destinos
        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            document.add(new Paragraph("Destinos Mais Frequentes").setFontSize(14).setBold());
            Table tDest = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            tDest.addHeaderCell(new Cell().add(new Paragraph("Local").setBold()));
            tDest.addHeaderCell(new Cell().add(new Paragraph("Visitas").setBold()));
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

    // ════════════════════════════════════════════════════════════════════════
    //  CSV — OpenCSV
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarCsvResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            byte[] bytes = gerarCsv(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + registration + ".csv\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV: " + e.getMessage(), e);
        }
    }

    private byte[] gerarCsv(TechnicianReportDTO dto) throws IOException {
        ByteArrayOutputStream baos   = new ByteArrayOutputStream();
        OutputStreamWriter    writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVWriter             csv    = new CSVWriter(writer);

        csv.writeNext(new String[]{"Seção", "Campo", "Valor"});

        csv.writeNext(new String[]{"Identificação", "Nome",          dto.getName()});
        csv.writeNext(new String[]{"Identificação", "Matrícula",     str(dto.getRegistration())});
        csv.writeNext(new String[]{"Identificação", "CPF",           dto.getCpf()});
        csv.writeNext(new String[]{"Identificação", "Email",         dto.getEmail()});
        csv.writeNext(new String[]{"Identificação", "Cargo",         dto.getRole()});
        csv.writeNext(new String[]{"Identificação", "CNH",           dto.getDriversLicense()});
        csv.writeNext(new String[]{"Identificação", "Nº Habilitação",dto.getLicenseNumber()});
        csv.writeNext(new String[]{"Identificação", "Nascimento",    dto.getBirthDate()});

        csv.writeNext(new String[]{"Status", "Ativo",
                Boolean.TRUE.equals(dto.getActive()) ? "Sim" : "Não"});
        csv.writeNext(new String[]{"Status", "Saída em aberto",
                Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Sim" : "Não"});

        csv.writeNext(new String[]{"Comportamento", "Tempo médio (h)",     fmt(dto.getAvgDepartureDurationHours())});
        csv.writeNext(new String[]{"Comportamento", "Maior saída (mileage)",    fmt(dto.getLongestDepartureKm())});
        csv.writeNext(new String[]{"Comportamento", "Freq. saídas/semana", fmt(dto.getDepartureFrequencyPerWeek())});

        if (dto.getDeparturesByPeriod() != null) {
            for (Map.Entry<String, Long> e : dto.getDeparturesByPeriod().entrySet()) {
                csv.writeNext(new String[]{"Saídas/período", e.getKey(), str(e.getValue())});
            }
        }
        if (dto.getSpendingByPeriod() != null) {
            for (Map.Entry<String, BigDecimal> e : dto.getSpendingByPeriod().entrySet()) {
                csv.writeNext(new String[]{"Gasto/período", e.getKey(), fmt(e.getValue())});
            }
        }

        if (dto.getDocuments() != null) {
            csv.writeNext(new String[]{"Documentos", "Recebidos", str(dto.getDocuments().getReceived())});
            csv.writeNext(new String[]{"Documentos", "Lidos",     str(dto.getDocuments().getRead())});
            csv.writeNext(new String[]{"Documentos", "Baixados",  str(dto.getDocuments().getDownloaded())});
        }

        if (dto.getDestinations() != null) {
            for (FrequentDestinationDTO d : dto.getDestinations()) {
                csv.writeNext(new String[]{"Destinos", d.getLocation(), str(d.getQuantity())});
            }
        }

        csv.close();
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Excel — Apache POI (XLSX)
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarExcelResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            byte[] bytes = gerarExcel(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + registration + ".xlsx\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }

    private byte[] gerarExcel(TechnicianReportDTO dto) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle cabecalho = criarEstiloCabecalho(workbook);

        // Aba 1 — Identificação e Status
        Sheet sheId = workbook.createSheet("Identificação");
        addXlsRow(sheId, cabecalho, 0, "Campo", "Valor");
        addXlsRow(sheId, null, 1, "Nome",           dto.getName());
        addXlsRow(sheId, null, 2, "Matrícula",      str(dto.getRegistration()));
        addXlsRow(sheId, null, 3, "CPF",            dto.getCpf());
        addXlsRow(sheId, null, 4, "Email",          dto.getEmail());
        addXlsRow(sheId, null, 5, "Cargo",          dto.getRole());
        addXlsRow(sheId, null, 6, "CNH",            dto.getDriversLicense());
        addXlsRow(sheId, null, 7, "Nº Habilitação", dto.getLicenseNumber());
        addXlsRow(sheId, null, 8, "Nascimento",     dto.getBirthDate());
        addXlsRow(sheId, null, 9, "Ativo",
                Boolean.TRUE.equals(dto.getActive()) ? "Sim" : "Não");
        addXlsRow(sheId, null, 10, "Saída em aberto",
                Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Sim" : "Não");
        sheId.autoSizeColumn(0); sheId.autoSizeColumn(1);

        // Aba 2 — Comportamento
        Sheet sheComp = workbook.createSheet("Comportamento");
        addXlsRow(sheComp, cabecalho, 0, "Indicador", "Valor");
        addXlsRow(sheComp, null, 1, "Tempo médio saída (h)",  fmt(dto.getAvgDepartureDurationHours()));
        addXlsRow(sheComp, null, 2, "Maior saída (mileage)",       fmt(dto.getLongestDepartureKm()));
        addXlsRow(sheComp, null, 3, "Maior duração (h)",      fmt(dto.getLongestDepartureDurationHours()));
        addXlsRow(sheComp, null, 4, "Freq. saídas/semana",    fmt(dto.getDepartureFrequencyPerWeek()));
        sheComp.autoSizeColumn(0); sheComp.autoSizeColumn(1);

        // Aba 3 — Saídas e KM por período
        if (dto.getDeparturesByPeriod() != null) {
            Sheet sheUso = workbook.createSheet("Saídas por Período");
            addXlsRow(sheUso, cabecalho, 0, "Período", "Saídas", "KM");
            int r = 1;
            for (String p : dto.getDeparturesByPeriod().keySet()) {
                Row row = sheUso.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(str(dto.getDeparturesByPeriod().get(p)));
                BigDecimal km = dto.getKmByPeriod() != null ? dto.getKmByPeriod().get(p) : null;
                row.createCell(2).setCellValue(fmt(km));
            }
            sheUso.autoSizeColumn(0); sheUso.autoSizeColumn(1); sheUso.autoSizeColumn(2);
        }

        // Aba 4 — Financeiro por período
        if (dto.getSpendingByPeriod() != null) {
            Sheet sheFin = workbook.createSheet("Financeiro por Período");
            addXlsRow(sheFin, cabecalho, 0, "Período", "Gasto (R$)", "Abastecimentos");
            int r = 1;
            for (String p : dto.getSpendingByPeriod().keySet()) {
                Row row = sheFin.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(fmt(dto.getSpendingByPeriod().get(p)));
                Long abast = dto.getRefuelsByPeriod() != null ? dto.getRefuelsByPeriod().get(p) : null;
                row.createCell(2).setCellValue(str(abast));
            }
            sheFin.autoSizeColumn(0); sheFin.autoSizeColumn(1); sheFin.autoSizeColumn(2);
        }

        // Aba 5 — Destinos
        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            Sheet sheDest = workbook.createSheet("Destinos");
            addXlsRow(sheDest, cabecalho, 0, "Local", "Visitas");
            int r = 1;
            for (FrequentDestinationDTO d : dto.getDestinations()) {
                Row row = sheDest.createRow(r++);
                row.createCell(0).setCellValue(d.getLocation());
                row.createCell(1).setCellValue(d.getQuantity());
            }
            sheDest.autoSizeColumn(0); sheDest.autoSizeColumn(1);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private CellStyle criarEstiloCabecalho(Workbook wb) {
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
            // 'var' evita ambiguidade entre org.apache.poi.ss.usermodel.Cell
            // e com.itextpdf.layout.element.Cell (ambas importadas no mesmo arquivo)
            var cell = row.createCell(i);
            cell.setCellValue(values[i] != null ? values[i] : "");
            if (style != null) cell.setCellStyle(style);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DOCX — Apache POI
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarDocxResponse(TechnicianReportDTO dto, Integer registration) {
        try {
            byte[] bytes = gerarDocx(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + registration + ".docx\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DOCX: " + e.getMessage(), e);
        }
    }

    private byte[] gerarDocx(TechnicianReportDTO dto) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addDocxTitulo(doc, "IPEM Control — Relatório de Técnico", 20);
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "1. Identificação", 14);
        addDocxField(doc, "Nome",           dto.getName());
        addDocxField(doc, "Matrícula",      str(dto.getRegistration()));
        addDocxField(doc, "CPF",            dto.getCpf());
        addDocxField(doc, "Email",          dto.getEmail());
        addDocxField(doc, "Cargo",          dto.getRole());
        addDocxField(doc, "CNH",            dto.getDriversLicense());
        addDocxField(doc, "Nº Habilitação", dto.getLicenseNumber());
        addDocxField(doc, "Nascimento",     dto.getBirthDate());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "2. Status Operacional", 14);
        addDocxField(doc, "Ativo",           Boolean.TRUE.equals(dto.getActive()) ? "Sim" : "Não");
        addDocxField(doc, "Saída em aberto", Boolean.TRUE.equals(dto.getOpenDeparture()) ? "Sim" : "Não");
        addDocxField(doc, "Data cadastro",   dto.getRegistrationDate());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "4. Comportamento Operacional", 14);
        addDocxField(doc, "Tempo médio saída (h)", fmt(dto.getAvgDepartureDurationHours()));
        addDocxField(doc, "Maior saída (mileage)",      fmt(dto.getLongestDepartureKm()));
        addDocxField(doc, "Maior duração (h)",     fmt(dto.getLongestDepartureDurationHours()));
        addDocxField(doc, "Freq. saídas/semana",   fmt(dto.getDepartureFrequencyPerWeek()));
        addDocxParagraph(doc, " ", false, 11);

        if (dto.getDocuments() != null) {
            addDocxTitulo(doc, "7. Documentos", 14);
            addDocxField(doc, "Recebidos", str(dto.getDocuments().getReceived()));
            addDocxField(doc, "Lidos",     str(dto.getDocuments().getRead()));
            addDocxField(doc, "Baixados",  str(dto.getDocuments().getDownloaded()));
            addDocxParagraph(doc, " ", false, 11);
        }

        if (dto.getDestinations() != null && !dto.getDestinations().isEmpty()) {
            addDocxTitulo(doc, "Destinos Mais Frequentes", 14);
            for (FrequentDestinationDTO d : dto.getDestinations()) {
                addDocxParagraph(doc, "• " + d.getLocation() + " — " + d.getQuantity() + " visita(s)",
                        false, 11);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.write(baos);
        doc.close();
        return baos.toByteArray();
    }

    private void addDocxTitulo(XWPFDocument doc, String text, int size) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(size);
    }

    private void addDocxParagraph(XWPFDocument doc, String text, boolean bold, int size) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text != null ? text : "");
        run.setBold(bold);
        run.setFontSize(size);
    }

    private void addDocxField(XWPFDocument doc, String label, String value) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun bold = p.createRun();
        bold.setBold(true);
        bold.setText(label + ": ");
        XWPFRun val = p.createRun();
        val.setText(value != null ? value : "—");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String fmt(BigDecimal v) { return v != null ? v.toPlainString() : "0"; }
    private String fmt(Double v)     { return v != null ? String.format("%.2f", v) : "0"; }
    private String str(Object v)     { return v != null ? v.toString() : ""; }
}

