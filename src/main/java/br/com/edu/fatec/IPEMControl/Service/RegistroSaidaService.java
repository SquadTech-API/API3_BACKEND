package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.FecharSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RegistroSaidaDTO;
import br.com.edu.fatec.IPEMControl.Entities.*;
import br.com.edu.fatec.IPEMControl.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroSaidaService {

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    public RegistroSaida abrirSaida(RegistroSaidaDTO dto) {

        Veiculo veiculo = veiculoRepository.findById(dto.getIdVeiculo())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(veiculo.getDisponivel())) {
            throw new RuntimeException("Veículo não está disponível.");
        }

        Usuario usuario = usuarioRepository.findByMatricula(dto.getMatriculaUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (Boolean.FALSE.equals(usuario.getColaboradorAtivo())) {
            throw new RuntimeException("Colaborador inativo.");
        }

        TipoServico tipoServico = tipoServicoRepository.findById(dto.getIdTipoServico())
                .orElseThrow(() -> new RuntimeException("Tipo de serviço não encontrado."));

        RegistroSaida registro = new RegistroSaida();
        registro.setVeiculo(veiculo);
        registro.setUsuario(usuario);
        registro.setTipoServico(tipoServico);
        registro.setLocalDestino(dto.getLocalDestino());
        registro.setObservacoes(dto.getObservacoes());
        registro.setKmInicial(dto.getKmInicial());
        registro.setDataHoraSaida(dto.getDataHoraSaida());
        registro.setStatus("em_andamento");

        veiculo.setDisponivel(false);
        veiculoRepository.save(veiculo);

        return registroSaidaRepository.save(registro);
    }

    public RegistroSaida fecharSaida(Integer id, FecharSaidaDTO dto) {

        RegistroSaida registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus())) {
            throw new RuntimeException("Esta saída já foi encerrada.");
        }

        registro.setKmFinal(dto.getKmFinal());
        registro.setDataRetorno(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank()) {
            registro.setObservacoes(dto.getObservacoes());
        }

        Veiculo veiculo = registro.getVeiculo();
        veiculo.setKmAtual(dto.getKmFinal());
        veiculo.setDisponivel(true);
        veiculoRepository.save(veiculo);

        return registroSaidaRepository.save(registro);
    }


    public List<RegistroSaida> listarTodos() {
        return registroSaidaRepository.findAll();
    }


    public RegistroSaida buscarPorId(Integer id) {
        return registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de saída não encontrado."));
    }
}