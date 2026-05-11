package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.OilChangeDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.OilChange;
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
    public OilChange salvar(OilChangeDTO dto) {

        if (dto.getVehicleId() == null)
            throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getOilChangeMileage() == null)
            throw new RegraDeNegocioException("Informe o KM da troca.");
        if (dto.getOilChangeDate() == null)
            throw new RegraDeNegocioException("Informe a data da troca.");

        Vehicle vehicle = veiculoRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        OilChange troca = new OilChange();
        troca.setVehicle(vehicle);
        troca.setChangeKm(dto.getOilChangeMileage());
        troca.setChangeDate(dto.getOilChangeDate());
        troca.setObservation(dto.getObservations());

        // Intervalo: usa o informado ou o padrão do veículo
        BigDecimal intervalo = dto.getIntervalKm() != null
                ? dto.getIntervalKm()
                : (vehicle.getOilChangeIntervalKm() != null
                ? vehicle.getOilChangeIntervalKm()
                : new BigDecimal("5000"));
        troca.setIntervalKm(intervalo);

        // Próxima troca: usa o informado ou calcula
        BigDecimal proxima = dto.getNextOilChangeMileage() != null
                ? dto.getNextOilChangeMileage()
                : dto.getOilChangeMileage().add(intervalo);
        troca.setNextChangeKm(proxima);

        // Vínculo com saída (opcional)
        if (dto.getDepartureId() != null) {
            DepartureLog saida = registroSaidaRepository.findById(dto.getDepartureId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Saída não encontrada."));
            troca.setDepartureLog(saida);
        }

        // Atualiza intervalo padrão no veículo para futuros alertas
        vehicle.setOilChangeIntervalKm(intervalo);
        vehicle.setOilChangeAlertSent(false);
        veiculoRepository.save(vehicle);

        return trocaOleoRepository.save(troca);
    }

    // ── GET /troca-oleo?veiculoId={id} ────────────────────────────────────────
    public List<OilChange> listarPorVeiculo(Integer idVeiculo) {
        if (idVeiculo == null)
            return trocaOleoRepository.findAll();
        return trocaOleoRepository.findByVeiculoIdVeiculoOrderByCreatedAtDesc(idVeiculo);
    }

    // ── GET /troca-oleo/{id} ──────────────────────────────────────────────────
    public OilChange buscarPorId(Integer id) {
        return trocaOleoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Troca de óleo não encontrada."));
    }

    // ── PUT /troca-oleo/{id} ──────────────────────────────────────────────────
    public OilChange atualizar(Integer id, OilChangeDTO dto) {
        OilChange troca = trocaOleoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Troca de óleo não encontrada."));

        if (dto.getOilChangeMileage() != null)        troca.setChangeKm(dto.getOilChangeMileage());
        if (dto.getIntervalKm() != null)    troca.setIntervalKm(dto.getIntervalKm());
        if (dto.getNextOilChangeMileage() != null) troca.setNextChangeKm(dto.getNextOilChangeMileage());
        if (dto.getOilChangeDate() != null)      troca.setChangeDate(dto.getOilChangeDate());
        if (dto.getObservations() != null)    troca.setObservation(dto.getObservations());

        return trocaOleoRepository.save(troca);
    }
}