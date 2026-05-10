package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.DestinoFrequenteDTO;
import br.com.edu.fatec.IPEMControl.DTO.RelatorioTecnicoDTO;
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
public class RelatorioExportService {

    // ════════════════════════════════════════════════════════════════════════
    //  PDF — iText 7
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarPdfResponse(RelatorioTecnicoDTO dto, Integer matricula) {
        try {
            byte[] bytes = gerarPdf(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + matricula + ".pdf\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    private byte[] gerarPdf(RelatorioTecnicoDTO dto) {
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
        addRow(tId, "Nome",           dto.getNome());
        addRow(tId, "Matrícula",      str(dto.getMatricula()));
        addRow(tId, "CPF",            dto.getCpf());
        addRow(tId, "Email",          dto.getEmail());
        addRow(tId, "Cargo",          dto.getCargo());
        addRow(tId, "CNH",            dto.getCnh());
        addRow(tId, "Nº Habilitação", dto.getNumHabilitacao());
        addRow(tId, "Nascimento",     dto.getDataNascimento());
        document.add(tId);
        document.add(new Paragraph(" "));

        // Status
        document.add(new Paragraph("2. Status Operacional").setFontSize(14).setBold());
        Table tSt = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tSt, "Ativo",              Boolean.TRUE.equals(dto.getAtivo()) ? "Sim" : "Não");
        addRow(tSt, "Saída em aberto",    Boolean.TRUE.equals(dto.getSaidaEmAberto()) ? "Sim" : "Não");
        addRow(tSt, "Cadastro",           dto.getDataCadastro());
        addRow(tSt, "Última atualização", dto.getUltimaAtualizacao());
        document.add(tSt);
        document.add(new Paragraph(" "));

        // Comportamento
        document.add(new Paragraph("4. Comportamento Operacional").setFontSize(14).setBold());
        Table tComp = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tComp, "Tempo médio saída (h)",  fmt(dto.getTempoMedioSaidaHoras()));
        addRow(tComp, "Maior saída (km)",       fmt(dto.getMaiorSaidaKm()));
        addRow(tComp, "Maior duração (h)",      fmt(dto.getMaiorSaidaDuracaoHoras()));
        addRow(tComp, "Freq. saídas/semana",    fmt(dto.getFrequenciaSaidasPorSemana()));
        document.add(tComp);
        document.add(new Paragraph(" "));

        // Manutenção
        document.add(new Paragraph("6. Manutenção").setFontSize(14).setBold());
        Table tMan = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(tMan, "Trocas de óleo",    str(dto.getTrocasOleo()));
        addRow(tMan, "Última troca",      dto.getUltimaTrocaOleo());
        document.add(tMan);
        document.add(new Paragraph(" "));

        // Documentos
        if (dto.getDocumentos() != null) {
            document.add(new Paragraph("7. Documentos").setFontSize(14).setBold());
            Table tDoc = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addRow(tDoc, "Recebidos", str(dto.getDocumentos().getRecebidos()));
            addRow(tDoc, "Lidos",     str(dto.getDocumentos().getLidos()));
            addRow(tDoc, "Baixados",  str(dto.getDocumentos().getBaixados()));
            document.add(tDoc);
            document.add(new Paragraph(" "));
        }

        // Destinos
        if (dto.getDestinos() != null && !dto.getDestinos().isEmpty()) {
            document.add(new Paragraph("Destinos Mais Frequentes").setFontSize(14).setBold());
            Table tDest = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            tDest.addHeaderCell(new Cell().add(new Paragraph("Local").setBold()));
            tDest.addHeaderCell(new Cell().add(new Paragraph("Visitas").setBold()));
            for (DestinoFrequenteDTO d : dto.getDestinos()) {
                tDest.addCell(d.getLocal());
                tDest.addCell(String.valueOf(d.getQuantidade()));
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

    public ResponseEntity<byte[]> exportarCsvResponse(RelatorioTecnicoDTO dto, Integer matricula) {
        try {
            byte[] bytes = gerarCsv(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + matricula + ".csv\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV: " + e.getMessage(), e);
        }
    }

    private byte[] gerarCsv(RelatorioTecnicoDTO dto) throws IOException {
        ByteArrayOutputStream baos   = new ByteArrayOutputStream();
        OutputStreamWriter    writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVWriter             csv    = new CSVWriter(writer);

        csv.writeNext(new String[]{"Seção", "Campo", "Valor"});

        csv.writeNext(new String[]{"Identificação", "Nome",          dto.getNome()});
        csv.writeNext(new String[]{"Identificação", "Matrícula",     str(dto.getMatricula())});
        csv.writeNext(new String[]{"Identificação", "CPF",           dto.getCpf()});
        csv.writeNext(new String[]{"Identificação", "Email",         dto.getEmail()});
        csv.writeNext(new String[]{"Identificação", "Cargo",         dto.getCargo()});
        csv.writeNext(new String[]{"Identificação", "CNH",           dto.getCnh()});
        csv.writeNext(new String[]{"Identificação", "Nº Habilitação",dto.getNumHabilitacao()});
        csv.writeNext(new String[]{"Identificação", "Nascimento",    dto.getDataNascimento()});

        csv.writeNext(new String[]{"Status", "Ativo",
                Boolean.TRUE.equals(dto.getAtivo()) ? "Sim" : "Não"});
        csv.writeNext(new String[]{"Status", "Saída em aberto",
                Boolean.TRUE.equals(dto.getSaidaEmAberto()) ? "Sim" : "Não"});

        csv.writeNext(new String[]{"Comportamento", "Tempo médio (h)",     fmt(dto.getTempoMedioSaidaHoras())});
        csv.writeNext(new String[]{"Comportamento", "Maior saída (km)",    fmt(dto.getMaiorSaidaKm())});
        csv.writeNext(new String[]{"Comportamento", "Freq. saídas/semana", fmt(dto.getFrequenciaSaidasPorSemana())});

        if (dto.getSaidasPorPeriodo() != null) {
            for (Map.Entry<String, Long> e : dto.getSaidasPorPeriodo().entrySet()) {
                csv.writeNext(new String[]{"Saídas/período", e.getKey(), str(e.getValue())});
            }
        }
        if (dto.getGastoPorPeriodo() != null) {
            for (Map.Entry<String, BigDecimal> e : dto.getGastoPorPeriodo().entrySet()) {
                csv.writeNext(new String[]{"Gasto/período", e.getKey(), fmt(e.getValue())});
            }
        }

        if (dto.getDocumentos() != null) {
            csv.writeNext(new String[]{"Documentos", "Recebidos", str(dto.getDocumentos().getRecebidos())});
            csv.writeNext(new String[]{"Documentos", "Lidos",     str(dto.getDocumentos().getLidos())});
            csv.writeNext(new String[]{"Documentos", "Baixados",  str(dto.getDocumentos().getBaixados())});
        }

        if (dto.getDestinos() != null) {
            for (DestinoFrequenteDTO d : dto.getDestinos()) {
                csv.writeNext(new String[]{"Destinos", d.getLocal(), str(d.getQuantidade())});
            }
        }

        csv.close();
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Excel — Apache POI (XLSX)
    // ════════════════════════════════════════════════════════════════════════

    public ResponseEntity<byte[]> exportarExcelResponse(RelatorioTecnicoDTO dto, Integer matricula) {
        try {
            byte[] bytes = gerarExcel(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + matricula + ".xlsx\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel: " + e.getMessage(), e);
        }
    }

    private byte[] gerarExcel(RelatorioTecnicoDTO dto) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle cabecalho = criarEstiloCabecalho(workbook);

        // Aba 1 — Identificação e Status
        Sheet sheId = workbook.createSheet("Identificação");
        addXlsRow(sheId, cabecalho, 0, "Campo", "Valor");
        addXlsRow(sheId, null, 1, "Nome",           dto.getNome());
        addXlsRow(sheId, null, 2, "Matrícula",      str(dto.getMatricula()));
        addXlsRow(sheId, null, 3, "CPF",            dto.getCpf());
        addXlsRow(sheId, null, 4, "Email",          dto.getEmail());
        addXlsRow(sheId, null, 5, "Cargo",          dto.getCargo());
        addXlsRow(sheId, null, 6, "CNH",            dto.getCnh());
        addXlsRow(sheId, null, 7, "Nº Habilitação", dto.getNumHabilitacao());
        addXlsRow(sheId, null, 8, "Nascimento",     dto.getDataNascimento());
        addXlsRow(sheId, null, 9, "Ativo",
                Boolean.TRUE.equals(dto.getAtivo()) ? "Sim" : "Não");
        addXlsRow(sheId, null, 10, "Saída em aberto",
                Boolean.TRUE.equals(dto.getSaidaEmAberto()) ? "Sim" : "Não");
        sheId.autoSizeColumn(0); sheId.autoSizeColumn(1);

        // Aba 2 — Comportamento
        Sheet sheComp = workbook.createSheet("Comportamento");
        addXlsRow(sheComp, cabecalho, 0, "Indicador", "Valor");
        addXlsRow(sheComp, null, 1, "Tempo médio saída (h)",  fmt(dto.getTempoMedioSaidaHoras()));
        addXlsRow(sheComp, null, 2, "Maior saída (km)",       fmt(dto.getMaiorSaidaKm()));
        addXlsRow(sheComp, null, 3, "Maior duração (h)",      fmt(dto.getMaiorSaidaDuracaoHoras()));
        addXlsRow(sheComp, null, 4, "Freq. saídas/semana",    fmt(dto.getFrequenciaSaidasPorSemana()));
        sheComp.autoSizeColumn(0); sheComp.autoSizeColumn(1);

        // Aba 3 — Saídas e KM por período
        if (dto.getSaidasPorPeriodo() != null) {
            Sheet sheUso = workbook.createSheet("Saídas por Período");
            addXlsRow(sheUso, cabecalho, 0, "Período", "Saídas", "KM");
            int r = 1;
            for (String p : dto.getSaidasPorPeriodo().keySet()) {
                Row row = sheUso.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(str(dto.getSaidasPorPeriodo().get(p)));
                BigDecimal km = dto.getKmPorPeriodo() != null ? dto.getKmPorPeriodo().get(p) : null;
                row.createCell(2).setCellValue(fmt(km));
            }
            sheUso.autoSizeColumn(0); sheUso.autoSizeColumn(1); sheUso.autoSizeColumn(2);
        }

        // Aba 4 — Financeiro por período
        if (dto.getGastoPorPeriodo() != null) {
            Sheet sheFin = workbook.createSheet("Financeiro por Período");
            addXlsRow(sheFin, cabecalho, 0, "Período", "Gasto (R$)", "Abastecimentos");
            int r = 1;
            for (String p : dto.getGastoPorPeriodo().keySet()) {
                Row row = sheFin.createRow(r++);
                row.createCell(0).setCellValue(p);
                row.createCell(1).setCellValue(fmt(dto.getGastoPorPeriodo().get(p)));
                Long abast = dto.getAbastPorPeriodo() != null ? dto.getAbastPorPeriodo().get(p) : null;
                row.createCell(2).setCellValue(str(abast));
            }
            sheFin.autoSizeColumn(0); sheFin.autoSizeColumn(1); sheFin.autoSizeColumn(2);
        }

        // Aba 5 — Destinos
        if (dto.getDestinos() != null && !dto.getDestinos().isEmpty()) {
            Sheet sheDest = workbook.createSheet("Destinos");
            addXlsRow(sheDest, cabecalho, 0, "Local", "Visitas");
            int r = 1;
            for (DestinoFrequenteDTO d : dto.getDestinos()) {
                Row row = sheDest.createRow(r++);
                row.createCell(0).setCellValue(d.getLocal());
                row.createCell(1).setCellValue(d.getQuantidade());
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

    public ResponseEntity<byte[]> exportarDocxResponse(RelatorioTecnicoDTO dto, Integer matricula) {
        try {
            byte[] bytes = gerarDocx(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio_tecnico_" + matricula + ".docx\"")
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DOCX: " + e.getMessage(), e);
        }
    }

    private byte[] gerarDocx(RelatorioTecnicoDTO dto) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        addDocxTitulo(doc, "IPEM Control — Relatório de Técnico", 20);
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "1. Identificação", 14);
        addDocxField(doc, "Nome",           dto.getNome());
        addDocxField(doc, "Matrícula",      str(dto.getMatricula()));
        addDocxField(doc, "CPF",            dto.getCpf());
        addDocxField(doc, "Email",          dto.getEmail());
        addDocxField(doc, "Cargo",          dto.getCargo());
        addDocxField(doc, "CNH",            dto.getCnh());
        addDocxField(doc, "Nº Habilitação", dto.getNumHabilitacao());
        addDocxField(doc, "Nascimento",     dto.getDataNascimento());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "2. Status Operacional", 14);
        addDocxField(doc, "Ativo",           Boolean.TRUE.equals(dto.getAtivo()) ? "Sim" : "Não");
        addDocxField(doc, "Saída em aberto", Boolean.TRUE.equals(dto.getSaidaEmAberto()) ? "Sim" : "Não");
        addDocxField(doc, "Data cadastro",   dto.getDataCadastro());
        addDocxParagraph(doc, " ", false, 11);

        addDocxTitulo(doc, "4. Comportamento Operacional", 14);
        addDocxField(doc, "Tempo médio saída (h)", fmt(dto.getTempoMedioSaidaHoras()));
        addDocxField(doc, "Maior saída (km)",      fmt(dto.getMaiorSaidaKm()));
        addDocxField(doc, "Maior duração (h)",     fmt(dto.getMaiorSaidaDuracaoHoras()));
        addDocxField(doc, "Freq. saídas/semana",   fmt(dto.getFrequenciaSaidasPorSemana()));
        addDocxParagraph(doc, " ", false, 11);

        if (dto.getDocumentos() != null) {
            addDocxTitulo(doc, "7. Documentos", 14);
            addDocxField(doc, "Recebidos", str(dto.getDocumentos().getRecebidos()));
            addDocxField(doc, "Lidos",     str(dto.getDocumentos().getLidos()));
            addDocxField(doc, "Baixados",  str(dto.getDocumentos().getBaixados()));
            addDocxParagraph(doc, " ", false, 11);
        }

        if (dto.getDestinos() != null && !dto.getDestinos().isEmpty()) {
            addDocxTitulo(doc, "Destinos Mais Frequentes", 14);
            for (DestinoFrequenteDTO d : dto.getDestinos()) {
                addDocxParagraph(doc, "• " + d.getLocal() + " — " + d.getQuantidade() + " visita(s)",
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

