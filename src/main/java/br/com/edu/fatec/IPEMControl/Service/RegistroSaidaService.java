package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.*;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Exception.RegraDeNegocioException;
import br.com.edu.fatec.IPEMControl.Repository.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistroSaidaService {

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    public DepartureLog abrirSaida(DepartureLogDTO dto) {
        if (dto.getVehicleId() == null) throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getUserRegistration() == null) throw new RegraDeNegocioException("Informe o usuário.");
        if (dto.getServiceTypeId() == null) throw new RegraDeNegocioException("Informe o type de serviço.");
        if (dto.getInitialMileage() == null) throw new RegraDeNegocioException("Informe o KM inicial.");
        if (dto.getInitialMileage().compareTo(BigDecimal.ZERO) < 0) throw new RegraDeNegocioException("KM inicial não pode ser negativo.");
        if (dto.getDepartureDatetime() == null) throw new RegraDeNegocioException("Informe a data e hora de saída.");
        if (dto.getDestination() == null || dto.getDestination().isBlank()) throw new RegraDeNegocioException("Informe o local de destino.");

        boolean usuarioJaEmSaida = registroSaidaRepository
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(dto.getUserRegistration(), "em_andamento")
                .isPresent();
        if (usuarioJaEmSaida)
            throw new RegraDeNegocioException("Você já possui uma saída em andamento. Registre o retorno antes de iniciar uma nova saída.");

        Vehicle vehicle = veiculoRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(vehicle.getAvailable()))
            throw new RegraDeNegocioException("Veículo não está disponível.");

        if (vehicle.getCurrentKm() != null && dto.getInitialMileage().compareTo(vehicle.getCurrentKm()) < 0) {
            throw new RegraDeNegocioException("KM inicial (" + dto.getInitialMileage() + ") não pode ser menor que o KM atual do veículo (" + vehicle.getCurrentKm() + ").");
        }

        User user = usuarioRepository.findByMatricula(dto.getUserRegistration())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (Boolean.FALSE.equals(user.getActiveColaborator()))
            throw new RegraDeNegocioException("Colaborador inativo.");

        ServiceType serviceType = tipoServicoRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        DepartureLog registro = new DepartureLog();
        registro.setVehicle(vehicle);
        registro.setUser(user);
        registro.setServiceType(serviceType);
        registro.setDestination(dto.getDestination());
        registro.setObservacoes(dto.getObservations());
        registro.setStartingKm(dto.getInitialMileage());
        registro.setDateTimeDeparture(dto.getDepartureDatetime());
        registro.setStatus("em_andamento");

        vehicle.setAvailable(false);
        veiculoRepository.save(vehicle);

        return registroSaidaRepository.save(registro);
    }

    public ReturnResponseDTO registrarRetorno(Integer id, ReturnDTO dto) {
        if (dto.getFinalMileage() == null) throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getReturnDatetime() == null) throw new RegraDeNegocioException("Informe o horário de chegada.");

        DepartureLog registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");

        if (dto.getFinalMileage().compareTo(registro.getStartingKm()) < 0)
            throw new RegraDeNegocioException("KM final não pode ser menor que o KM inicial.");

        if (dto.getReturnDatetime().isBefore(registro.getDateTimeDeparture()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getFinalMileage().subtract(registro.getStartingKm());

        registro.setFinishingKm(dto.getFinalMileage());
        registro.setDrivenKm(kmRodados);
        registro.setReturnDate(dto.getReturnDatetime());
        registro.setStatus("concluido");

        if (dto.getObservations() != null && !dto.getObservations().isBlank())
            registro.setObservacoes(dto.getObservations());

        Vehicle vehicle = registro.getVehicle();
        vehicle.setCurrentKm(dto.getFinalMileage());
        vehicle.setAvailable(true);
        veiculoRepository.save(vehicle);

        registroSaidaRepository.save(registro);

        return new ReturnResponseDTO(
                registro.getDepartureLogId(), registro.getStatus(), registro.getStartingKm(),
                registro.getFinishingKm(), kmRodados, registro.getDateTimeDeparture(),
                registro.getReturnDate(), vehicle.getModel(), vehicle.getPrefix(),
                registro.getUser().getName()
        );
    }

    public DepartureLog fecharSaida(Integer id, FecharSaidaDTO dto) {
        DepartureLog registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");
        if (dto.getKmFinal() == null)
            throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getDataRetorno() == null)
            throw new RegraDeNegocioException("Informe o horário de chegada.");
        if (dto.getKmFinal().compareTo(registro.getStartingKm()) < 0)
            throw new RegraDeNegocioException("KM final não pode ser menor que o KM inicial.");
        if (dto.getDataRetorno().isBefore(registro.getDateTimeDeparture()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getKmFinal().subtract(registro.getStartingKm());

        registro.setFinishingKm(dto.getKmFinal());
        registro.setDrivenKm(kmRodados);
        registro.setReturnDate(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank())
            registro.setObservacoes(dto.getObservacoes());

        Vehicle vehicle = registro.getVehicle();
        vehicle.setCurrentKm(dto.getKmFinal());
        vehicle.setAvailable(true);
        veiculoRepository.save(vehicle);

        return registroSaidaRepository.save(registro);
    }

    public List<DepartureLog> listarTodos() {
        return registroSaidaRepository.findAll();
    }

    public DepartureLog buscarPorId(Integer id) {
        return registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));
    }

    public MonthlyUsageReportDTO gerarRelatorioUsoMensalPorVeiculo(Long idVeiculo, LocalDateTime inicio, LocalDateTime fim) {
        // CERTIFICAÇÃO: Alterado para buscar por DataHoraSaida para evitar relatórios zerados
        List<DepartureLog> viagens = registroSaidaRepository.findByVeiculoIdVeiculoAndDataHoraSaidaBetween(idVeiculo.intValue(), inicio, fim);

        BigDecimal totalKm = viagens.stream()
                .map(v -> v.getDrivenKm() != null ? v.getDrivenKm() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGasto = BigDecimal.ZERO;
        BigDecimal totalLitros = BigDecimal.ZERO;

        for (DepartureLog viagem : viagens) {
            List<Fueling> fuelings = abastecimentoRepository.findByRegistroSaida(viagem);
            for (Fueling a : fuelings) {
                if (a.getTotalValue() != null) totalGasto = totalGasto.add(a.getTotalValue());
                if (a.getLitersAmount() != null) totalLitros = totalLitros.add(a.getLitersAmount());
            }
        }

        return new MonthlyUsageReportDTO(totalKm, viagens.size(), totalGasto, totalLitros, viagens);
    }

    public byte[] gerarArquivoRelatorio(Long idVeiculo, String formato, String periodoStr) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = switch (periodoStr.toLowerCase()) {
            case "hoje" -> fim.withHour(0).withMinute(0).withSecond(0);
            case "7d" -> fim.minusDays(7);
            case "30d" -> fim.minusDays(30);
            case "1y" -> fim.minusYears(1);
            default -> fim.minusDays(30);
        };

        MonthlyUsageReportDTO dados = gerarRelatorioUsoMensalPorVeiculo(idVeiculo, inicio, fim);

        if ("pdf".equalsIgnoreCase(formato)) {
            return gerarPdfRelatorio(idVeiculo, periodoStr, inicio, fim, dados);
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATORIO DE USO - IPEM CONTROL\n");
        relatorio.append("Viatura ID: ").append(idVeiculo).append("\n");
        relatorio.append("Periodo: ").append(periodoStr).append(" (").append(inicio.toLocalDate()).append(" a ").append(fim.toLocalDate()).append(")\n");
        relatorio.append("--------------------------------------------------\n");
        relatorio.append("RESUMO GERAL:\n");
        relatorio.append("- Total de Saidas no periodo: ").append(dados.getTotalTrips()).append("\n");
        relatorio.append("- Quilometragem total rodada: ").append(dados.getTotalMileage()).append(" KM\n");
        relatorio.append("- Consumo total de combustível: ").append(dados.getTotalLiters()).append(" Litros\n");
        relatorio.append("- Gasto total com abastecimento: R$ ").append(dados.getTotalSpending()).append("\n");
        relatorio.append("--------------------------------------------------\n");
        relatorio.append("DETALHAMENTO DE VIAGENS:\n");

        if (dados.getDetails() != null && !dados.getDetails().isEmpty()) {
            for (DepartureLog v : dados.getDetails()) {
                relatorio.append("Data: ").append(v.getDateTimeDeparture().toLocalDate())
                        .append(" | User: ").append(v.getUser() != null ? v.getUser().getName() : "N/I")
                        .append(" | Destino: ").append(v.getDestination() != null ? v.getDestination() : "N/I")
                        .append(" | KM Rodados: ").append(v.getDrivenKm() != null ? v.getDrivenKm() : "0").append("\n");
            }
        } else {
            relatorio.append("Nenhuma viagem registrada para este vehicle no periodo.\n");
        }

        relatorio.append("--------------------------------------------------\n");
        relatorio.append("Gerado em: ").append(LocalDateTime.now()).append("\n");

        return relatorio.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] gerarPdfRelatorio(Long idVeiculo, String periodoStr, LocalDateTime inicio, LocalDateTime fim, MonthlyUsageReportDTO dados) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("RELATORIO DE USO - IPEM CONTROL").setBold().setFontSize(16));
            document.add(new Paragraph("Viatura ID: " + idVeiculo));
            document.add(new Paragraph("Periodo: " + periodoStr + " (" + inicio.toLocalDate() + " a " + fim.toLocalDate() + ")"));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("RESUMO GERAL:"));
            document.add(new Paragraph("- Total de Saidas no periodo: " + dados.getTotalTrips()));
            document.add(new Paragraph("- Quilometragem total rodada: " + dados.getTotalMileage() + " KM"));
            document.add(new Paragraph("- Consumo total de combustível: " + dados.getTotalLiters() + " Litros"));
            document.add(new Paragraph("- Gasto total com abastecimento: R$ " + dados.getTotalSpending()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("DETALHAMENTO DE VIAGENS:"));

            if (dados.getDetails() != null && !dados.getDetails().isEmpty()) {
                for (DepartureLog v : dados.getDetails()) {
                    String user = v.getUser() != null ? v.getUser().getName() : "N/I";
                    String dest = v.getDestination() != null ? v.getDestination() : "N/I";
                    String km = v.getDrivenKm() != null ? v.getDrivenKm().toString() : "0";

                    document.add(new Paragraph("Data: " + v.getDateTimeDeparture().toLocalDate() +
                            " | User: " + user +
                            " | Destino: " + dest +
                            " | KM Rodados: " + km));
                }
            } else {
                document.add(new Paragraph("Nenhuma viagem registrada para este vehicle no periodo."));
            }

            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Gerado em: " + LocalDateTime.now()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}