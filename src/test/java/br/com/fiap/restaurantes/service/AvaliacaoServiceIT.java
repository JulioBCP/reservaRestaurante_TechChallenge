package br.com.fiap.restaurantes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.restaurantes.entities.Avaliacao;
import br.com.fiap.restaurantes.exception.AvaliacaoNotFoundException;
import br.com.fiap.restaurantes.repository.AvaliacaoRepository;
import br.com.fiap.restaurantes.utils.AvaliacaoHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import jakarta.transaction.Transactional;

import java.util.Collection;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AvaliacaoServiceIT {

  @Autowired
  private AvaliacaoRepository avaliacaoRepository;

  @Autowired
  private AvaliacaoService avaliacaoService;

  @Test
  void devePermitirRegistrarAvaliacao() {
    var avaliacao = AvaliacaoHelper.gerarAvaliacao();

    Avaliacao avaliacaoArmazenada = avaliacaoService.criarAvaliacao(avaliacao);

    assertThat(avaliacaoArmazenada)
        .isNotNull()
        .isInstanceOf(Avaliacao.class);
    assertThat(avaliacaoArmazenada.getId())
        .isNotNull();
    assertThat(avaliacaoArmazenada.getDescricao())
        .isNotNull()
        .isNotEmpty()
        .isEqualTo(avaliacao.getDescricao());
    assertThat(avaliacaoArmazenada.getRestaurante())
        .isNotNull()
        .isEqualTo(avaliacao.getRestaurante());
  }

  @Test
  void devePermitirBuscarAvaliacao() {
    var avaliacao = AvaliacaoHelper.registrarAvaliacao(avaliacaoRepository);

    var avaliacaoObtidaOptional = avaliacaoRepository.findById(avaliacao.getId());

    assertThat(avaliacaoObtidaOptional)
        .isPresent()
        .containsSame(avaliacao);
    avaliacaoObtidaOptional.ifPresent(avaliacaoObtida -> {
      assertThat(avaliacaoObtida.getId())
          .isEqualTo(avaliacao.getId());
      assertThat(avaliacaoObtida.getDataAvaliacao())
          .isEqualTo(avaliacao.getDataAvaliacao());
      assertThat(avaliacaoObtida.getCliente())
          .isEqualTo(avaliacao.getCliente());
      assertThat(avaliacaoObtida.getRestaurante())
          .isEqualTo(avaliacao.getRestaurante());
    });
  }

  @Test
  void deveGerarExcecao_QuandoBuscarAvaliacao_IdNaoExistente() {
    var id = 1L;

    assertThatThrownBy(() -> avaliacaoService.buscarAvaliacao(id))
        .isInstanceOf(AvaliacaoNotFoundException.class)
        .hasMessage("avaliacao não encontrada");
  }

  @Test
  void devePermirirAlterarAvaliacao() {
    var avaliacaoOriginal = AvaliacaoHelper.registrarAvaliacao(avaliacaoRepository);
    var avaliacaoModificada = avaliacaoOriginal.toBuilder().build();
    avaliacaoModificada.setDescricao("abcd");

    var avaliacaoObtida = avaliacaoService.alterarAvaliacao(avaliacaoOriginal.getId(),
            avaliacaoModificada);

    assertThat(avaliacaoObtida)
        .isInstanceOf(Avaliacao.class)
        .isNotNull();
    assertThat(avaliacaoObtida.getId())
        .isEqualTo(avaliacaoModificada.getId());
    assertThat(avaliacaoObtida.getDescricao())
        .isEqualTo(avaliacaoModificada.getDescricao());
  }

  @Test
  void devePermitirApagarAvaliacao() {
    var avaliacaoRegistrada = AvaliacaoHelper.registrarAvaliacao(avaliacaoRepository);
    var resultado = avaliacaoService.apagarAvaliacao(avaliacaoRegistrada.getId());
    assertThat(resultado).isTrue();
  }

  @Test
  void devePermitirListarAvaliacoes() {
    Collection<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoes();

    assertThat(avaliacoes).hasSize(3);
  }

  @Test
  @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void devePermitirListarTodasAsAvaliacoes_QuandoNaoExisteRegistro() {
    Collection<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoes();
    assertThat(avaliacoes).isEmpty();
  }

}
