package com.estudos.algamoneyapi.repository;

import com.estudos.algamoneyapi.model.Lancamento;
import com.estudos.algamoneyapi.repository.lancamento.LancamentoRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {
}
