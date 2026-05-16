package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AtividadeDiariaDTO;
import br.com.edu.fatec.IPEMControl.DTO.DailyReportDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.User;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.ExitRecordRepository;
import br.com.edu.fatec.IPEMControl.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private ExitRecordRepository exitRecordRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public DailyReportDTO gerarRelatorioDiarioPorTecnico(Integer matricula, LocalDate data) {

        User user = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));


        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(LocalTime.MAX);


        List<DepartureLog> saidasDoDia = exitRecordRepository
                .findByUsuarioMatriculaAndDataHoraSaidaBetween(matricula, inicioDia, fimDia);


        BigDecimal kmTotal = saidasDoDia.stream()
                .map(saida -> saida.getDrivenKm() != null ? saida.getDrivenKm() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Mapeia as entidades para o AtividadeDiariaDTO
        List<AtividadeDiariaDTO> atividades = saidasDoDia.stream()
                .map(saida -> new AtividadeDiariaDTO(
                        saida.getVehicle().getPrefix(),
                        saida.getDestination(),
                        saida.getDateTimeDeparture(),
                        saida.getReturnDate(),
                        saida.getDrivenKm(),
                        saida.getStatus()
                ))
                .collect(Collectors.toList());

        return new DailyReportDTO(
                user.getRegistration(),
                user.getName(),
                data,
                atividades.size(),
                kmTotal,
                atividades
        );
    }
}