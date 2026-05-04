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

    public RegistroSaida abrirSaida(RegistroSaidaDTO dto) {
        if (dto.getIdVeiculo() == null) throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getMatriculaUsuario() == null) throw new RegraDeNegocioException("Informe o usuário.");
        if (dto.getIdTipoServico() == null) throw new RegraDeNegocioException("Informe o tipo de serviço.");
        if (dto.getKmInicial() == null) throw new RegraDeNegocioException("Informe o KM inicial.");
        if (dto.getKmInicial().compareTo(BigDecimal.ZERO) < 0) throw new RegraDeNegocioException("KM inicial não pode ser negativo.");
        if (dto.getDataHoraSaida() == null) throw new RegraDeNegocioException("Informe a data e hora de saída.");
        if (dto.getLocalDestino() == null || dto.getLocalDestino().isBlank()) throw new RegraDeNegocioException("Informe o local de destino.");

        boolean usuarioJaEmSaida = registroSaidaRepository
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(dto.getMatriculaUsuario(), "em_andamento")
                .isPresent();
        if (usuarioJaEmSaida)
            throw new RegraDeNegocioException("Você já possui uma saída em andamento. Registre o retorno antes de iniciar uma nova saída.");

        Veiculo veiculo = veiculoRepository.findById(dto.getIdVeiculo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(veiculo.getDisponivel()))
            throw new RegraDeNegocioException("Veículo não está disponível.");

        if (veiculo.getKmAtual() != null && dto.getKmInicial().compareTo(veiculo.getKmAtual()) < 0) {
            throw new RegraDeNegocioException("KM inicial (" + dto.getKmInicial() + ") não pode ser menor que o KM atual do veículo (" + veiculo.getKmAtual() + ").");
        }

        Usuario usuario = usuarioRepository.findByMatricula(dto.getMatriculaUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (Boolean.FALSE.equals(usuario.getColaboradorAtivo()))
            throw new RegraDeNegocioException("Colaborador inativo.");

        TipoServico tipoServico = tipoServicoRepository.findById(dto.getIdTipoServico())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        RegistroSaida registro = new RegistroSaida();
        registro.setVeiculo(veiculo);
        registro.setUsuario(usuario);
        registro.setTipoServico(tipoServico);
        registro.setLocalDestino(dto.getLocalDestino());
        registro.setObservacoes(dto.getObservacoes());
        registro.setKmInicial(dto.getKmInicial());
        registro.setDataHoraSaida(dto.getDataHoraSaida());
        registro.setStatus("em_andamento");

        veiculo.setDisponivel(false);
        veiculoRepository.save(veiculo);

        return registroSaidaRepository.save(registro);
    }

    public RetornoRespostaDTO registrarRetorno(Integer id, RetornoDTO dto) {
        if (dto.getKmFinal() == null) throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getDataRetorno() == null) throw new RegraDeNegocioException("Informe o horário de chegada.");

        RegistroSaida registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");

        if (dto.getKmFinal().compareTo(registro.getKmInicial()) < 0)
            throw new RegraDeNegocioException("KM final não pode ser menor que o KM inicial.");

        if (dto.getDataRetorno().isBefore(registro.getDataHoraSaida()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getKmFinal().subtract(registro.getKmInicial());

        registro.setKmFinal(dto.getKmFinal());
        registro.setKmRodados(kmRodados);
        registro.setDataRetorno(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank())
            registro.setObservacoes(dto.getObservacoes());

        Veiculo veiculo = registro.getVeiculo();
        veiculo.setKmAtual(dto.getKmFinal());
        veiculo.setDisponivel(true);
        veiculoRepository.save(veiculo);

        registroSaidaRepository.save(registro);

        return new RetornoRespostaDTO(
                registro.getIdSaida(), registro.getStatus(), registro.getKmInicial(),
                registro.getKmFinal(), kmRodados, registro.getDataHoraSaida(),
                registro.getDataRetorno(), veiculo.getModelo(), veiculo.getPrefixo(),
                registro.getUsuario().getNome()
        );
    }

    public RegistroSaida fecharSaida(Integer id, FecharSaidaDTO dto) {
        RegistroSaida registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");
        if (dto.getKmFinal() == null)
            throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getDataRetorno() == null)
            throw new RegraDeNegocioException("Informe o horário de chegada.");
        if (dto.getKmFinal().compareTo(registro.getKmInicial()) < 0)
            throw new RegraDeNegocioException("KM final não pode ser menor que o KM inicial.");
        if (dto.getDataRetorno().isBefore(registro.getDataHoraSaida()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getKmFinal().subtract(registro.getKmInicial());

        registro.setKmFinal(dto.getKmFinal());
        registro.setKmRodados(kmRodados);
        registro.setDataRetorno(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank())
            registro.setObservacoes(dto.getObservacoes());

        Veiculo veiculo = registro.getVeiculo();
        veiculo.setKmAtual(dto.getKmFinal());
        veiculo.setDisponivel(true);
        veiculoRepository.save(veiculo);

        return registroSaidaRepository.save(registro);
    }

    public List<RegistroSaida> listarTodos() {
        return registroSaidaRepository.findAll();
    }

    public RegistroSaida buscarPorId(Integer id) {
        return registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));
    }

    public RelatorioUsoMensalDTO gerarRelatorioUsoMensalPorVeiculo(Long idVeiculo, LocalDateTime inicio, LocalDateTime fim) {
        // CERTIFICAÇÃO: Alterado para buscar por DataHoraSaida para evitar relatórios zerados
        List<RegistroSaida> viagens = registroSaidaRepository.findByVeiculoIdVeiculoAndDataHoraSaidaBetween(idVeiculo.intValue(), inicio, fim);

        BigDecimal totalKm = viagens.stream()
                .map(v -> v.getKmRodados() != null ? v.getKmRodados() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGasto = BigDecimal.ZERO;
        BigDecimal totalLitros = BigDecimal.ZERO;

        for (RegistroSaida viagem : viagens) {
            List<Abastecimento> abastecimentos = abastecimentoRepository.findByRegistroSaida(viagem);
            for (Abastecimento a : abastecimentos) {
                if (a.getValorTotal() != null) totalGasto = totalGasto.add(a.getValorTotal());
                if (a.getQuantidadeLitros() != null) totalLitros = totalLitros.add(a.getQuantidadeLitros());
            }
        }

        return new RelatorioUsoMensalDTO(totalKm, viagens.size(), totalGasto, totalLitros, viagens);
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

        RelatorioUsoMensalDTO dados = gerarRelatorioUsoMensalPorVeiculo(idVeiculo, inicio, fim);

        if ("pdf".equalsIgnoreCase(formato)) {
            return gerarPdfRelatorio(idVeiculo, periodoStr, inicio, fim, dados);
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATORIO DE USO - IPEM CONTROL\n");
        relatorio.append("Viatura ID: ").append(idVeiculo).append("\n");
        relatorio.append("Periodo: ").append(periodoStr).append(" (").append(inicio.toLocalDate()).append(" a ").append(fim.toLocalDate()).append(")\n");
        relatorio.append("--------------------------------------------------\n");
        relatorio.append("RESUMO GERAL:\n");
        relatorio.append("- Total de Saidas no periodo: ").append(dados.getTotalDeViagens()).append("\n");
        relatorio.append("- Quilometragem total rodada: ").append(dados.getQuilometragemTotal()).append(" KM\n");
        relatorio.append("- Consumo total de combustível: ").append(dados.getLitrosTotal()).append(" Litros\n");
        relatorio.append("- Gasto total com abastecimento: R$ ").append(dados.getGastoTotal()).append("\n");
        relatorio.append("--------------------------------------------------\n");
        relatorio.append("DETALHAMENTO DE VIAGENS:\n");

        if (dados.getDetalhes() != null && !dados.getDetalhes().isEmpty()) {
            for (RegistroSaida v : dados.getDetalhes()) {
                relatorio.append("Data: ").append(v.getDataHoraSaida().toLocalDate())
                        .append(" | Usuario: ").append(v.getUsuario() != null ? v.getUsuario().getNome() : "N/I")
                        .append(" | Destino: ").append(v.getLocalDestino() != null ? v.getLocalDestino() : "N/I")
                        .append(" | KM Rodados: ").append(v.getKmRodados() != null ? v.getKmRodados() : "0").append("\n");
            }
        } else {
            relatorio.append("Nenhuma viagem registrada para este veiculo no periodo.\n");
        }

        relatorio.append("--------------------------------------------------\n");
        relatorio.append("Gerado em: ").append(LocalDateTime.now()).append("\n");

        return relatorio.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] gerarPdfRelatorio(Long idVeiculo, String periodoStr, LocalDateTime inicio, LocalDateTime fim, RelatorioUsoMensalDTO dados) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("RELATORIO DE USO - IPEM CONTROL").setBold().setFontSize(16));
            document.add(new Paragraph("Viatura ID: " + idVeiculo));
            document.add(new Paragraph("Periodo: " + periodoStr + " (" + inicio.toLocalDate() + " a " + fim.toLocalDate() + ")"));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("RESUMO GERAL:"));
            document.add(new Paragraph("- Total de Saidas no periodo: " + dados.getTotalDeViagens()));
            document.add(new Paragraph("- Quilometragem total rodada: " + dados.getQuilometragemTotal() + " KM"));
            document.add(new Paragraph("- Consumo total de combustível: " + dados.getLitrosTotal() + " Litros"));
            document.add(new Paragraph("- Gasto total com abastecimento: R$ " + dados.getGastoTotal()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("DETALHAMENTO DE VIAGENS:"));

            if (dados.getDetalhes() != null && !dados.getDetalhes().isEmpty()) {
                for (RegistroSaida v : dados.getDetalhes()) {
                    String user = v.getUsuario() != null ? v.getUsuario().getNome() : "N/I";
                    String dest = v.getLocalDestino() != null ? v.getLocalDestino() : "N/I";
                    String km = v.getKmRodados() != null ? v.getKmRodados().toString() : "0";

                    document.add(new Paragraph("Data: " + v.getDataHoraSaida().toLocalDate() +
                            " | Usuario: " + user +
                            " | Destino: " + dest +
                            " | KM Rodados: " + km));
                }
            } else {
                document.add(new Paragraph("Nenhuma viagem registrada para este veiculo no periodo."));
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