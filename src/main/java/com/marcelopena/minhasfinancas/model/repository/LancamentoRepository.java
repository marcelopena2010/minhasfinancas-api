package com.marcelopena.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcelopena.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	
	
}
