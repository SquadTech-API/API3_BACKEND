package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.UsageHistoryCardDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.Fueling;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsageHistoryService {

    private final ExitRecordRepository exitRecordRepository;
    private final FuelingRepository refuelingRepository;

    public UsageHistoryService(ExitRecordRepository exitRecordRepository,
                               FuelingRepository refuelingRepository) {
        this.exitRecordRepository = exitRecordRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public List<UsageHistoryCardDTO> getUsageHistoryByVehicle(Integer vehicleId) {
        List<DepartureLog> departures = exitRecordRepository
                .findByVehicleVehicleIdAndDateTimeDepartureBetween(
                        vehicleId,
                        java.time.LocalDateTime.now().minusYears(5),
                        java.time.LocalDateTime.now()
                );

        return departures.stream()
                .sorted((a, b) -> {
                    if (a.getDateTimeDeparture() == null) return 1;
                    if (b.getDateTimeDeparture() == null) return -1;
                    return b.getDateTimeDeparture().compareTo(a.getDateTimeDeparture());
                })
                .map(departureLog -> {
                    String driver = departureLog.getUser() != null
                            ? departureLog.getUser().getName() : "-";

                    String serviceType = departureLog.getServiceType() != null
                            ? departureLog.getServiceType().getServiceName() : "-";

                    BigDecimal drivenKm = departureLog.getDrivenKm() != null
                            ? departureLog.getDrivenKm() : BigDecimal.ZERO;

                    List<Fueling> fuelings =
                            refuelingRepository.findByDepartureLog(departureLog);
                    boolean wasFueled = !fuelings.isEmpty();

                    return new UsageHistoryCardDTO(
                            driver,
                            departureLog.getDateTimeDeparture(),
                            serviceType,
                            drivenKm,
                            wasFueled
                    );
                })
                .collect(Collectors.toList());
    }
}
