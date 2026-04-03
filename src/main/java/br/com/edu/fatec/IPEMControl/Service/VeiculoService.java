package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VeiculoResumoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
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

    public List<VeiculoResumoDTO> listarVeiculosResumido() {

        return veiculoRepository.findAll().stream().map(veiculo -> {

            Optional<RegistroSaida> ultimoRegistro =
                    registroSaidaRepository
                            .findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(veiculo.getIdVeiculo());

            boolean emUso = ultimoRegistro
                    .map(r -> "em_andamento".equalsIgnoreCase(r.getStatus()))
                    .orElse(false);

            String ultimoUso = ultimoRegistro
                    .map(r -> formatarData(r.getDataHoraSaida()))
                    .orElse("—");

            Optional<Abastecimento> ultimoAbastecimento =
                    abastecimentoRepository.findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(veiculo.getIdVeiculo());

            String ultimoAbastecimentoStr = ultimoAbastecimento
                    .map(a -> formatarData(a.getDataHora()))
                    .orElse("—");

            String km = veiculo.getKmAtual() != null
                    ? FMT_KM.format(veiculo.getKmAtual().longValue())
                    : "—";

            return new VeiculoResumoDTO(
                    veiculo.getIdVeiculo(),
                    veiculo.getModelo(),
                    veiculo.getPrefixo(),
                    ultimoUso,
                    ultimoAbastecimentoStr,
                    km,
                    emUso ? "em_uso" : "disponivel"
            );

        }).toList();
    }

    private String formatarData(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(FMT_DATA);
    }
}