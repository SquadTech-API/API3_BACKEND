package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.UsageHistoryCardDTO;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageHistoryService {

    private final DepartureLogRepository departureLogRepository;
    private final FuelingRepository fuelingRepository;

    public List<UsageHistoryCardDTO> getUsageHistoryByVehicle(Integer vehicleId) {
        return departureLogRepository
                .findByVehicleIdAndDepartureDatetimeBetween(
                        vehicleId,
                        LocalDateTime.now().minusYears(5),
                        LocalDateTime.now()
                )
                .stream()
                .sorted((a, b) -> {
                    if (a.getDepartureDatetime() == null) return 1;
                    if (b.getDepartureDatetime() == null) return -1;
                    return b.getDepartureDatetime().compareTo(a.getDepartureDatetime());
                })
                .map(d -> {
                    String driver = d.getUser() != null
                            ? d.getUser().getFullName() : "-";

                    String serviceType = d.getServiceType() != null
                            ? d.getServiceType().getServiceName() : "-";

                    BigDecimal drivenMileage = d.getDrivenMileage() != null
                            ? d.getDrivenMileage() : BigDecimal.ZERO;

                    boolean wasFueled = !fuelingRepository.findByDepartureLog(d).isEmpty();

                    return new UsageHistoryCardDTO(
                            driver,
                            d.getDepartureDatetime(),
                            serviceType,
                            drivenMileage,
                            wasFueled
                    );
                })
                .collect(Collectors.toList());
    }
}