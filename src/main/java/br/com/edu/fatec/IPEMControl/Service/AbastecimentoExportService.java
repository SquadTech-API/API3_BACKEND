package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoItemDTO;
import br.com.edu.fatec.IPEMControl.DTO.RelatorioAbastecimentoDTO;
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

    public byte[] exportar(RelatorioAbastecimentoDTO relatorio, String formato) {
        return switch (formato) {
            case "csv"   -> exportarCsv(relatorio);
            case "excel" -> exportarExcel(relatorio);
            case "pdf"   -> exportarPdf(relatorio);
            case "docx"  -> exportarDocx(relatorio);
            default      -> exportarCsv(relatorio);
        };
    }

    private byte[] exportarCsv(RelatorioAbastecimentoDTO relatorio) {
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream();
             CSVWriter escritor = new CSVWriter(new OutputStreamWriter(saida))) {

            //Cabeçalho
            escritor.writeNext(new String[]{
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            });

            //Linhas
            for (AbastecimentoItemDTO item : relatorio.getAbastecimentos()) {
                escritor.writeNext(new String[]{
                        item.getDataHora()           != null ? item.getDataHora().toString()           : "",
                        item.getVeiculo()             != null ? item.getVeiculo()                      : "",
                        item.getResponsavel()         != null ? item.getResponsavel()                  : "",
                        item.getTipoCombustivel()     != null ? item.getTipoCombustivel()              : "",
                        item.getQuantidadeLitros()    != null ? item.getQuantidadeLitros().toString()  : "",
                        item.getValorTotal()          != null ? item.getValorTotal().toString()        : "",
                        item.getKmAbastecimento()     != null ? item.getKmAbastecimento().toString()   : "",
                        item.getPostoNome()           != null ? item.getPostoNome()                    : "",
                        item.getPostoCidade()         != null ? item.getPostoCidade()                  : "",
                        item.getNotaFiscal()          != null ? item.getNotaFiscal()                   : "—"
                });
            }

            escritor.flush();
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV.", e);
        }
    }

    private byte[] exportarExcel(RelatorioAbastecimentoDTO relatorio) {
        try (Workbook planilha = new XSSFWorkbook();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            Sheet aba = planilha.createSheet("Abastecimentos");

            //Cabeçalho
            Row cabecalho = aba.createRow(0);
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };
            for (int i = 0; i < colunas.length; i++) {
                cabecalho.createCell(i).setCellValue(colunas[i]);
            }

            //Linhas
            List<AbastecimentoItemDTO> abastecimentos = relatorio.getAbastecimentos();
            for (int i = 0; i < abastecimentos.size(); i++) {
                AbastecimentoItemDTO item = abastecimentos.get(i);
                Row linha = aba.createRow(i + 1);
                linha.createCell(0).setCellValue(item.getDataHora()        != null ? item.getDataHora().toString()           : "");
                linha.createCell(1).setCellValue(item.getVeiculo()          != null ? item.getVeiculo()                      : "");
                linha.createCell(2).setCellValue(item.getResponsavel()      != null ? item.getResponsavel()                  : "");
                linha.createCell(3).setCellValue(item.getTipoCombustivel()  != null ? item.getTipoCombustivel()              : "");
                linha.createCell(4).setCellValue(item.getQuantidadeLitros() != null ? item.getQuantidadeLitros().doubleValue(): 0);
                linha.createCell(5).setCellValue(item.getValorTotal()       != null ? item.getValorTotal().doubleValue()     : 0);
                linha.createCell(6).setCellValue(item.getKmAbastecimento()  != null ? item.getKmAbastecimento().doubleValue(): 0);
                linha.createCell(7).setCellValue(item.getPostoNome()        != null ? item.getPostoNome()                    : "");
                linha.createCell(8).setCellValue(item.getPostoCidade()      != null ? item.getPostoCidade()                  : "");
                linha.createCell(9).setCellValue(item.getNotaFiscal()       != null ? item.getNotaFiscal()                   : "—");
            }

            planilha.write(saida);
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel.", e);
        }
    }

    private byte[] exportarPdf(RelatorioAbastecimentoDTO relatorio) {
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            PdfWriter escritor = new PdfWriter(saida);
            PdfDocument pdf = new PdfDocument(escritor);
            Document documento = new Document(pdf);

            //Título
            documento.add(new Paragraph("Relatório de Abastecimentos")
                    .setBold()
                    .setFontSize(16));

            //Tabela com 10 colunas
            Table tabela = new Table(10);
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };

            //Cabeçalho
            for (String coluna : colunas) {
                tabela.addHeaderCell(new Cell().add(new Paragraph(coluna).setBold()));
            }

            //Linhas
            for (AbastecimentoItemDTO item : relatorio.getAbastecimentos()) {
                tabela.addCell(item.getDataHora()        != null ? item.getDataHora().toString()           : "");
                tabela.addCell(item.getVeiculo()          != null ? item.getVeiculo()                      : "");
                tabela.addCell(item.getResponsavel()      != null ? item.getResponsavel()                  : "");
                tabela.addCell(item.getTipoCombustivel()  != null ? item.getTipoCombustivel()              : "");
                tabela.addCell(item.getQuantidadeLitros() != null ? item.getQuantidadeLitros().toString()  : "");
                tabela.addCell(item.getValorTotal()       != null ? item.getValorTotal().toString()        : "");
                tabela.addCell(item.getKmAbastecimento()  != null ? item.getKmAbastecimento().toString()   : "");
                tabela.addCell(item.getPostoNome()        != null ? item.getPostoNome()                    : "");
                tabela.addCell(item.getPostoCidade()      != null ? item.getPostoCidade()                  : "");
                tabela.addCell(item.getNotaFiscal()       != null ? item.getNotaFiscal()                   : "—");
            }

            documento.add(tabela);
            documento.close();

            return saida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF.", e);
        }
    }

    private byte[] exportarDocx(RelatorioAbastecimentoDTO relatorio) {
        try (XWPFDocument documento = new XWPFDocument();
             ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            //Título
            XWPFParagraph titulo = documento.createParagraph();
            XWPFRun textoTitulo = titulo.createRun();
            textoTitulo.setText("Relatório de Abastecimentos");
            textoTitulo.setBold(true);
            textoTitulo.setFontSize(16);

            //Tabela com 10 colunas
            XWPFTable tabela = documento.createTable();
            String[] colunas = {
                    "Data/Hora", "Veículo", "Responsável", "Combustível",
                    "Litros", "Valor Total", "KM", "Posto", "Cidade", "NF"
            };

            //Cabeçalho
            XWPFTableRow linhaCabecalho = tabela.getRow(0);
            linhaCabecalho.getCell(0).setText(colunas[0]);
            for (int i = 1; i < colunas.length; i++) {
                linhaCabecalho.addNewTableCell().setText(colunas[i]);
            }

            //Linhas
            for (AbastecimentoItemDTO item : relatorio.getAbastecimentos()) {
                XWPFTableRow linha = tabela.createRow();
                linha.getCell(0).setText(item.getDataHora()        != null ? item.getDataHora().toString()          : "");
                linha.getCell(1).setText(item.getVeiculo()          != null ? item.getVeiculo()                     : "");
                linha.getCell(2).setText(item.getResponsavel()      != null ? item.getResponsavel()                 : "");
                linha.getCell(3).setText(item.getTipoCombustivel()  != null ? item.getTipoCombustivel()             : "");
                linha.getCell(4).setText(item.getQuantidadeLitros() != null ? item.getQuantidadeLitros().toString() : "");
                linha.getCell(5).setText(item.getValorTotal()       != null ? item.getValorTotal().toString()       : "");
                linha.getCell(6).setText(item.getKmAbastecimento()  != null ? item.getKmAbastecimento().toString()  : "");
                linha.getCell(7).setText(item.getPostoNome()        != null ? item.getPostoNome()                   : "");
                linha.getCell(8).setText(item.getPostoCidade()      != null ? item.getPostoCidade()                 : "");
                linha.getCell(9).setText(item.getNotaFiscal()       != null ? item.getNotaFiscal()                  : "—");
            }

            documento.write(saida);
            return saida.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar DOCX.", e);
        }
    }
}