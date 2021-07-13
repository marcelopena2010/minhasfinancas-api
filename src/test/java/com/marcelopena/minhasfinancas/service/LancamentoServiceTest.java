package com.marcelopena.minhasfinancas.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.marcelopena.minhasfinancas.exception.RegraNegocioException;
import com.marcelopena.minhasfinancas.model.entity.Lancamento;
import com.marcelopena.minhasfinancas.model.entity.Usuario;
import com.marcelopena.minhasfinancas.model.enums.StatusLancamento;
import com.marcelopena.minhasfinancas.model.repository.LancamentoRepository;
import com.marcelopena.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.marcelopena.minhasfinancas.model.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@SpyBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamento.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class ).when(service).validar(lancamentoASalvar);

		//verificacao
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		//execucao
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execucao
		service.atualizar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		//verificacao
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), RegraNegocioException.class);
				
		//execucao
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		
		//execucao
		service.deletar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		//verificacao
		Assertions.catchThrowableOfType(() -> service.deletar(lancamentoASalvar), NullPointerException.class);
				
		//execucao
		Mockito.verify(repository, Mockito.never()).delete(lancamentoASalvar);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacao
		Assertions
			.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//execucao
		service.atualizarStatus(lancamento, novoStatus);
		
		//verificacoes
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//cenario
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1l);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
	}
}
