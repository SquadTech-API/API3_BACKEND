package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.FecharSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RegistroSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoDTO;
import br.com.edu.fatec.IPEMControl.DTO.RetornoRespostaDTO;
import br.com.edu.fatec.IPEMControl.Entities.*;
import br.com.edu.fatec.IPEMControl.Exception.RegraDeNegocioException;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        if (dto.getIdVeiculo() == null)
            throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getMatriculaUsuario() == null)
            throw new RegraDeNegocioException("Informe o usuário.");
        if (dto.getIdTipoServico() == null)
            throw new RegraDeNegocioException("Informe o tipo de serviço.");
        if (dto.getKmInicial() == null)
            throw new RegraDeNegocioException("Informe o KM inicial.");
        if (dto.getKmInicial().compareTo(BigDecimal.ZERO) < 0)
            throw new RegraDeNegocioException("KM inicial não pode ser negativo.");
        if (dto.getDataHoraSaida() == null)
            throw new RegraDeNegocioException("Informe a data e hora de saída.");
        if (dto.getLocalDestino() == null || dto.getLocalDestino().isBlank())
            throw new RegraDeNegocioException("Informe o local de destino.");

        // ── ADICIONADO: impede o usuário de abrir nova saída se já tem uma em andamento
        boolean usuarioJaEmSaida = registroSaidaRepository
                .findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(
                        dto.getMatriculaUsuario(), "em_andamento")
                .isPresent();
        if (usuarioJaEmSaida)
            throw new RegraDeNegocioException(
                    "Você já possui uma saída em andamento. Registre o retorno antes de iniciar uma nova saída.");

        Vehicle vehicle = veiculoRepository.findById(dto.getIdVeiculo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        if (Boolean.FALSE.equals(vehicle.getDisponivel()))
            throw new RegraDeNegocioException("Veículo não está disponível.");

        if (vehicle.getKmAtual() != null &&
                dto.getKmInicial().compareTo(vehicle.getKmAtual()) < 0) {
            throw new RegraDeNegocioException(
                    "KM inicial (" + dto.getKmInicial() + ") não pode ser menor que o KM atual do veículo (" + vehicle.getKmAtual() + ").");
        }

        Usuario usuario = usuarioRepository.findByMatricula(dto.getMatriculaUsuario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (Boolean.FALSE.equals(usuario.getColaboradorAtivo()))
            throw new RegraDeNegocioException("Colaborador inativo.");

        TipoServico tipoServico = tipoServicoRepository.findById(dto.getIdTipoServico())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        RegistroSaida registro = new RegistroSaida();
        registro.setVehicle(vehicle);
        registro.setUsuario(usuario);
        registro.setTipoServico(tipoServico);
        registro.setLocalDestino(dto.getLocalDestino());
        registro.setObservacoes(dto.getObservacoes());
        registro.setKmInicial(dto.getKmInicial());
        registro.setDataHoraSaida(dto.getDataHoraSaida());
        registro.setStatus("em_andamento");

        vehicle.setDisponivel(false);
        veiculoRepository.save(vehicle);

        return registroSaidaRepository.save(registro);
    }

    public RetornoRespostaDTO registrarRetorno(Integer id, RetornoDTO dto) {

        if (dto.getKmFinal() == null)
            throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getDataRetorno() == null)
            throw new RegraDeNegocioException("Informe o horário de chegada.");

        RegistroSaida registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");

        if (dto.getKmFinal().compareTo(registro.getKmInicial()) < 0)
            throw new RegraDeNegocioException(
                    "KM final (" + dto.getKmFinal() + ") não pode ser menor que o KM inicial (" + registro.getKmInicial() + ").");

        if (dto.getDataRetorno().isBefore(registro.getDataHoraSaida()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getKmFinal().subtract(registro.getKmInicial());

        registro.setKmFinal(dto.getKmFinal());
        registro.setKmRodados(kmRodados);
        registro.setDataRetorno(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank())
            registro.setObservacoes(dto.getObservacoes());

        Vehicle vehicle = registro.getVehicle();
        vehicle.setKmAtual(dto.getKmFinal());
        vehicle.setDisponivel(true);
        veiculoRepository.save(vehicle);

        registroSaidaRepository.save(registro);

        return new RetornoRespostaDTO(
                registro.getIdSaida(),
                registro.getStatus(),
                registro.getKmInicial(),
                registro.getKmFinal(),
                kmRodados,
                registro.getDataHoraSaida(),
                registro.getDataRetorno(),
                vehicle.getModelo(),
                vehicle.getPrefixo(),
                registro.getUsuario().getNome()
        );
    }

    public RegistroSaida fecharSaida(Integer id, FecharSaidaDTO dto) {

        RegistroSaida registro = registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));

        if (!"em_andamento".equalsIgnoreCase(registro.getStatus()))
            throw new RegraDeNegocioException("Esta saída já foi encerrada.");
        if (dto.getKmFinal() == null)
            throw new RegraDeNegocioException("Informe o KM final.");
        if (dto.getDataRetorno() == null)
            throw new RegraDeNegocioException("Informe o horário de chegada.");
        if (dto.getKmFinal().compareTo(registro.getKmInicial()) < 0)
            throw new RegraDeNegocioException("KM final não pode ser menor que o KM inicial.");
        if (dto.getDataRetorno().isBefore(registro.getDataHoraSaida()))
            throw new RegraDeNegocioException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal kmRodados = dto.getKmFinal().subtract(registro.getKmInicial());

        registro.setKmFinal(dto.getKmFinal());
        registro.setKmRodados(kmRodados);
        registro.setDataRetorno(dto.getDataRetorno());
        registro.setStatus("concluido");

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank())
            registro.setObservacoes(dto.getObservacoes());

        Vehicle vehicle = registro.getVehicle();
        vehicle.setKmAtual(dto.getKmFinal());
        vehicle.setDisponivel(true);
        veiculoRepository.save(vehicle);

        return registroSaidaRepository.save(registro);
    }

    public List<RegistroSaida> listarTodos() {
        return registroSaidaRepository.findAll();
    }

    public RegistroSaida buscarPorId(Integer id) {
        return registroSaidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saída não encontrado."));
    }
}