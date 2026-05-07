package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.TrocaOleoDTO;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.TrocaOleo;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Exception.RegraDeNegocioException;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.TrocaOleoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * NOVO: Service para gerenciamento de trocas de óleo.
 * Antes não existia — o frontend chamava endpoints que retornavam 404.
 */
@Service
public class TrocaOleoService {

    @Autowired
    private TrocaOleoRepository trocaOleoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    // ── POST /troca-oleo ──────────────────────────────────────────────────────
    public TrocaOleo salvar(TrocaOleoDTO dto) {

        if (dto.getIdVeiculo() == null)
            throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getKmTroca() == null)
            throw new RegraDeNegocioException("Informe o KM da troca.");
        if (dto.getDataTroca() == null)
            throw new RegraDeNegocioException("Informe a data da troca.");

        Vehicle vehicle = veiculoRepository.findById(dto.getIdVeiculo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        TrocaOleo troca = new TrocaOleo();
        troca.setVehicle(vehicle);
        troca.setKmTroca(dto.getKmTroca());
        troca.setDataTroca(dto.getDataTroca());
        troca.setObservacoes(dto.getObservacoes());

        // Intervalo: usa o informado ou o padrão do veículo
        BigDecimal intervalo = dto.getIntervaloKm() != null
                ? dto.getIntervaloKm()
                : (vehicle.getOilChangeIntervalKm() != null
                ? vehicle.getOilChangeIntervalKm()
                : new BigDecimal("5000"));
        troca.setIntervaloKm(intervalo);

        // Próxima troca: usa o informado ou calcula
        BigDecimal proxima = dto.getKmProximaTroca() != null
                ? dto.getKmProximaTroca()
                : dto.getKmTroca().add(intervalo);
        troca.setKmProximaTroca(proxima);

        // Vínculo com saída (opcional)
        if (dto.getIdSaida() != null) {
            RegistroSaida saida = registroSaidaRepository.findById(dto.getIdSaida())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Saída não encontrada."));
            troca.setRegistroSaida(saida);
        }

        // Atualiza intervalo padrão no veículo para futuros alertas
        vehicle.setOilChangeIntervalKm(intervalo);
        vehicle.setOilChangeAlertSent(false);
        veiculoRepository.save(vehicle);

        return trocaOleoRepository.save(troca);
    }

    // ── GET /troca-oleo?veiculoId={id} ────────────────────────────────────────
    public List<TrocaOleo> listarPorVeiculo(Integer idVeiculo) {
        if (idVeiculo == null)
            return trocaOleoRepository.findAll();
        return trocaOleoRepository.findByVeiculoIdVeiculoOrderByCreatedAtDesc(idVeiculo);
    }

    // ── GET /troca-oleo/{id} ──────────────────────────────────────────────────
    public TrocaOleo buscarPorId(Integer id) {
        return trocaOleoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Troca de óleo não encontrada."));
    }

    // ── PUT /troca-oleo/{id} ──────────────────────────────────────────────────
    public TrocaOleo atualizar(Integer id, TrocaOleoDTO dto) {
        TrocaOleo troca = trocaOleoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Troca de óleo não encontrada."));

        if (dto.getKmTroca() != null)        troca.setKmTroca(dto.getKmTroca());
        if (dto.getIntervaloKm() != null)    troca.setIntervaloKm(dto.getIntervaloKm());
        if (dto.getKmProximaTroca() != null) troca.setKmProximaTroca(dto.getKmProximaTroca());
        if (dto.getDataTroca() != null)      troca.setDataTroca(dto.getDataTroca());
        if (dto.getObservacoes() != null)    troca.setObservacoes(dto.getObservacoes());

        return trocaOleoRepository.save(troca);
    }
}