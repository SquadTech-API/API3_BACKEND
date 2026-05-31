package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.DailyActivityDTO;
import br.com.edu.fatec.ipemControl.dto.DailyReportDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ExitRecordRepository exitRecordRepository;
    private final UserRepository userRepository;

    public ReportService(ExitRecordRepository exitRecordRepository,
                         UserRepository userRepository) {
        this.exitRecordRepository = exitRecordRepository;
        this.userRepository = userRepository;
    }

    public DailyReportDTO generateDailyReportByTechnician(Integer registration, LocalDate date) {

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        List<DepartureLog> dailyDepartures = exitRecordRepository
                .findByUserRegistrationAndDateTimeDepartureBetween(registration, dayStart, dayEnd);

        BigDecimal totalKm = dailyDepartures.stream()
                .map(departureLog -> departureLog.getDrivenKm() != null ? departureLog.getDrivenKm() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DailyActivityDTO> activities = dailyDepartures.stream()
                .map(departureLog -> new DailyActivityDTO (
                        departureLog.getVehicle().getPrefix(),
                        departureLog.getDestination(),
                        departureLog.getDateTimeDeparture(),
                        departureLog.getReturnDate(),
                        departureLog.getDrivenKm(),
                        departureLog.getStatus()
                ))
                .collect(Collectors.toList());

        return new DailyReportDTO(
                user.getRegistration(),
                user.getName(),
                date,
                activities.size(),
                totalKm,
                activities
        );
    }
}
