package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyItemDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyReportDTO;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class FuelSupplyExportService {

    public byte[] export(FuelSupplyReportDTO report, String format) {
        return switch (format) {
            case "csv"   -> exportCsv(report);
            case "excel" -> exportExcel(report);
            // PDF e DOCX serão implementados quando JWT for adicionado
            default      -> exportCsv(report);
        };
    }

    private byte[] exportCsv(FuelSupplyReportDTO report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            // Cabeçalho
            writer.writeNext(new String[]{
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            });

            // Linhas
            for (FuelSupplyItemDTO item : report.getFuelSupplies()) {
                writer.writeNext(new String[]{
                        item.getDateTime()        != null ? item.getDateTime().toString()        : "",
                        item.getVehicle()         != null ? item.getVehicle()                   : "",
                        item.getResponsible()     != null ? item.getResponsible()               : "",
                        item.getFuelType()        != null ? item.getFuelType()                  : "",
                        item.getLitersAmount()    != null ? item.getLitersAmount().toString()   : "",
                        item.getTotalValue()      != null ? item.getTotalValue().toString()     : "",
                        item.getOdometerReading() != null ? item.getOdometerReading().toString(): "",
                        item.getStationName()     != null ? item.getStationName()               : "",
                        item.getStationCity()     != null ? item.getStationCity()               : "",
                        item.getInvoiceNumber()   != null ? item.getInvoiceNumber()             : "—"
                });
            }

            writer.flush();
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV.", e);
        }
    }

    private byte[] exportExcel(FuelSupplyReportDTO report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Abastecimentos");

            // Cabeçalho
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Linhas
            List<FuelSupplyItemDTO> fuelSupplies = report.getFuelSupplies();
            for (int i = 0; i < fuelSupplies.size(); i++) {
                FuelSupplyItemDTO item = fuelSupplies.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getDateTime()        != null ? item.getDateTime().toString()         : "");
                row.createCell(1).setCellValue(item.getVehicle()         != null ? item.getVehicle()                    : "");
                row.createCell(2).setCellValue(item.getResponsible()     != null ? item.getResponsible()                : "");
                row.createCell(3).setCellValue(item.getFuelType()        != null ? item.getFuelType()                   : "");
                row.createCell(4).setCellValue(item.getLitersAmount()    != null ? item.getLitersAmount().doubleValue() : 0);
                row.createCell(5).setCellValue(item.getTotalValue()      != null ? item.getTotalValue().doubleValue()   : 0);
                row.createCell(6).setCellValue(item.getOdometerReading() != null ? item.getOdometerReading().doubleValue() : 0);
                row.createCell(7).setCellValue(item.getStationName()     != null ? item.getStationName()               : "");
                row.createCell(8).setCellValue(item.getStationCity()     != null ? item.getStationCity()               : "");
                row.createCell(9).setCellValue(item.getInvoiceNumber()   != null ? item.getInvoiceNumber()             : "—");
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel.", e);
        }
    }
}