package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.VehicleReportDTO;
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
public class VehicleReportExportService {

    public ResponseEntity<byte[]> exportPdf(VehicleReportDTO dto, Integer vehicleId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(new PdfDocument(new PdfWriter(baos)));
            document.add(new Paragraph("VEHICLE REPORT — IPEM CONTROL").setBold().setFontSize(18));

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();
            addRow(table, "Prefix",          dto.getPrefix());
            addRow(table, "License Plate",   dto.getLicensePlate());
            addRow(table, "Brand",           dto.getBrand());
            addRow(table, "Model",           dto.getModel());
            addRow(table, "Year",            str(dto.getManufactureYear()));
            addRow(table, "Fuel Type",       dto.getFuelTypeName());
            addRow(table, "Total Mileage",   fmt(dto.getMileageDriven()));
            addRow(table, "Avg Consumption", fmt(dto.getAvgConsumption()));
            addRow(table, "Total Departures",str(dto.getTotalDepartures()));

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=vehicle_report_" + vehicleId + ".pdf")
                    .body(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public ResponseEntity<byte[]> exportCsv(VehicleReportDTO dto, Integer vehicleId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CSVWriter csv = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            csv.writeNext(new String[]{"Field", "Value"});
            csv.writeNext(new String[]{"Prefix",          dto.getPrefix()});
            csv.writeNext(new String[]{"License Plate",   dto.getLicensePlate()});
            csv.writeNext(new String[]{"Brand",           dto.getBrand()});
            csv.writeNext(new String[]{"Model",           dto.getModel()});
            csv.writeNext(new String[]{"Year",            str(dto.getManufactureYear())});
            csv.writeNext(new String[]{"Fuel Type",       dto.getFuelTypeName()});
            csv.writeNext(new String[]{"Total Mileage",   fmt(dto.getMileageDriven())});
            csv.writeNext(new String[]{"Avg Consumption", fmt(dto.getAvgConsumption())});
            csv.writeNext(new String[]{"Total Departures",str(dto.getTotalDepartures())});
            csv.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=vehicle_report_" + vehicleId + ".csv")
                    .body(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    public ResponseEntity<byte[]> exportExcel(VehicleReportDTO dto, Integer vehicleId) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vehicle");
            addXlsRow(sheet, 0, "Field",           "Value");
            addXlsRow(sheet, 1, "Prefix",           dto.getPrefix());
            addXlsRow(sheet, 2, "License Plate",    dto.getLicensePlate());
            addXlsRow(sheet, 3, "Brand",            dto.getBrand());
            addXlsRow(sheet, 4, "Model",            dto.getModel());
            addXlsRow(sheet, 5, "Year",             str(dto.getManufactureYear()));
            addXlsRow(sheet, 6, "Fuel Type",        dto.getFuelTypeName());
            addXlsRow(sheet, 7, "Total Mileage",    fmt(dto.getMileageDriven()));
            addXlsRow(sheet, 8, "Avg Consumption",  fmt(dto.getAvgConsumption()));
            addXlsRow(sheet, 9, "Total Departures", str(dto.getTotalDepartures()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=vehicle_report_" + vehicleId + ".xlsx")
                    .body(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }

    private void addRow(Table table, String field, String value) {
        table.addCell(new Cell().add(new Paragraph(field)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "")));
    }

    private void addXlsRow(Sheet sheet, int rowNum, String field, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(field);
        row.createCell(1).setCellValue(value != null ? value : "");
    }

    private String fmt(Double v) { return v != null ? String.format("%.2f", v) : "0.00"; }
    private String str(Object v) { return v != null ? v.toString() : ""; }
}