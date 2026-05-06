package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VeiculoResumoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static final DecimalFormat FMT_KM;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        FMT_KM = new DecimalFormat("#,###", symbols);
    }

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    /**
     * Lista veículos com resumo.
     * CORRIGIDO: parâmetro "todos" — quando false filtra apenas veículos ativos (ativo=true).
     * ADM usa ?todos=true para ver todos incluindo inativos.
     */
    public List<VeiculoResumoDTO> listarVeiculosResumo(boolean todos) {

        List<Veiculo> veiculos = veiculoRepository.findAll().stream()
                // CORRIGIDO: técnico não vê veículos inativos
                .filter(v -> todos || Boolean.TRUE.equals(v.getAtivo()))
                .collect(Collectors.toList());

        return veiculos.stream().map(veiculo -> {

            Optional<RegistroSaida> ultimoRegistro =
                    registroSaidaRepository
                            .findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(veiculo.getIdVeiculo());

            boolean emUso = ultimoRegistro
                    .map(r -> "em_andamento".equalsIgnoreCase(r.getStatus()))
                    .orElse(false);

            String ultimoUso = ultimoRegistro
                    .map(r -> formatarData(r.getDataHoraSaida()))
                    .orElse("—");

            String ultimoMotorista = ultimoRegistro
                    .map(r -> r.getUsuario() != null ? r.getUsuario().getNome() : "—")
                    .orElse("—");

            Optional<Fueling> ultimoAbastecimento =
                    abastecimentoRepository
                            .findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(veiculo.getIdVeiculo());

            String ultimoAbastecimentoStr = ultimoAbastecimento
                    .map(a -> formatarData(a.getDateTime()))
                    .orElse("—");

            String km = veiculo.getKmAtual() != null
                    ? FMT_KM.format(veiculo.getKmAtual().longValue())
                    : "—";

            String status = emUso ? "em_uso" : "disponivel";

            // CORRIGIDO: VeiculoResumoDTO agora inclui habilitacaoCategoria e ativo
            VeiculoResumoDTO dto = new VeiculoResumoDTO(
                    veiculo.getIdVeiculo(),
                    veiculo.getModelo(),
                    veiculo.getPrefixo(),
                    ultimoUso,
                    ultimoMotorista,
                    ultimoAbastecimentoStr,
                    km,
                    status
            );
            dto.setHabilitacaoCategoria(veiculo.getHabilitacaoCategoria());
            dto.setAtivo(veiculo.getAtivo());
            return dto;

        }).collect(Collectors.toList());
    }

    /**
     * Ativa ou desativa um veículo.
     * NOVO: endpoint /veiculos/{id}/ativar e /veiculos/{id}/desativar
     */
    public Veiculo toggleAtivo(Integer id, boolean ativo) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));
        veiculo.setAtivo(ativo);
        return veiculoRepository.save(veiculo);
    }

    /**
     * Atualiza dados de um veículo (PUT completo).
     * NOVO: endpoint /veiculos/{id} PUT
     */
    public Veiculo atualizar(Integer id, Veiculo atualizado) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        veiculo.setPrefixo(atualizado.getPrefixo());
        veiculo.setPlaca(atualizado.getPlaca());
        veiculo.setMarca(atualizado.getMarca());
        veiculo.setModelo(atualizado.getModelo());
        veiculo.setAno(atualizado.getAno());
        veiculo.setKmAtual(atualizado.getKmAtual());
        veiculo.setTipoCombustivel(atualizado.getTipoCombustivel());
        veiculo.setHabilitacaoCategoria(atualizado.getHabilitacaoCategoria());
        veiculo.setNucleoDar(atualizado.getNucleoDar());

        if (atualizado.getIntervaloTrocaOleoKm() != null)
            veiculo.setIntervaloTrocaOleoKm(atualizado.getIntervaloTrocaOleoKm());
        if (atualizado.getNumeroFl() != null)
            veiculo.setNumeroFl(atualizado.getNumeroFl());
        if (atualizado.getAtivo() != null)
            veiculo.setAtivo(atualizado.getAtivo());

        return veiculoRepository.save(veiculo);
    }

    private String formatarData(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(FMT_DATA);
    }
}