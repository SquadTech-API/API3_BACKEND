package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VehicleSummaryDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.RefuelingRepository;
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
    private RefuelingRepository refuelingRepository;

    /**
     * Lista veículos com summary.
     * CORRIGIDO: parâmetro "todos" — quando false filtra apenas veículos ativos (active=true).
     * ADM usa ?todos=true para ver todos incluindo inativos.
     */
    public List<VehicleSummaryDTO> listarVeiculosResumo(boolean todos) {

        List<Vehicle> vehicles = veiculoRepository.findAll().stream()
                // CORRIGIDO: técnico não vê veículos inativos
                .filter(v -> todos || Boolean.TRUE.equals(v.getActive()))
                .collect(Collectors.toList());

        return vehicles.stream().map(veiculo -> {

            Optional<DepartureLog> ultimoRegistro =
                    registroSaidaRepository
                            .findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(veiculo.getVehicleId());

            boolean emUso = ultimoRegistro
                    .map(r -> "em_andamento".equalsIgnoreCase(r.getStatus()))
                    .orElse(false);

            String ultimoUso = ultimoRegistro
                    .map(r -> formatarData(r.getDateTimeDeparture()))
                    .orElse("—");

            String ultimoMotorista = ultimoRegistro
                    .map(r -> r.getUser() != null ? r.getUser().getName() : "—")
                    .orElse("—");

            Optional<Fueling> ultimoAbastecimento =
                    refuelingRepository
                            .findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(veiculo.getVehicleId());

            String ultimoAbastecimentoStr = ultimoAbastecimento
                    .map(a -> formatarData(a.getDateTime()))
                    .orElse("—");

            String km = veiculo.getCurrentKm() != null
                    ? FMT_KM.format(veiculo.getCurrentKm().longValue())
                    : "—";

            String status = emUso ? "em_uso" : "disponivel";

            // CORRIGIDO: VehicleSummaryDTO agora inclui licenseCategory e active
            VehicleSummaryDTO dto = new VehicleSummaryDTO(
                    veiculo.getVehicleId(),
                    veiculo.getModel(),
                    veiculo.getPrefix(),
                    ultimoUso,
                    ultimoMotorista,
                    ultimoAbastecimentoStr,
                    km,
                    status
            );
            dto.setLicenseCategory(veiculo.getLicenseCategory());
            dto.setActive(veiculo.getActive());
            return dto;

        }).collect(Collectors.toList());
    }

    /**
     * Ativa ou desativa um veículo.
     * NOVO: endpoint /vehicles/{id}/ativar e /vehicles/{id}/desativar
     */
    public Vehicle toggleAtivo(Integer id, boolean ativo) {
        Vehicle vehicle = veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));
        vehicle.setActive(ativo);
        return veiculoRepository.save(vehicle);
    }

    /**
     * Atualiza dados de um veículo (PUT completo).
     * NOVO: endpoint /vehicles/{id} PUT
     */
    public Vehicle atualizar(Integer id, Vehicle atualizado) {
        Vehicle vehicle = veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        vehicle.setPrefix(atualizado.getPrefix());
        vehicle.setLicensePlate(atualizado.getLicensePlate());
        vehicle.setBrand(atualizado.getBrand());
        vehicle.setModel(atualizado.getModel());
        vehicle.setYear(atualizado.getYear());
        vehicle.setCurrentKm(atualizado.getCurrentKm());
        vehicle.setFuelType(atualizado.getFuelType());
        vehicle.setLicenseCategory(atualizado.getLicenseCategory());
        vehicle.setNucleoDar(atualizado.getNucleoDar());

        if (atualizado.getOilChangeIntervalKm() != null)
            vehicle.setOilChangeIntervalKm(atualizado.getOilChangeIntervalKm());
        if (atualizado.getFlNumber() != null)
            vehicle.setFlNumber(atualizado.getFlNumber());
        if (atualizado.getActive() != null)
            vehicle.setActive(atualizado.getActive());

        return veiculoRepository.save(vehicle);
    }

    private String formatarData(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(FMT_DATA);
    }
}