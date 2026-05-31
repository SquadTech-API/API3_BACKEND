package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.DailyActivityDTO;
import br.com.edu.fatec.ipemControl.dto.DailyReportDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DepartureLogRepository departureLogRepository;
    private final UserRepository userRepository;

    public DailyReportDTO generateDailyReportByTechnician(Integer registration, LocalDate date) {

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(LocalTime.MAX);

        List<DepartureLog> dailyDepartures = departureLogRepository
                .findByUserRegistrationAndDepartureDatetimeBetween(registration, dayStart, dayEnd);

        BigDecimal totalKm = dailyDepartures.stream()
                .map(d -> d.getDrivenMileage() != null ? d.getDrivenMileage() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DailyActivityDTO> activities = dailyDepartures.stream()
                .map(d -> new DailyActivityDTO(
                        d.getVehicle().getPrefix(),
                        d.getDestination(),
                        d.getDepartureDatetime(),
                        d.getReturnDatetime(),
                        d.getDrivenMileage(),
                        d.getStatus()
                ))
                .collect(Collectors.toList());

        return new DailyReportDTO(
                user.getRegistration(),
                user.getFullName(),
                date,
                activities.size(),
                totalKm,
                activities
        );
    }
}