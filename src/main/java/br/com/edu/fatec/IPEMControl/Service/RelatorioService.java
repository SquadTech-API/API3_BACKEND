package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AtividadeDiariaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RelatorioDiarioDTO;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Usuario;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
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
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public RelatorioDiarioDTO gerarRelatorioDiarioPorTecnico(Integer matricula, LocalDate data) {

        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));


        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(LocalTime.MAX);


        List<RegistroSaida> saidasDoDia = registroSaidaRepository
                .findByUsuarioMatriculaAndDataHoraSaidaBetween(matricula, inicioDia, fimDia);


        BigDecimal kmTotal = saidasDoDia.stream()
                .map(saida -> saida.getKmRodados() != null ? saida.getKmRodados() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Mapeia as entidades para o AtividadeDiariaDTO
        List<AtividadeDiariaDTO> atividades = saidasDoDia.stream()
                .map(saida -> new AtividadeDiariaDTO(
                        saida.getVeiculo().getPrefixo(),
                        saida.getLocalDestino(),
                        saida.getDataHoraSaida(),
                        saida.getDataRetorno(),
                        saida.getKmRodados(),
                        saida.getStatus()
                ))
                .collect(Collectors.toList());

        return new RelatorioDiarioDTO(
                usuario.getMatricula(),
                usuario.getNome(),
                data,
                atividades.size(),
                kmTotal,
                atividades
        );
    }
}