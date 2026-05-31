package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.FuelingItemDTO;
import br.com.edu.fatec.ipemControl.dto.FuelReportDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class FuelingExportService {

    private static final String[] HEADERS = {
            "Date/Time", "Vehicle", "Responsible", "Fuel Type",
            "Liters", "Total Value", "Mileage", "Station", "City", "Invoice"
    };

    public byte[] export(FuelReportDTO report, String format) {
        return switch (format) {
            case "csv"   -> exportCsv(report);
            case "excel" -> exportExcel(report);
            case "pdf"   -> exportPdf(report);
            case "docx"  -> exportDocx(report);
            default      -> exportCsv(report);
        };
    }

    private String[] toRow(FuelingItemDTO item) {
        return new String[]{
                item.getFuelingDatetime()    != null ? item.getFuelingDatetime().toString() : "",
                item.getVehicle()            != null ? item.getVehicle()                   : "",
                item.getResponsible()        != null ? item.getResponsible()               : "",
                item.getFuelTypeName()       != null ? item.getFuelTypeName()              : "",
                item.getLiters()             != null ? item.getLiters().toString()         : "",
                item.getTotalValue()         != null ? item.getTotalValue().toString()     : "",
                item.getMileageAtFueling()   != null ? item.getMileageAtFueling().toString(): "",
                item.getStationName()        != null ? item.getStationName()               : "",
                item.getStationCity()        != null ? item.getStationCity()               : "",
                item.getInvoiceNumber()      != null ? item.getInvoiceNumber()             : "—"
        };
    }

    private byte[] exportCsv(FuelReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos))) {
            writer.writeNext(HEADERS);
            for (FuelingItemDTO item : report.getFuelings()) writer.writeNext(toRow(item));
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    private byte[] exportExcel(FuelReportDTO report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Fueling");
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) header.createCell(i).setCellValue(HEADERS[i]);

            List<FuelingItemDTO> items = report.getFuelings();
            for (int i = 0; i < items.size(); i++) {
                String[] row = toRow(items.get(i));
                Row xlsRow = sheet.createRow(i + 1);
                for (int j = 0; j < row.length; j++) xlsRow.createCell(j).setCellValue(row[j]);
            }
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    private byte[] exportPdf(FuelReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(new PdfDocument(new PdfWriter(baos)));
            document.add(new Paragraph("Fueling Report").setBold().setFontSize(16));
            Table table = new Table(HEADERS.length);
            for (String h : HEADERS) table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));
            for (FuelingItemDTO item : report.getFuelings())
                for (String cell : toRow(item)) table.addCell(cell);
            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private byte[] exportDocx(FuelReportDTO report) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XWPFParagraph title = doc.createParagraph();
            XWPFRun run = title.createRun();
            run.setText("Fueling Report");
            run.setBold(true);
            run.setFontSize(16);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText(HEADERS[0]);
            for (int i = 1; i < HEADERS.length; i++) headerRow.addNewTableCell().setText(HEADERS[i]);

            for (FuelingItemDTO item : report.getFuelings()) {
                String[] row = toRow(item);
                XWPFTableRow xlsRow = table.createRow();
                for (int i = 0; i < row.length; i++) xlsRow.getCell(i).setText(row[i]);
            }
            doc.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating DOCX", e);
        }
    }
}