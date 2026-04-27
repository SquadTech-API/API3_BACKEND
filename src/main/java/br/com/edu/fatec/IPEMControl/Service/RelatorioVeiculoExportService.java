package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Service
public class RelatorioVeiculoExportService {

    public ResponseEntity<byte[]> exportarPdf(RelatorioVeiculoDTO dto, Integer idVeiculo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("RELATÓRIO DE VEÍCULO").setBold().setFontSize(18));

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();

            addRow(table, "Prefixo", dto.getPrefixo());
            addRow(table, "Placa", dto.getPlaca());
            addRow(table, "Marca", dto.getMarca());
            addRow(table, "Modelo", dto.getModelo());
            addRow(table, "Ano", str(dto.getAno()));
            addRow(table, "Combustível", dto.getCombustivel());
            addRow(table, "KM Rodado", fmt(dto.getKmRodado()));
            addRow(table, "Consumo Médio", fmt(dto.getConsumoMedio()));
            addRow(table, "Total de Saídas", str(dto.getTotalSaidas()));

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=relatorio_veiculo_" + idVeiculo + ".pdf")
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public ResponseEntity<byte[]> exportarCsv(RelatorioVeiculoDTO dto, Integer idVeiculo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
            CSVWriter csv = new CSVWriter(writer);

            csv.writeNext(new String[]{"Campo", "Valor"});
            csv.writeNext(new String[]{"Prefixo", dto.getPrefixo()});
            csv.writeNext(new String[]{"Placa", dto.getPlaca()});
            csv.writeNext(new String[]{"Marca", dto.getMarca()});
            csv.writeNext(new String[]{"Modelo", dto.getModelo()});
            csv.writeNext(new String[]{"Ano", str(dto.getAno())});
            csv.writeNext(new String[]{"Combustível", dto.getCombustivel()});
            csv.writeNext(new String[]{"KM Rodado", fmt(dto.getKmRodado())});
            csv.writeNext(new String[]{"Consumo Médio", fmt(dto.getConsumoMedio())});
            csv.writeNext(new String[]{"Total de Saídas", str(dto.getTotalSaidas())});

            csv.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=relatorio_veiculo_" + idVeiculo + ".csv")
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    public ResponseEntity<byte[]> exportarExcel(RelatorioVeiculoDTO dto, Integer idVeiculo) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Veículo");

            addXlsRow(sheet, 0, "Campo", "Valor");
            addXlsRow(sheet, 1, "Prefixo", dto.getPrefixo());
            addXlsRow(sheet, 2, "Placa", dto.getPlaca());
            addXlsRow(sheet, 3, "Marca", dto.getMarca());
            addXlsRow(sheet, 4, "Modelo", dto.getModelo());
            addXlsRow(sheet, 5, "Ano", str(dto.getAno()));
            addXlsRow(sheet, 6, "Combustível", dto.getCombustivel());
            addXlsRow(sheet, 7, "KM Rodado", fmt(dto.getKmRodado()));
            addXlsRow(sheet, 8, "Consumo Médio", fmt(dto.getConsumoMedio()));
            addXlsRow(sheet, 9, "Total de Saídas", str(dto.getTotalSaidas()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=relatorio_veiculo_" + idVeiculo + ".xlsx")
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    public ResponseEntity<byte[]> exportarDocx(RelatorioVeiculoDTO dto, Integer idVeiculo) {
        try {
            XWPFDocument doc = new XWPFDocument();

            addDocxField(doc, "Prefixo", dto.getPrefixo());
            addDocxField(doc, "Placa", dto.getPlaca());
            addDocxField(doc, "Marca", dto.getMarca());
            addDocxField(doc, "Modelo", dto.getModelo());
            addDocxField(doc, "Ano", str(dto.getAno()));
            addDocxField(doc, "Combustível", dto.getCombustivel());
            addDocxField(doc, "KM Rodado", fmt(dto.getKmRodado()));
            addDocxField(doc, "Consumo Médio", fmt(dto.getConsumoMedio()));
            addDocxField(doc, "Total de Saídas", str(dto.getTotalSaidas()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.write(baos);
            doc.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=relatorio_veiculo_" + idVeiculo + ".docx")
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar DOCX", e);
        }
    }

    private void addRow(Table table, String campo, String valor) {
        table.addCell(new Cell().add(new Paragraph(campo)));
        table.addCell(new Cell().add(new Paragraph(valor)));
    }

    private void addXlsRow(Sheet sheet, int rowNum, String campo, String valor) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(campo);
        row.createCell(1).setCellValue(valor);
    }

    private void addDocxField(XWPFDocument doc, String campo, String valor) {
        var p = doc.createParagraph();
        var run = p.createRun();
        run.setText(campo + ": " + valor);
    }

    private String fmt(Double valor) {
        return valor != null ? String.format("%.2f", valor) : "0.00";
    }

    private String str(Object valor) {
        return valor != null ? valor.toString() : "";
    }
}