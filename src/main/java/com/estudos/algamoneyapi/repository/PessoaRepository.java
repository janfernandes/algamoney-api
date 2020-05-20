package com.estudos.algamoneyapi.repository;

import com.estudos.algamoneyapi.model.Pessoa;
import com.estudos.algamoneyapi.repository.pessoa.PessoaRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepositoryQuery {
}
