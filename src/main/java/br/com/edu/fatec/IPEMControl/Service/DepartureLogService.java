package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.*;
import br.com.edu.fatec.IPEMControl.Exception.ResourceNotFoundException;
import br.com.edu.fatec.IPEMControl.Exception.BusinessRuleException;
import br.com.edu.fatec.IPEMControl.Repository.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartureLogService {

    private final ExitRecordRepository exitRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final RefuelingRepository refuelingRepository;

    public DepartureLogService(ExitRecordRepository exitRecordRepository,
                               VehicleRepository vehicleRepository,
                               UserRepository userRepository,
                               ServiceTypeRepository serviceTypeRepository,
                               RefuelingRepository refuelingRepository) {
        this.exitRecordRepository = exitRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public DepartureLogResponseDTO openDeparture(DepartureLogDTO dto) {
        if (dto.getVehicleId() == null) throw new BusinessRuleException("Informe o veículo.");
        if (dto.getUserRegistration() == null) throw new BusinessRuleException("Informe o usuário.");
        if (dto.getServiceTypeId() == null) throw new BusinessRuleException("Informe o type de serviço.");
        if (dto.getInitialMileage() == null) throw new BusinessRuleException("Informe o KM inicial.");
        if (dto.getInitialMileage().compareTo(BigDecimal.ZERO) < 0) throw new BusinessRuleException("KM inicial não pode ser negativo.");
        if (dto.getDepartureDatetime() == null) throw new BusinessRuleException("Informe a data e hora de saída.");
        if (dto.getDestination() == null || dto.getDestination().isBlank()) throw new BusinessRuleException("Informe o local de destino.");

        boolean usuarioJaEmSaida = exitRecordRepository
                .findTopByUserRegistrationAndStatusOrderByDateTimeDepartureDesc(dto.getUserRegistration(), "em_andamento")
                .isPresent();
        if (usuarioJaEmSaida)
            throw new BusinessRuleException("Você já possui uma saída em andamento. Registre o retorno antes de iniciar uma nova saída.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(vehicle.getAvailable()))
            throw new BusinessRuleException("Veículo não está disponível.");

        if (vehicle.getCurrentKm() != null && dto.getInitialMileage().compareTo(vehicle.getCurrentKm()) < 0) {
            throw new BusinessRuleException("KM inicial (" + dto.getInitialMileage() + ") não pode ser menor que o KM atual do veículo (" + vehicle.getCurrentKm() + ").");
        }

        User user = userRepository.findByRegistration(dto.getUserRegistration())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (Boolean.FALSE.equals(user.getActiveColaborator()))
            throw new BusinessRuleException("Colaborador inativo.");

        ServiceType serviceType = serviceTypeRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));

        DepartureLog departureLog = new DepartureLog();
        departureLog.setVehicle(vehicle);
        departureLog.setUser(user);
        departureLog.setServiceType(serviceType);
        departureLog.setDestination(dto.getDestination());
        departureLog.setObservations(dto.getObservations());
        departureLog.setStartingKm(dto.getInitialMileage());
        departureLog.setDateTimeDeparture(dto.getDepartureDatetime());
        departureLog.setStatus("em_andamento");

        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);

        return toDTO(exitRecordRepository.save(departureLog));
    }

    public ReturnResponseDTO registerReturn(Integer id, ReturnDTO dto) {
        if (dto.getFinalMileage() == null) throw new BusinessRuleException("Informe o KM final.");
        if (dto.getReturnDatetime() == null) throw new BusinessRuleException("Informe o horário de chegada.");

        DepartureLog departureLog = exitRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(departureLog.getStatus()))
            throw new BusinessRuleException("Esta saída já foi encerrada.");

        if (dto.getFinalMileage().compareTo(departureLog.getStartingKm()) < 0)
            throw new BusinessRuleException("KM final não pode ser menor que o KM inicial.");

        if (dto.getReturnDatetime().isBefore(departureLog.getDateTimeDeparture()))
            throw new BusinessRuleException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal drivenKm = dto.getFinalMileage().subtract(departureLog.getStartingKm());

        departureLog.setFinishingKm(dto.getFinalMileage());
        departureLog.setDrivenKm(drivenKm);
        departureLog.setReturnDate(dto.getReturnDatetime());
        departureLog.setStatus("concluido");

        if (dto.getObservations() != null && !dto.getObservations().isBlank())
            departureLog.setObservations(dto.getObservations());

        Vehicle vehicle = departureLog.getVehicle();
        vehicle.setCurrentKm(dto.getFinalMileage());
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);

        exitRecordRepository.save(departureLog);

        return new ReturnResponseDTO(
                departureLog.getDepartureLogId(), departureLog.getStatus(), departureLog.getStartingKm(),
                departureLog.getFinishingKm(), drivenKm, departureLog.getDateTimeDeparture(),
                departureLog.getReturnDate(), vehicle.getModel(), vehicle.getPrefix(),
                departureLog.getUser().getName()
        );
    }

    public DepartureLogResponseDTO closeDeparture(Integer id, CloseExitDTO dto) {
        DepartureLog departureLog = exitRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(departureLog.getStatus()))
            throw new BusinessRuleException("Esta saída já foi encerrada.");
        if (dto.getFinalMileage() == null)
            throw new BusinessRuleException("Informe o KM final.");
        if (dto.getReturnDate() == null)
            throw new BusinessRuleException("Informe o horário de chegada.");
        if (dto.getFinalMileage().compareTo(departureLog.getStartingKm()) < 0)
            throw new BusinessRuleException("KM final não pode ser menor que o KM inicial.");
        if (dto.getReturnDate().isBefore(departureLog.getDateTimeDeparture()))
            throw new BusinessRuleException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal drivenKm = dto.getFinalMileage().subtract(departureLog.getStartingKm());

        departureLog.setFinishingKm(dto.getFinalMileage());
        departureLog.setDrivenKm(drivenKm);
        departureLog.setReturnDate(dto.getReturnDate());
        departureLog.setStatus("concluido");

        if (dto.getObservations() != null && !dto.getObservations().isBlank())
            departureLog.setObservations(dto.getObservations());

        Vehicle vehicle = departureLog.getVehicle();
        vehicle.setCurrentKm(dto.getFinalMileage());
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);

        return toDTO(exitRecordRepository.save(departureLog));
    }

    public List<DepartureLogResponseDTO> findAll() {
        return exitRecordRepository.findAll().stream().map(this::toDTO).toList();
    }

    public DepartureLogResponseDTO findById(Integer id) {
        return exitRecordRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Departure log not found."));
    }

    public DepartureLogResponseDTO findActiveByVehicle(Integer vehicleId) {
        return exitRecordRepository
                .findTopByVehicleVehicleIdAndStatusOrderByDateTimeDepartureDesc(vehicleId, "em_andamento")
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Active departure log not found."));
    }

    public DepartureLogResponseDTO findActiveByUser(Integer registration) {
        return exitRecordRepository
                .findTopByUserRegistrationAndStatusOrderByDateTimeDepartureDesc(registration, "em_andamento")
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Active departure log not found."));
    }

    public List<DepartureLogResponseDTO> findOilChangeDeparturesByVehicle(Integer vehicleId) {
        return exitRecordRepository.findByVehicleVehicleIdAndServiceTypeOilChangeSTTrue(vehicleId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public MonthlyUsageReportDTO generateMonthlyUsageReportByVehicle(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        // CERTIFICAÇÃO: Alterado para search por DataHoraSaida para evitar relatórios zerados
        List<DepartureLog> trips = exitRecordRepository.findByVehicleVehicleIdAndDateTimeDepartureBetween(vehicleId.intValue(), start, end);

        BigDecimal totalKm = trips.stream()
                .map(v -> v.getDrivenKm() != null ? v.getDrivenKm() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGasto = BigDecimal.ZERO;
        BigDecimal totalLitros = BigDecimal.ZERO;

        for (DepartureLog trip : trips) {
            List<Fueling> fuelings = refuelingRepository.findByDepartureLog(trip);
            for (Fueling a : fuelings) {
                if (a.getTotalValue() != null) totalGasto = totalGasto.add(a.getTotalValue());
                if (a.getLitersAmount() != null) totalLitros = totalLitros.add(a.getLitersAmount());
            }
        }

        return new MonthlyUsageReportDTO(totalKm, trips.size(), totalGasto, totalLitros, trips);
    }

    public byte[] generateReportFile(Long vehicleId, String format, String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = switch (period.toLowerCase()) {
            case "hoje" -> end.withHour(0).withMinute(0).withSecond(0);
            case "7d" -> end.minusDays(7);
            case "30d" -> end.minusDays(30);
            case "1y" -> end.minusYears(1);
            default -> end.minusDays(30);
        };

        MonthlyUsageReportDTO data = generateMonthlyUsageReportByVehicle(vehicleId, start, end);

        if ("pdf".equalsIgnoreCase(format)) {
            return generatePdfReport(vehicleId, period, start, end, data);
        }

        StringBuilder report = new StringBuilder();
        report.append("RELATORIO DE USO - IPEM CONTROL\n");
        report.append("Viatura ID: ").append(vehicleId).append("\n");
        report.append("Periodo: ").append(period).append(" (").append(start.toLocalDate()).append(" a ").append(end.toLocalDate()).append(")\n");
        report.append("--------------------------------------------------\n");
        report.append("RESUMO GERAL:\n");
        report.append("- Total de Saidas no periodo: ").append(data.getTotalTrips()).append("\n");
        report.append("- Quilometragem total rodada: ").append(data.getTotalMileage()).append(" KM\n");
        report.append("- Consumo total de combustível: ").append(data.getTotalLiters()).append(" Litros\n");
        report.append("- Gasto total com abastecimento: R$ ").append(data.getTotalSpending()).append("\n");
        report.append("--------------------------------------------------\n");
        report.append("DETALHAMENTO DE VIAGENS:\n");

        if (data.getDetails() != null && !data.getDetails().isEmpty()) {
            for (DepartureLog v : data.getDetails()) {
                report.append("Data: ").append(v.getDateTimeDeparture().toLocalDate())
                        .append(" | User: ").append(v.getUser() != null ? v.getUser().getName() : "N/I")
                        .append(" | Destino: ").append(v.getDestination() != null ? v.getDestination() : "N/I")
                        .append(" | KM Rodados: ").append(v.getDrivenKm() != null ? v.getDrivenKm() : "0").append("\n");
            }
        } else {
            report.append("Nenhuma trip registrada para este vehicle no periodo.\n");
        }

        report.append("--------------------------------------------------\n");
        report.append("Gerado em: ").append(LocalDateTime.now()).append("\n");

        return report.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] generatePdfReport(Long vehicleId, String period, LocalDateTime start, LocalDateTime end, MonthlyUsageReportDTO data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("RELATORIO DE USO - IPEM CONTROL").setBold().setFontSize(16));
            document.add(new Paragraph("Viatura ID: " + vehicleId));
            document.add(new Paragraph("Periodo: " + period + " (" + start.toLocalDate() + " a " + end.toLocalDate() + ")"));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("RESUMO GERAL:"));
            document.add(new Paragraph("- Total de Saidas no periodo: " + data.getTotalTrips()));
            document.add(new Paragraph("- Quilometragem total rodada: " + data.getTotalMileage() + " KM"));
            document.add(new Paragraph("- Consumo total de combustível: " + data.getTotalLiters() + " Litros"));
            document.add(new Paragraph("- Gasto total com abastecimento: R$ " + data.getTotalSpending()));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("DETALHAMENTO DE VIAGENS:"));

            if (data.getDetails() != null && !data.getDetails().isEmpty()) {
                for (DepartureLog v : data.getDetails()) {
                    String user = v.getUser() != null ? v.getUser().getName() : "N/I";
                    String dest = v.getDestination() != null ? v.getDestination() : "N/I";
                    String km = v.getDrivenKm() != null ? v.getDrivenKm().toString() : "0";

                    document.add(new Paragraph("Data: " + v.getDateTimeDeparture().toLocalDate() +
                            " | User: " + user +
                            " | Destino: " + dest +
                            " | KM Rodados: " + km));
                }
            } else {
                document.add(new Paragraph("Nenhuma trip registrada para este vehicle no periodo."));
            }

            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Gerado em: " + LocalDateTime.now()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    private DepartureLogResponseDTO toDTO(DepartureLog departureLog) {
        DepartureLogResponseDTO dto = new DepartureLogResponseDTO();
        dto.setDepartureLogId(departureLog.getDepartureLogId());
        dto.setDestination(departureLog.getDestination());
        dto.setStatus(departureLog.getStatus());
        dto.setObservations(departureLog.getObservations());
        dto.setDepartureDateTime(departureLog.getDateTimeDeparture());
        dto.setReturnDate(departureLog.getReturnDate());
        dto.setStartingKm(departureLog.getStartingKm());
        dto.setFinishingKm(departureLog.getFinishingKm());
        dto.setDrivenKm(departureLog.getDrivenKm());
        if (departureLog.getVehicle() != null) {
            dto.setVehicleId(departureLog.getVehicle().getVehicleId());
            dto.setVehiclePrefix(departureLog.getVehicle().getPrefix());
            dto.setVehicleModel(departureLog.getVehicle().getModel());
            dto.setVehicleLicensePlate(departureLog.getVehicle().getLicensePlate());
        }
        if (departureLog.getUser() != null) {
            dto.setUserRegistration(departureLog.getUser().getRegistration());
            dto.setUserName(departureLog.getUser().getName());
        }
        if (departureLog.getServiceType() != null) {
            dto.setServiceTypeId(departureLog.getServiceType().getServiceTypeId());
            dto.setServiceName(departureLog.getServiceType().getServiceName());
        }
        dto.setCreatedAt(departureLog.getCreatedAt());
        dto.setUpdatedAt(departureLog.getUpdatedAt());
        return dto;
    }
}
