package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.FuelingItemDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelReportDTO;
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
public class AbastecimentoExportService {

    public byte[] exportar(FuelReportDTO relatorio, String formato) {
        return switch (formato) {
            case "csv"   -> exportarCsv(relatorio);
            case "excel" -> exportarExcel(relatorio);
            case "pdf"   -> exportarPdf(relatorio);
            case "docx"  -> exportarDocx(relatorio);
            default      -> exportarCsv(relatorio);
        };
    }

    private byte[] exportarCsv(FuelReportDTO relatorio) {
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream();
             CSVWriter escritor = new CSVWriter(new OutputStreamWriter(saida))) {

            escritor.writeNext(new String[]{
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            });

            for (FuelingItemDTO item : relatorio.getRefuels()) {
                escritor.writeNext(new String[]{
                        item.getDateTime()           != null ? item.getDateTime().toString()          : "",
                        item.getVehicle()             != null ? item.getVehicle()                     : "",
                        item.getResponsiblePerson()         != null ? item.getResponsiblePerson()                 : "",
                        item.getFuelType()     != null ? item.getFuelType()             : "",
                        item.getLitersQuantity()    != null ? item.getLitersQuantity().toString() : "",
                        item.getTotalAmount()          != null ? item.getTotalAmount().toString()       : "",
                        item.getFuelingMileage()     != null ? item.getFuelingMileage().toString()  : "",
                        item.getGasStationName()           != null ? item.getGasStationName()                   : "",
                        item.getGasStationCity()         != null ? item.getGasStationCity()                 : "",
                        item.getInvoiceNumber()          != null ? item.getInvoiceNumber()                  : "—"
                });
            }

            escritor.flush();
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV.", e);
        }
    }

    private byte[] exportarExcel(FuelReportDTO relatorio) {
        try (Workbook planilha = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            Sheet aba = planilha.createSheet("Abastecimentos");

            Row cabecalho = aba.createRow(0);
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };
            for (int i = 0; i < colunas.length; i++) {
                cabecalho.createCell(i).setCellValue(colunas[i]);
            }

            List<FuelingItemDTO> abastecimentos = relatorio.getRefuels();
            for (int i = 0; i < abastecimentos.size(); i++) {
                FuelingItemDTO item = abastecimentos.get(i);
                Row linha = aba.createRow(i + 1);
                linha.createCell(0).setCellValue(item.getDateTime()        != null ? item.getDateTime().toString()           : "");
                linha.createCell(1).setCellValue(item.getVehicle()          != null ? item.getVehicle()                      : "");
                linha.createCell(2).setCellValue(item.getResponsiblePerson()      != null ? item.getResponsiblePerson()                  : "");
                linha.createCell(3).setCellValue(item.getFuelType()  != null ? item.getFuelType()              : "");
                linha.createCell(4).setCellValue(item.getLitersQuantity() != null ? item.getLitersQuantity().doubleValue(): 0);
                linha.createCell(5).setCellValue(item.getTotalAmount()       != null ? item.getTotalAmount().doubleValue()      : 0);
                linha.createCell(6).setCellValue(item.getFuelingMileage()  != null ? item.getFuelingMileage().doubleValue() : 0);
                linha.createCell(7).setCellValue(item.getGasStationName()        != null ? item.getGasStationName()                    : "");
                linha.createCell(8).setCellValue(item.getGasStationCity()      != null ? item.getGasStationCity()                  : "");
                linha.createCell(9).setCellValue(item.getInvoiceNumber()       != null ? item.getInvoiceNumber()                   : "—");
            }

            planilha.write(saida);
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel.", e);
        }
    }

    private byte[] exportarPdf(FuelReportDTO relatorio) {
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            PdfWriter escritor = new PdfWriter(saida);
            PdfDocument pdf = new PdfDocument(escritor);
            Document documento = new Document(pdf);

            documento.add(new Paragraph("Relatório de Abastecimentos")
                    .setBold()
                    .setFontSize(16));

            Table tabela = new Table(10);
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };

            for (String coluna : colunas) {
                tabela.addHeaderCell(new Cell().add(new Paragraph(coluna).setBold()));
            }

            for (FuelingItemDTO item : relatorio.getRefuels()) {
                tabela.addCell(item.getDateTime()        != null ? item.getDateTime().toString()          : "");
                tabela.addCell(item.getVehicle()          != null ? item.getVehicle()                     : "");
                tabela.addCell(item.getResponsiblePerson()      != null ? item.getResponsiblePerson()                 : "");
                tabela.addCell(item.getFuelType()  != null ? item.getFuelType()             : "");
                tabela.addCell(item.getLitersQuantity() != null ? item.getLitersQuantity().toString() : "");
                tabela.addCell(item.getTotalAmount()       != null ? item.getTotalAmount().toString()       : "");
                tabela.addCell(item.getFuelingMileage()  != null ? item.getFuelingMileage().toString()  : "");
                tabela.addCell(item.getGasStationName()        != null ? item.getGasStationName()                   : "");
                tabela.addCell(item.getGasStationCity()      != null ? item.getGasStationCity()                 : "");
                tabela.addCell(item.getInvoiceNumber()       != null ? item.getInvoiceNumber()                  : "—");
            }

            documento.add(tabela);
            documento.close();

            return saida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF.", e);
        }
    }

    private byte[] exportarDocx(FuelReportDTO relatorio) {
        try (XWPFDocument documento = new XWPFDocument();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            XWPFParagraph titulo = documento.createParagraph();
            XWPFRun textoTitulo = titulo.createRun();
            textoTitulo.setText("Relatório de Abastecimentos");
            textoTitulo.setBold(true);
            textoTitulo.setFontSize(16);

            XWPFTable tabela = documento.createTable();
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };

            XWPFTableRow linhaCabecalho = tabela.getRow(0);
            linhaCabecalho.getCell(0).setText(colunas[0]);
            for (int i = 1; i < colunas.length; i++) {
                linhaCabecalho.addNewTableCell().setText(colunas[i]);
            }

            for (FuelingItemDTO item : relatorio.getRefuels()) {
                XWPFTableRow linha = tabela.createRow();
                linha.getCell(0).setText(item.getDateTime()        != null ? item.getDateTime().toString()          : "");
                linha.getCell(1).setText(item.getVehicle()          != null ? item.getVehicle()                     : "");
                linha.getCell(2).setText(item.getResponsiblePerson()      != null ? item.getResponsiblePerson()                 : "");
                linha.getCell(3).setText(item.getFuelType()  != null ? item.getFuelType()             : "");
                linha.getCell(4).setText(item.getLitersQuantity() != null ? item.getLitersQuantity().toString() : "");
                linha.getCell(5).setText(item.getTotalAmount()       != null ? item.getTotalAmount().toString()       : "");
                linha.getCell(6).setText(item.getFuelingMileage()  != null ? item.getFuelingMileage().toString()  : "");
                linha.getCell(7).setText(item.getGasStationName()        != null ? item.getGasStationName()                   : "");
                linha.getCell(8).setText(item.getGasStationCity()      != null ? item.getGasStationCity()                 : "");
                linha.getCell(9).setText(item.getInvoiceNumber()       != null ? item.getInvoiceNumber()                  : "—");
            }

            documento.write(saida);
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar DOCX.", e);
        }
    }
}